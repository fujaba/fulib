package org.fulib.parser;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import java.io.PrintStream;

public class FulibErrorHandler extends BaseErrorListener
{
   private final PrintStream out;

   public FulibErrorHandler(PrintStream out)
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
      this.out.println(file + ":" + line + ":" + column + ": " + type + ": " + msg);
   }
}
