package org.fulib.util;

import org.stringtemplate.v4.StringRenderer;

import java.util.Locale;

public class FulibStringRenderer extends StringRenderer
{

   @Override
   public String toString(String value, String formatString, Locale locale)
   {
      if (formatString == null)
      {
         return value;
      }
      else if (formatString.equals("upper_snake"))
      {
         return value.replaceAll("([a-z0-9])([A-Z]+)", "$1_$2").toUpperCase(locale);
      }
      else
      {
         return super.toString(value, formatString, locale);
      }
   }
}
