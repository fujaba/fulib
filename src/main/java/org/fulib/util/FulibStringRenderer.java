package org.fulib.util;

import org.stringtemplate.v4.StringRenderer;

import java.util.Locale;

public class FulibStringRenderer extends StringRenderer
{

   @Override
   public String toString(String value, String formatString, Locale locale)
   {
      if ("upper_snake".equals(formatString))
      {
         return value.replaceAll("([a-z0-9])([A-Z]+)", "$1_$2").toUpperCase(locale);
      }
      else
      {
         return super.toString(value, formatString, locale);
      }
   }
}
