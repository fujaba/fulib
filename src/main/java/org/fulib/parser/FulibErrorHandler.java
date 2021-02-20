package org.fulib.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.IOException;

public class FulibErrorHandler extends BaseErrorListener
{
   private final Appendable out;

   public FulibErrorHandler(Appendable out)
   {
      this.out = out;
   }

   @Override
   public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
      String msg, RecognitionException e)
   {
      final String sourceName = recognizer.getInputStream().getSourceName();
      this.report(sourceName, line, charPositionInLine, "syntax", msg);
   }

   private void report(String file, int line, int column, String type, String msg)
   {
      try
      {
         this.out
            .append(file)
            .append(':')
            .append(String.valueOf(line))
            .append(':')
            .append(String.valueOf(column))
            .append(": ")
            .append(type)
            .append(": ")
            .append(msg)
            .append('\n');
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
