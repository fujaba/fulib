package org.fulib.parser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.fulib.classmodel.FileFragmentMap;

import java.io.IOException;

public class FragmentMapBuilder extends FulibClassBaseListener
{
   // =============== Fields ===============

   private final FileFragmentMap map;

   // =============== Constructors ===============

   public FragmentMapBuilder(FileFragmentMap map)
   {
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
      final FragmentMapBuilder builder = new FragmentMapBuilder(map);
      ParseTreeWalker.DEFAULT.walk(builder, context);
      return map;
   }

   // =============== Methods ===============
}
