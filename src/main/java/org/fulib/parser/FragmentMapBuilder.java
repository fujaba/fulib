package org.fulib.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
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

   private void addCodeFragment(String key, int startPos, int endPos)
   {
      if (startPos - this.lastFragmentEndPos > 1)
      {
         final String gapText = this.input.getText(Interval.of(this.lastFragmentEndPos + 1, startPos - 1));
         final CodeFragment gap = new CodeFragment().setKey(Parser.GAP).setText(gapText);
         this.map.add(gap);
      }

      final String text = this.input.getText(Interval.of(startPos, endPos));
      CodeFragment codeFragment = new CodeFragment().setKey(key).setText(text);
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
}
