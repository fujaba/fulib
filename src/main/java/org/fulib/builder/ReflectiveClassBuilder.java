package org.fulib.builder;

import org.fulib.builder.reflect.*;
import org.fulib.classmodel.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fulib.builder.Type.MANY;
import static org.fulib.builder.Type.ONE;

class ReflectiveClassBuilder
{
   private static final String ID_PATTERN = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
   private static final Pattern CLASS_PATTERN = Pattern.compile(ID_PATTERN + "(?:\\." + ID_PATTERN + ")*");

   static Clazz load(Class<?> classDef, ClassModelManager manager)
   {
      final Clazz clazz = manager.haveClass(classDef.getSimpleName());

      final Class<?> superClass = classDef.getSuperclass();
      if (superClass != null && superClass != Object.class)
      {
         final String superName = superClass.getSimpleName();
         final Clazz superClazz = manager.haveClass(superName);
         manager.haveSuper(clazz, superClazz);
      }

      for (final Field field : classDef.getDeclaredFields())
      {
         loadField(field, clazz, manager);
      }

      return clazz;
   }

   private static void loadField(Field field, Clazz clazz, ClassModelManager manager)
   {
      if (field.isSynthetic())
      {
         return;
      }

      final Link link = field.getAnnotation(Link.class);
      if (link == null)
      {
         loadAttribute(field, clazz, manager);
      }
      else
      {
         loadAssoc(field, link, clazz, manager);
      }
   }

   private static void loadAttribute(Field field, Clazz clazz, ClassModelManager manager)
   {
      final String name = field.getName();
      final CollectionType collectionType = getCollectionType(field.getType());
      final String type = getType(field, collectionType);

      final Attribute attribute = manager.haveAttribute(clazz, name, type);
      attribute.setCollectionType(collectionType);
      attribute.setDescription(getDescription(field));
      attribute.setSince(getSince(field));

      final InitialValue initialValue = field.getAnnotation(InitialValue.class);
      if (initialValue != null)
      {
         attribute.setInitialization(initialValue.value());
      }
   }

   private static String getType(Field field, CollectionType collectionType)
   {
      final org.fulib.builder.reflect.Type type = field.getAnnotation(org.fulib.builder.reflect.Type.class);
      if (type != null)
      {
         return type.value();
      }

      final Class<?> declaringClass = field.getDeclaringClass();
      if (collectionType == null)
      {
         return toSource(declaringClass, field.getGenericType());
      }

      final Type genericType = field.getGenericType();
      if (genericType instanceof ParameterizedType)
      {
         return toSource(declaringClass, ((ParameterizedType) genericType).getActualTypeArguments()[0]);
      }

      throw new InvalidClassModelException(
         String.format("%s.%s: cannot determine element type of %s", declaringClass.getSimpleName(),
                       field.getName(), field.getType().getSimpleName()));
   }

   private static String toSource(Class<?> base, Type type)
   {
      final String input = type.getTypeName();
      final Matcher matcher = CLASS_PATTERN.matcher(input);
      final StringBuilder sb = new StringBuilder();
      int prev = 0;
      while (matcher.find())
      {
         final String className = matcher.group();
         sb.append(input, prev, matcher.start());
         toSource(base, className, sb);
         prev = matcher.end();
      }
      sb.append(input, prev, input.length());
      return sb.toString();
   }

   private static void toSource(Class<?> base, String className, StringBuilder out)
   {
      switch (className)
      {
      case "void":
      case "boolean":
      case "byte":
      case "short":
      case "char":
      case "int":
      case "long":
      case "float":
      case "double":
         out.append(className);
         return;
      }

      try
      {
         final Class<?> resolved = Class.forName(className);
         final Package resolvedPackage = resolved.getPackage();
         final String canonicalName = resolved.getCanonicalName();
         if ("java.lang".equals(resolvedPackage.getName()))
         {
            out.append(canonicalName, "java.lang.".length(), canonicalName.length());
         }
         else if (resolvedPackage == base.getPackage())
         {
            out.append(resolved.getSimpleName());
         }
         else
         {
            out.append("import(").append(canonicalName).append(')');
         }
      }
      catch (ClassNotFoundException e)
      {
         throw new InvalidClassModelException("cannot determine source representation of " + className, e);
      }
   }

