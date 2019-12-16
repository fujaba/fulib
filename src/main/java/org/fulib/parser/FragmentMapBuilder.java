package org.fulib.parser;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.fulib.Parser;
import org.fulib.classmodel.CodeFragment;
import org.fulib.classmodel.FileFragmentMap;

import java.io.IOException;

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

      final FulibClassParser.FileContext context = parser.file();

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
   public void enterPackageDecl(FulibClassParser.PackageDeclContext ctx)
   {
      this.addCodeFragment(Parser.PACKAGE, ctx);
   }

   @Override
   public void enterImportDecl(FulibClassParser.ImportDeclContext ctx)
   {
      String typeName = ctx.qualifiedName().getText();
      if (ctx.STAR() != null)
      {
         typeName += ".*";
      }

      this.addCodeFragment(Parser.IMPORT + ":" + typeName, ctx);
   }

   @Override
   public void enterClassDecl(FulibClassParser.ClassDeclContext ctx)
   {
      this.addCodeFragment(Parser.CLASS, ctx.getStart().getStartIndex(),
                           ctx.classMember().classBody().LBRACE().getSymbol().getStopIndex());
   }

   @Override
   public void exitClassDecl(FulibClassParser.ClassDeclContext ctx)
   {
      this.addCodeFragment(Parser.CLASS_END, ctx.classMember().classBody().RBRACE());
   }
}
