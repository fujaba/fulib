package org.fulib.util;

public class Validator
{

   /**
    * Checks whether the string is a valid Java identifier, and throws an {@link IllegalArgumentException} if not.
    *
    * @param string
    *    the string to check
    *
    * @throws IllegalArgumentException
    *    if the string is not a valid Java identifier
    */
   public static void checkValidJavaId(String string)
   {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("" + string + " is not an valid java identifier");

      if (string == null) throw illegalArgumentException;

      if (string.endsWith(".") || string.startsWith(".")) throw illegalArgumentException;

      if (string.indexOf('.') >= 0)
      {
         for (String s : string.split("\\."))
         {
            checkValidJavaId(s);
         }
         return;
      }

      if ( ! string.matches("[a-zA-Z_]\\w*")) throw illegalArgumentException;

      String javaKeyWords = " abstract assert boolean break " +
                            "byte case catch char " +
                            "class const continue default " +
                            "do double else enum " +
                            "extends final finally float " +
                            "for goto if implements " +
                            "import instanceof int interface " +
                            "long native new package " +
                            "private protected public return " +
                            "short static strictfp super " +
                            "switch synchronized this throw " +
                            "throws transient try void " +
                            "volatile while  true  false " +
                            "null ";

      if (javaKeyWords.indexOf(" " + string + " ") >= 0 ) throw illegalArgumentException;

      // hm, string seems valid
   }
}
