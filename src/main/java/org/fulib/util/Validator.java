package org.fulib.util;

import javax.lang.model.SourceVersion;

/**
 * @author Adrian Kunz
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

   public static void checkJavaLangNameClash(String className)
   {
      // java lang classes like Object, String, ...
      String javaLangName = "java.lang." + className;
      try
      {
         Class.forName(javaLangName);
         // that is no good
         throw new IllegalArgumentException("name clash with " + javaLangName);
      }
      catch (ClassNotFoundException e)
      {
         // that is good
      }
   }

   private static boolean hasPrefixVerb(String name, String verb)
   {
      return name.startsWith(verb) && name.length() > verb.length() && !Character.isLowerCase(
         name.codePointAt(verb.length()));
   }

   /**
    * @param methodName
    *    the method name
    * @param parameterCount
    *    the number of parameters
    *
    * @return whether a method with the given name and number of parameters is a property accessor (getter or setter).
    *
    * @see #isSetter(String, int)
    * @see #isGetter(String, int)
    * @since 1.3
    */
   public static boolean isProperty(String methodName, int parameterCount)
   {
      return isGetter(methodName, parameterCount) || isSetter(methodName, parameterCount);
   }

   /**
    * @param methodName
    *    the method name
    * @param parameterCount
    *    the number of parameters
    *
    * @return whether a method with the given name and number of parameters is a setter.
    * A setter is defined as a method with one parameter, whose name starts with {@code set}, {@code with} or
    * {@code without} followed by a character that is not lowercase.
    *
    * @since 1.3
    */
   public static boolean isSetter(String methodName, int parameterCount)
   {
      return
         (hasPrefixVerb(methodName, "set") || hasPrefixVerb(methodName, "with") || hasPrefixVerb(methodName, "without"))
         && parameterCount == 1;
   }

   /**
    * @param methodName
    *    the method name
    * @param parameterCount
    *    the number of parameters
    *
    * @return whether a method with the given name and number of parameters is a getter.
    * A getter is defined as a method with no parameters, whose name starts with {@code get} or {@code _init}
    * followed by a character that is not lowercase, or whose name ends with {@code Property} (for JavaFX properties).
    *
    * @since 1.3
    */
   public static boolean isGetter(String methodName, int parameterCount)
   {
      return (hasPrefixVerb(methodName, "get") || hasPrefixVerb(methodName, "_init") || methodName.endsWith("Property"))
             && parameterCount == 0;
   }
}