   private static CollectionType getCollectionType(Class<?> type)
   {
      if (!Collection.class.isAssignableFrom(type))
      {
         return null;
      }

      final Class<? extends Collection<?>> collectionType = (Class<? extends Collection<?>>) type;
      final CollectionInterface itf = CollectionInterface.valueOf(collectionType);
      if (itf != null)
      {
         return itf.getDefaultImpl();
      }

      return CollectionType.of(collectionType);
   }

   private static void loadAssoc(Field field, Link link, Clazz clazz, ClassModelManager manager)
   {
      final String name = field.getName();
      final CollectionType collectionType = getCollectionType(field.getType());

      String otherName = link.value();
      if (otherName.isEmpty())
      {
         otherName = null;
      }

      final Class<?> other = getOther(field, collectionType);

      if (otherName != null)
      {
         validateLinkTarget(field.getDeclaringClass(), name, otherName, other);
      }

      final String otherClazzName = other.getSimpleName();
      final Clazz otherClazz = manager.haveClass(otherClazzName);

      final AssocRole role = manager.associate(clazz, name, collectionType != null ? MANY : ONE, otherClazz, otherName,
                                               0);
      role.setCollectionType(collectionType);
      role.setDescription(getDescription(field));
      role.setSince(getSince(field));
   }

   private static void validateLinkTarget(Class<?> owner, String name, String otherName, Class<?> other)
   {
      final Field targetField;
      try
      {
         targetField = other.getDeclaredField(otherName);
      }
      catch (NoSuchFieldException e)
      {
         throw new InvalidClassModelException(
            String.format("%s.%s: invalid link target: field %s.%s not found", owner.getSimpleName(), name,
                          other.getSimpleName(), otherName), e);
      }

      final Link targetLink = targetField.getAnnotation(Link.class);
      if (targetLink == null)
      {
         throw new InvalidClassModelException(
            String.format("%s.%s: invalid link target: field %s.%s is not annotated with @Link", owner.getSimpleName(),
                          name, other.getSimpleName(), otherName));
      }

      final String targetLinkName = targetLink.value();
      if (!name.equals(targetLinkName))
      {
         throw new InvalidClassModelException(String.format(
            "%s.%s: invalid link target: field %s.%s is annotated as @Link(\"%s\") instead of @Link(\"%s\")",
            owner.getSimpleName(), name, other.getSimpleName(), otherName, targetLinkName, name));
      }

      final Class<?> otherOther = getOther(targetField, getCollectionType(targetField.getType()));
      if (otherOther != owner)
      {
         throw new InvalidClassModelException(
            String.format("%s.%s: invalid link target: field %s.%s has target type %s instead of %s",
                          owner.getSimpleName(), name, other.getSimpleName(), otherName, otherOther.getSimpleName(),
                          owner.getSimpleName()));
      }
   }

   private static Class<?> getOther(Field field, CollectionType collectionType)
   {
      final org.fulib.builder.reflect.Type type = field.getAnnotation(org.fulib.builder.reflect.Type.class);
      if (type != null)
      {
         return getSiblingClass(field, type.value());
      }

      if (collectionType == null)
      {
         return field.getType();
      }

      final Type genericType = field.getGenericType();
      if (genericType instanceof ParameterizedType)
      {
         final Type typeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
         if (typeArgument instanceof Class)
         {
            return (Class<?>) typeArgument;
         }
      }

      throw new InvalidClassModelException(
         String.format("%s.%s: cannot determine element type of %s", field.getDeclaringClass().getSimpleName(),
                       field.getName(), field.getType().getSimpleName()));
   }

   private static Class<?> getSiblingClass(Field field, String simpleSiblingName)
   {
      final Class<?> declaringClass = field.getDeclaringClass();
      final String name = declaringClass.getName();
      final String simpleName = declaringClass.getSimpleName();
      final String siblingName = name.substring(0, name.length() - simpleName.length()) + simpleSiblingName;

      try
      {
         return Class.forName(siblingName);
      }
      catch (ClassNotFoundException e)
      {
         throw new InvalidClassModelException(
            String.format("%s.%s: invalid link target: class %s not found", simpleName, field.getName(),
                          simpleSiblingName), e);
      }
   }

   private static String getDescription(Field field)
   {
      final Description annotation = field.getAnnotation(Description.class);
      return annotation != null ? annotation.value() : null;
   }

   private static String getSince(Field field)
   {
      final Since annotation = field.getAnnotation(Since.class);
      return annotation != null ? annotation.value() : null;
   }
}
