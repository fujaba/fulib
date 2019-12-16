package org.fulib.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.fulib.Parser;
import org.fulib.classmodel.CodeFragment;
import org.fulib.classmodel.FileFragmentMap;

import java.io.IOException;
import java.util.List;

import static org.fulib.parser.FulibClassParser.*;

public class FragmentMapBuilder extends FulibClassBaseListener
{
   // =============== Fields ===============

   private final CharStream      input;
   private final FileFragmentMap map;

   private int lastFragmentEndPos = -1;

   // =============== Constructors ===============

   public FragmentMapBuilder(CharStream input, FileFragmentMap map)
   {
      this.input = input;
      this.map = map;
   }

   // =============== Static Methods ===============

   public static FileFragmentMap parse(String fileName) throws IOException
   {
      final CharStream input = CharStreams.fromFileName(fileName);

      final FulibClassLexer lexer = new FulibClassLexer(input);
      final FulibClassParser parser = new FulibClassParser(new CommonTokenStream(lexer));

      final FileContext context = parser.file();

      final FileFragmentMap map = new FileFragmentMap(fileName);
      final FragmentMapBuilder builder = new FragmentMapBuilder(input, map);
      ParseTreeWalker.DEFAULT.walk(builder, context);
      return map;
   }

   // =============== Methods ===============

   private void addCodeFragment(String key, ParserRuleContext ctx)
   {
      this.addCodeFragment(key, ctx.getStart().getStartIndex(), ctx.getStop().getStopIndex());
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
      if (startPos - this.lastFragmentEndPos > 1)
      {
         final String gapText = this.input.getText(Interval.of(this.lastFragmentEndPos + 1, startPos - 1));
         final CodeFragment gap = new CodeFragment().setKey(Parser.GAP).setText(gapText);
         this.map.add(gap);
      }

      final String text = this.input.getText(pos);
      final CodeFragment codeFragment = new CodeFragment().setKey(key).setText(text);
      this.map.add(codeFragment);

      this.lastFragmentEndPos = endPos;
   }

   @Override
   public void enterPackageDecl(PackageDeclContext ctx)
   {
      this.addCodeFragment(Parser.PACKAGE, ctx);
   }

   @Override
   public void enterImportDecl(ImportDeclContext ctx)
   {
      String typeName = ctx.qualifiedName().getText();
      if (ctx.STAR() != null)
      {
         typeName += ".*";
      }

      this.addCodeFragment(Parser.IMPORT + ":" + typeName, ctx);
   }

   @Override
   public void enterClassDecl(ClassDeclContext ctx)
   {
      this.addCodeFragment(Parser.CLASS, ctx.getStart().getStartIndex(),
                           ctx.classMember().classBody().LBRACE().getSymbol().getStopIndex());
   }

   @Override
   public void enterFieldMember(FieldMemberContext ctx)
   {
      final MemberContext memberCtx = (MemberContext) ctx.parent;
      final String fieldName = ctx.IDENTIFIER().getText();
      this.addCodeFragment(Parser.ATTRIBUTE + ":" + fieldName, memberCtx);
   }

   @Override
   public void enterConstructorMember(ConstructorMemberContext ctx)
   {
      final MemberContext memberCtx = (MemberContext) ctx.parent;
      final String className = "\"FOO\"";

      final StringBuilder signature = new StringBuilder();
      signature.append(Parser.CONSTRUCTOR);
      signature.append(':');
      signature.append(className);
      writeParams(signature, ctx.parameterList());

      this.addCodeFragment(signature.toString(), memberCtx);
   }

   @Override
   public void enterMethodMember(MethodMemberContext ctx)
   {
      final MemberContext memberCtx = (MemberContext) ctx.parent;
      final String methodName = ctx.IDENTIFIER().getText();

      final StringBuilder signature = new StringBuilder();
      signature.append(Parser.METHOD);
      signature.append(':');
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
      final String baseType =
         typeCtx.primitiveType() != null ? getType(typeCtx.primitiveType()) : getType(typeCtx.referenceType());

      builder.append(baseType);

      final int arrayDimensions = typeCtx.arraySuffix().size();
      for (int i = 0; i < arrayDimensions; i++)
      {
         builder.append("[]");
      }
   }

   private static String getType(PrimitiveTypeContext primitiveTypeCtx)
   {
      return primitiveTypeCtx.getText();
   }

   private static String getType(ReferenceTypeContext referenceTypeCtx)
   {
      return referenceTypeCtx.getText();
   }

   @Override
   public void exitClassDecl(ClassDeclContext ctx)
   {
      this.addCodeFragment(Parser.CLASS_END, ctx.classMember().classBody().RBRACE());
   }
}
