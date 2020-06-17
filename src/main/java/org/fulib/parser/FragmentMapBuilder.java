package org.fulib.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.fulib.classmodel.CodeFragment;
import org.fulib.classmodel.FileFragmentMap;
import org.fulib.parser.FulibClassParser.*;

import java.io.IOException;
import java.util.List;

import static org.fulib.classmodel.FileFragmentMap.CLASS;
import static org.fulib.classmodel.FileFragmentMap.EOF;
import static org.fulib.classmodel.FileFragmentMap.IMPORT;
import static org.fulib.classmodel.FileFragmentMap.PACKAGE;
import static org.fulib.classmodel.FileFragmentMap.*;

/**
 * @since 1.2
 */
public class FragmentMapBuilder extends FulibClassBaseListener
{
   // =============== Fields ===============

   private final CharStream input;
   private final FileFragmentMap map;

   private int lastFragmentEndPos = -1;
   private String className;

   // =============== Constructors ===============

   public FragmentMapBuilder(CharStream input, FileFragmentMap map)
   {
      this.input = input;
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
         // TODO better error handling
         // e.printStackTrace();
         return new FileFragmentMap(fileName);
      }

      final FulibClassLexer lexer = new FulibClassLexer(input);
      final FulibClassParser parser = new FulibClassParser(new CommonTokenStream(lexer));
      parser.removeErrorListeners();
      parser.addErrorListener(new FulibErrorHandler(System.err));

      final FileContext context = parser.file();

      final FileFragmentMap map = new FileFragmentMap(fileName);
      final FragmentMapBuilder builder = new FragmentMapBuilder(input, map);
      ParseTreeWalker.DEFAULT.walk(builder, context);
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
      this.addCodeFragment(CLASS + '/' + this.className + '/' + CLASS_DECL, ctx.getStart().getStartIndex(),
                           classMemberCtx.classBody().LBRACE().getSymbol().getStopIndex());
   }

   @Override
   public void enterFieldMember(FieldMemberContext ctx)
   {
      final MemberContext memberCtx = (MemberContext) ctx.parent;
      final List<FieldNamePartContext> nameParts = ctx.fieldNamePart();
      final int size = nameParts.size();

      final String firstName = nameParts.get(0).IDENTIFIER().getText();
      if (size == 1)
      {
         // only one field, straightforward (pass the whole ctx)
         this.addCodeFragment(CLASS + '/' + this.className + '/' + ATTRIBUTE + '/' + firstName, memberCtx);
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
      this.addCodeFragment(CLASS + '/' + this.className + '/' + ATTRIBUTE + '/' + firstName, memberCtx.getStart(),
                           commas.get(0).getSymbol());

      // all but the first and last part range from name to comma
      for (int i = 1; i < size - 1; i++)
      {
         final FieldNamePartContext namePart = nameParts.get(i);
         this.addCodeFragment(CLASS + '/' + this.className + '/' + ATTRIBUTE + '/' + namePart.IDENTIFIER().getText(),
                              namePart.getStart(), commas.get(i).getSymbol());
      }

      // last part includes semicolon
      final FieldNamePartContext lastPart = nameParts.get(size - 1);
      this.addCodeFragment(CLASS + '/' + this.className + '/' + ATTRIBUTE + '/' + lastPart.IDENTIFIER().getText(),
                           lastPart.getStart(), memberCtx.getStop());
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

      this.addCodeFragment(signature.toString(), memberCtx);
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
      signature.append(METHOD);
      signature.append('/');
      signature.append(methodName);
      writeParams(signature, ctx.parameterList());

      this.addCodeFragment(signature.toString(), memberCtx);
   }

   private static void writeParams(StringBuilder signature, ParameterListContext paramsCtx)
   {
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
      if (paramCtx.ELLIPSIS() != null)
      {
         builder.append("...");
      }
   }

   private static void writeType(TypeContext typeCtx, StringBuilder builder)
   {
      if (typeCtx.primitiveType() != null)
      {
         writeType(typeCtx.primitiveType(), builder);
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

   private static void writeType(ReferenceTypePartContext referenceTypePartCtx, StringBuilder builder)
   {
      builder.append(referenceTypePartCtx.IDENTIFIER().getText());

      final TypeArgListContext typeArgListCtx = referenceTypePartCtx.typeArgList();
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
      // make sure the method/ and attribute/ sections get created by adding dummies
      this.map.append(
         new CodeFragment().setKey(CLASS + '/' + this.className + '/' + ATTRIBUTE + '/' + "#start").setText(""));
      this.map.append(
         new CodeFragment().setKey(CLASS + '/' + this.className + '/' + METHOD + '/' + "#start").setText(""));
      this.addCodeFragment(CLASS + '/' + this.className + '/' + CLASS_END, ctx.classMember().classBody().RBRACE());
   }

   @Override
   public void exitFile(FileContext ctx)
   {
      // TODO this adds two gaps. Not sure if FileFragmentMap can handle only one, so leaving it at that for now.
      this.addCodeFragment(EOF, this.lastFragmentEndPos + 1, this.input.size());
   }
}
