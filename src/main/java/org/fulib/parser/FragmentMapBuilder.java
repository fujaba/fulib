package org.fulib.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.fulib.classmodel.CodeFragment;
import org.fulib.classmodel.FileFragmentMap;
import org.fulib.parser.FulibClassParser.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static org.fulib.classmodel.FileFragmentMap.CLASS;
import static org.fulib.classmodel.FileFragmentMap.EOF;
import static org.fulib.classmodel.FileFragmentMap.IMPORT;
import static org.fulib.classmodel.FileFragmentMap.PACKAGE;
import static org.fulib.classmodel.FileFragmentMap.*;
import static org.fulib.util.Validator.isProperty;

/**
 * @since 1.2
 */
public class FragmentMapBuilder extends FulibClassBaseListener
{
   // =============== Fields ===============

   private final CharStream input;
   private final CommonTokenStream tokenStream;
   private final FileFragmentMap map;

   private int lastFragmentEndPos = -1;
   private String className;

   // =============== Constructors ===============

   /**
    * @param input
    *    the character input
    * @param map
    *    the fragment map that should be populated
    *
    * @deprecated since 1.3; for internal use only - use one of the public static methods
    */
   @Deprecated
   public FragmentMapBuilder(CharStream input, FileFragmentMap map)
   {
      this(input, null, map);
   }

   private FragmentMapBuilder(CharStream input, CommonTokenStream tokenStream, FileFragmentMap map)
   {
      this.input = input;
      this.tokenStream = tokenStream;
      this.map = map;
   }

   // =============== Static Methods ===============

   public static FileFragmentMap parse(String fileName)
   {
      final CharStream input;
      try
      {
         input = CharStreams.fromFileName(fileName);
      }
      catch (IOException e)
      {
         throw new IllegalArgumentException("failed to read: " + fileName, e);
      }

      return parse(fileName, input);
   }

   public static FileFragmentMap parse(String fileName, CharStream input)
   {
      final FulibClassLexer lexer = new FulibClassLexer(input);
      final CommonTokenStream tokenStream = new CommonTokenStream(lexer);
      final FulibClassParser parser = new FulibClassParser(tokenStream);
      parser.removeErrorListeners();

      final StringWriter writer = new StringWriter();
      parser.addErrorListener(new FulibErrorHandler(new PrintWriter(writer)));

      final FileContext context = parser.file();

      final FileFragmentMap map = new FileFragmentMap(fileName);
      final FragmentMapBuilder builder = new FragmentMapBuilder(input, tokenStream, map);
      ParseTreeWalker.DEFAULT.walk(builder, context);

      final String errors = writer.toString();
      if (!errors.isEmpty())
      {
         throw new IllegalArgumentException(fileName + " contained syntax errors, aborting:\n" + errors);
      }

      return map;
   }

   // =============== Methods ===============

   private void addCodeFragment(String key, ParserRuleContext ctx)
   {
      this.addCodeFragment(key, ctx.getStart(), ctx.getStop());
   }

   private void addCodeFragment(String key, Token start, Token stop)
   {
      this.addCodeFragment(key, start.getStartIndex(), stop.getStopIndex());
   }

   private void addCodeFragment(String key, TerminalNode node)
   {
      this.addCodeFragment(key, node.getSymbol());
   }

   private void addCodeFragment(String key, Token symbol)
   {
      this.addCodeFragment(key, symbol.getStartIndex(), symbol.getStopIndex());
   }

   private void addCodeFragment(String key, int startPos, int endPos)
   {
      this.addCodeFragment(key, Interval.of(startPos, endPos));
   }

   private void addCodeFragment(String key, Interval pos)
   {
      final int startPos = pos.a;
      final int endPos = pos.b;
      // TODO skip empty gaps
      // if (startPos - this.lastFragmentEndPos > 1)
      {
         final String gapText = this.input.getText(Interval.of(this.lastFragmentEndPos + 1, startPos - 1));
         final CodeFragment gap = new CodeFragment().setKey(key + "#gap-before").setText(gapText);
         this.map.append(gap);
      }

      final String text = this.input.getText(pos);
      final CodeFragment codeFragment = new CodeFragment().setKey(key).setText(text);
      this.map.append(codeFragment);

      this.lastFragmentEndPos = endPos;
   }

   @Override
   public void enterPackageDecl(PackageDeclContext ctx)
   {
      this.addCodeFragment(PACKAGE, ctx);
      // make sure the import/ section gets created by adding a dummy
      this.map.append(new CodeFragment().setKey(IMPORT + "/#start").setText(""));
   }

   @Override
   public void enterImportDecl(ImportDeclContext ctx)
   {
      String typeName = ctx.qualifiedName().getText();
      if (ctx.STAR() != null)
      {
         typeName += ".*";
      }

      this.addCodeFragment(IMPORT + '/' + typeName, ctx);
   }

   @Override
   public void enterClassDecl(ClassDeclContext ctx)
   {
      final ClassMemberContext classMemberCtx = ctx.classMember();
      this.className = classMemberCtx.IDENTIFIER().getText();

      final Token start = this.getStartOrJavaDoc(ctx);
      final Token stop = classMemberCtx.classBody().LBRACE().getSymbol();
      this.addCodeFragment(CLASS + '/' + this.className + '/' + CLASS_DECL, start, stop);
   }

