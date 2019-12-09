package org.fulib.util;

import javax.lang.model.SourceVersion;

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
      if (!SourceVersion.isName(string))
      {
         throw new IllegalArgumentException("'" + string + "' is not an valid Java identifier");
      }
   }
}
