package org.fulib.util;

import javax.lang.model.SourceVersion;

/**
 * @author Adrian Kunz
 *
 * @since 1.2
 */
public class Validator
{
   public static boolean isQualifiedName(String string)
   {
      return string != null && SourceVersion.isName(string);
   }

   public static void checkQualifiedName(String string)
   {
      if (!isQualifiedName(string))
      {
         throw new IllegalArgumentException("'" + string + "' is not an valid qualified Java identifier");
      }
   }

   public static boolean isSimpleName(String string)
   {
      return string != null && SourceVersion.isIdentifier(string) && !SourceVersion.isKeyword(string);
   }

   public static void checkSimpleName(String string)
   {
      if (!isSimpleName(string))
      {
         throw new IllegalArgumentException("'" + string + "' is not a valid Java identifier");
      }
   }
}
