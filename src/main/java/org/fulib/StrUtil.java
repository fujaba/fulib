package org.fulib;

import java.util.Objects;

public class StrUtil
{
   /**
    * Checks whether the given string is null, empty, or only consists of whitespace.
    *
    * @param string
    *    the string
    *
    * @return true if the given string is null, empty, or only consists of whitespace, false otherwise
    *
    * @deprecated since 1.2
    */
   @Deprecated
   public static boolean isEmpty(String string)
   {
      return string == null || string.trim().isEmpty();
   }

   /**
    * Checks if the two strings are equal, handling nulls correctly.
    *
    * @param a
    *    the first string
    * @param b
    *    the second string
    *
    * @return true if the two strings are equal or both null, false otherwise
    *
    * @deprecated since 1.2; use {@link Objects#equals(Object, Object)} instead
    */
   @Deprecated
   public static boolean stringEquals(String a, String b)
   {
      return Objects.equals(a, b);
   }

   /**
    * Creates a new string by removing the last n characters from the given string.
    *
    * @param string
    *    the string
    * @param n
    *    the number of characters to remove from the end
    *
    * @return the new string
    *
    * @deprecated since 1.2
    */
   @Deprecated
   public static String truncate(String string, int n)
   {
      string = string.substring(0, string.length() - n);
      return string;
   }

   /**
    * Creates a new string by converting the first character of the given string to lowercase.
    * If the given string is empty, the empty string is returned.
    *
    * @param string
    *    the string
    *
    * @return the new string
    */
   public static String cap(String string)
   {
      if (string.isEmpty())
      {
         return "";
      }

      final StringBuilder builder = new StringBuilder(string);
      builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
      return builder.toString();
   }

   /**
    * Creates a new string by converting the first character of the given string to lowercase.
    * If the given string is empty, the empty string is returned.
    *
    * @param string
    *    the string
    *
    * @return the new string
    */
   public static String downFirstChar(String string)
   {
      if (string.isEmpty())
      {
         return "";
      }

      final StringBuilder builder = new StringBuilder(string);
      builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
      return builder.toString();
   }
}