   @Override
   public void enterFieldMember(FieldMemberContext ctx)
   {
      final MemberContext memberCtx = (MemberContext) ctx.parent;
      final List<FieldNamePartContext> nameParts = ctx.fieldNamePart();
      final int size = nameParts.size();

      final boolean isStatic = memberCtx.modifier().stream().anyMatch(m -> m.STATIC() != null);
      final String kind = isStatic ? STATIC_ATTRIBUTE : ATTRIBUTE;

      final String firstName = nameParts.get(0).IDENTIFIER().getText();
      if (size == 1)
      {
         // only one field, straightforward (pass the whole ctx)
         final Token start = this.getStartOrJavaDoc(memberCtx);
         final Token stop = memberCtx.getStop();
         this.addCodeFragment(CLASS + '/' + this.className + '/' + kind + '/' + firstName, start, stop);
         return;
      }

      // multiple fields in one declaration, e.g. int i = 0, j = 1, k = 2;
      // encode as:
      // 1) 'int i = 0,'
      // gap: ' '
      // 2) 'j = 1,'
      // gap: ' '
      // 3) 'k = 2;'
      // we need to handle the case "ending with ," for 1)
      // and "starting with ," for 2) and 3) when merging in FileFragmentMap

      final List<TerminalNode> commas = ctx.COMMA();

      // first part includes type and annotations and first comma
      this.addCodeFragment(CLASS + '/' + this.className + '/' + kind + '/' + firstName,
                           this.getStartOrJavaDoc(memberCtx), commas.get(0).getSymbol());

      // all but the first and last part range from name to comma
      for (int i = 1; i < size - 1; i++)
      {
         final FieldNamePartContext namePart = nameParts.get(i);
         this.addCodeFragment(CLASS + '/' + this.className + '/' + kind + '/' + namePart.IDENTIFIER().getText(),
                              this.getStartOrJavaDoc(namePart), commas.get(i).getSymbol());
      }

      // last part includes semicolon
      final FieldNamePartContext lastPart = nameParts.get(size - 1);
      this.addCodeFragment(CLASS + '/' + this.className + '/' + kind + '/' + lastPart.IDENTIFIER().getText(),
                           this.getStartOrJavaDoc(lastPart), memberCtx.getStop());
   }

   @Override
   public void enterConstructorMember(ConstructorMemberContext ctx)
   {
      final MemberContext memberCtx = (MemberContext) ctx.parent;

      final StringBuilder signature = new StringBuilder();
      signature.append(CLASS);
      signature.append('/');
      signature.append(this.className);
      signature.append('/');
      signature.append(CONSTRUCTOR);
      signature.append('/');
      signature.append(this.className);
      writeParams(signature, ctx.parameterList());

      final Token start = this.getStartOrJavaDoc(memberCtx);
      this.addCodeFragment(signature.toString(), start, memberCtx.getStop());
   }

   @Override
   public void enterMethodMember(MethodMemberContext ctx)
   {
      final MemberContext memberCtx = (MemberContext) ctx.parent;
      final String methodName = ctx.IDENTIFIER().getText();

      final StringBuilder signature = new StringBuilder();

      signature.append(CLASS);
      signature.append('/');
      signature.append(this.className);
      signature.append('/');
      final int parameterCount = (int) ctx.parameterList().parameter().stream().filter(p -> p.THIS() == null).count();
      signature.append(isProperty(methodName, parameterCount) ? PROPERTY : METHOD);
      signature.append('/');
      signature.append(methodName);
      writeParams(signature, ctx.parameterList());

      final Token start = this.getStartOrJavaDoc(memberCtx);
      this.addCodeFragment(signature.toString(), start, memberCtx.getStop());
   }

   private Token getStartOrJavaDoc(ParserRuleContext memberCtx)
   {
      final Token start = memberCtx.getStart();

      // TODO remove if statement when removing the public constructor in v2
      if (this.tokenStream == null)
      {
         // for compatibility, the constructor without the tokenStream parameter is still available.
         return start;
      }

      for (int i = start.getTokenIndex() - 1; i >= 0; i--)
      {
         final Token prev = this.tokenStream.get(i);
         switch (prev.getChannel())
         {
         case 2: // TODO FulibClassLexer.COMMENT
            // regular comments between the JavaDoc comment and the declaration are ok
            continue;
         case 3: // TODO FulibClassLexer.JAVADOC
            return prev;
         default:
            // anything other than a comment indicates that there is no JavaDoc comment for this declaration
            return start;
         }
      }
      return start;
   }

   public static String getParamsSignature(ParameterListContext paramsCtx)
   {
      if (paramsCtx == null)
      {
         return "()";
      }

      final StringBuilder builder = new StringBuilder();
      writeParams(builder, paramsCtx);
      return builder.toString();
   }

