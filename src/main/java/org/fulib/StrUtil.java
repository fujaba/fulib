package org.fulib;

import java.util.Objects;

public class StrUtil
{
   public static String cap(String oldTxt)
   {
      if (isEmpty(oldTxt)) return "";

      return oldTxt.substring(0,1).toUpperCase() + oldTxt.substring(1);
   }

   public static boolean isEmpty(String txt)
   {
      return txt == null || txt.trim().length() == 0;
   }

   public static boolean stringEquals(String word1, String word2)
   {
      return word1 == null ? word2 == null : word1.equals(word2);
   }

   public static String downFirstChar(String oldTxt)
   {
      Objects.requireNonNull(oldTxt);
      return oldTxt.substring(0,1).toLowerCase() + oldTxt.substring(1);
   }
}
