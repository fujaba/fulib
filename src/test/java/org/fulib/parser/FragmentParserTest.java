package org.fulib.parser;

import org.antlr.v4.runtime.CharStreams;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

public class FragmentParserTest
{
   private static final String[] CLASS_NAMES = {
      "AllInOneJava7",
      "AllInOneJava8",
      "IdentifierTest",
      "TryWithResourceDemo",
   };

   @Test
   public void test() throws IOException
   {
      for (String className : CLASS_NAMES)
      {
         final String fileName = className + ".java";
         try (final InputStream inputStream = this.getClass().getResourceAsStream(fileName))
         {
            FragmentMapBuilder.parse(fileName, CharStreams.fromStream(inputStream));
         }
      }
   }
}