   private static void writeParams(StringBuilder signature, ParameterListContext paramsCtx)
   {
      if (paramsCtx == null)
      {
         signature.append("()");
         return;
      }

      signature.append('(');
      for (final ParameterContext paramCtx : paramsCtx.parameter())
      {
         writeType(paramCtx, signature);
         signature.append(',');
      }

      final int lastIndex = signature.length() - 1;
      if (signature.charAt(lastIndex) == ',')
      {
         signature.setCharAt(lastIndex, ')');
      }
      else
      {
         signature.append(')');
      }
   }

   private static void writeType(ParameterContext paramCtx, StringBuilder builder)
   {
      if (paramCtx.THIS() != null)
      {
         // don't include annotated receiver type in signature
         return;
      }
      writeType(paramCtx.type(), builder);
      for (int arrayDimensions = paramCtx.arraySuffix().size(); arrayDimensions > 0; arrayDimensions--)
      {
         builder.append("[]");
      }
      if (paramCtx.ELLIPSIS() != null)
      {
         builder.append("...");
      }
   }

   public static String getTypeSignature(TypeContext typeCtx)
   {
      final StringBuilder builder = new StringBuilder();
      writeType(typeCtx, builder);
      return builder.toString();
   }

   private static void writeType(TypeContext typeCtx, StringBuilder builder)
   {
      if (typeCtx.primitiveType() != null)
      {
         writeType(typeCtx.primitiveType(), builder);
      }
      else if (typeCtx.importType() != null)
      {
         writeType(typeCtx.importType(), builder);
      }
      else
      {
         writeType(typeCtx.referenceType(), builder);
      }

      final int arrayDimensions = typeCtx.arraySuffix().size();
      // noinspection StringRepeatCanBeUsed // for JDK 8 compatibility
      for (int i = 0; i < arrayDimensions; i++)
      {
         builder.append("[]");
      }
   }

   private static void writeType(PrimitiveTypeContext primitiveTypeCtx, StringBuilder builder)
   {
      builder.append(primitiveTypeCtx.getText());
   }

   private static void writeType(ReferenceTypeContext referenceTypeCtx, StringBuilder builder)
   {
      final List<ReferenceTypePartContext> parts = referenceTypeCtx.referenceTypePart();

      writeType(parts.get(0), builder);
      for (int i = 1; i < parts.size(); i++)
      {
         builder.append('.');
         writeType(parts.get(i), builder);
      }
   }

   private static void writeType(ImportTypeContext importTypeCtx, StringBuilder builder)
   {
      // import(org.example.Foo) ends up as only Foo in the code, so the signature should also use the simple name
      final List<TerminalNode> identifiers = importTypeCtx.importTypeName().qualifiedName().IDENTIFIER();
      builder.append(identifiers.get(identifiers.size() - 1).getText());
      writeTypeArgs(importTypeCtx.typeArgList(), builder);
   }

   private static void writeType(ReferenceTypePartContext referenceTypePartCtx, StringBuilder builder)
   {
      builder.append(referenceTypePartCtx.IDENTIFIER().getText());
      writeTypeArgs(referenceTypePartCtx.typeArgList(), builder);
   }

   private static void writeTypeArgs(TypeArgListContext typeArgListCtx, StringBuilder builder)
   {
      if (typeArgListCtx == null)
      {
         return;
      }

      final List<TypeArgContext> typeArgCtxs = typeArgListCtx.typeArg();

      builder.append('<');
      writeType(typeArgCtxs.get(0), builder);
      for (int i = 1; i < typeArgCtxs.size(); i++)
      {
         builder.append(',');
         writeType(typeArgCtxs.get(i), builder);
      }
      builder.append('>');
   }

   private static void writeType(TypeArgContext typeArgCtx, StringBuilder builder)
   {
      final TypeContext type = typeArgCtx.type();
      if (type != null)
      {
         writeType(type, builder);
         return;
      }

      final AnnotatedTypeContext annotatedType = typeArgCtx.annotatedType();
      if (annotatedType == null)
      {
         builder.append('?');
         return;
      }

      builder.append(typeArgCtx.EXTENDS() != null ? "? extends " : "? super ");
      writeType(annotatedType.type(), builder);
   }

   @Override
   public void exitClassDecl(ClassDeclContext ctx)
   {
      // make sure the major sections exist by adding dummies
      this.map.insert(
         new CodeFragment().setKey(CLASS + '/' + this.className + '/' + STATIC_ATTRIBUTE + '/' + "#start").setText(""));
      this.map.insert(
         new CodeFragment().setKey(CLASS + '/' + this.className + '/' + ATTRIBUTE + '/' + "#start").setText(""));
      this.map.insert(
         new CodeFragment().setKey(CLASS + '/' + this.className + '/' + PROPERTY + '/' + "#start").setText(""));
      this.map.insert(
         new CodeFragment().setKey(CLASS + '/' + this.className + '/' + METHOD + '/' + "#start").setText(""));
      this.addCodeFragment(CLASS + '/' + this.className + '/' + CLASS_END, ctx.classMember().classBody().RBRACE());
   }

   @Override
   public void exitFile(FileContext ctx)
   {
      this.addCodeFragment(EOF, this.input.size(), this.input.size());
   }
}
