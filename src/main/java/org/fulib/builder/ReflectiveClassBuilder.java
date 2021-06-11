package org.fulib.builder;

import org.fulib.builder.reflect.*;
import org.fulib.classmodel.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.fulib.builder.Type.*;

class ReflectiveClassBuilder
{
   private static final String ID_PATTERN = "\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*";
   private static final Pattern CLASS_PATTERN = Pattern.compile(ID_PATTERN + "(?:\\." + ID_PATTERN + ")*");
   private final ClassModelManager manager;

   ReflectiveClassBuilder(ClassModelManager classModelManager)
   {
      this.manager = classModelManager;
   }

   static Clazz load(Class<?> classDef, ClassModelManager classModelManager)
   {
      return new ReflectiveClassBuilder(classModelManager).load(classDef);
   }

   Clazz load(Class<?> classDef)
   {
      final Clazz clazz = manager.haveClass(classDef.getSimpleName());

      final DTO dto = classDef.getAnnotation(DTO.class);
      if (dto != null)
      {
         loadDto(dto, clazz);
      }

      final Class<?> superClass = classDef.getSuperclass();
      if (superClass != null && superClass != Object.class)
      {
         final String superName = superClass.getSimpleName();
         final Clazz superClazz = manager.haveClass(superName);
         manager.haveSuper(clazz, superClazz);
      }

      for (final Field field : classDef.getDeclaredFields())
      {
         loadField(field, clazz, false);
      }

      return clazz;
   }

   private void loadDto(DTO dto, Clazz clazz)
   {
      final Class<?> model = dto.model();
      final Set<String> include = new HashSet<>(Arrays.asList(dto.pick()));
      final Set<String> exclude = new HashSet<>(Arrays.asList(dto.omit()));

      for (final Field field : model.getDeclaredFields())
      {
         final String name = field.getName();
         if ((include.isEmpty() || include.contains(name)) && !exclude.contains(name))
         {
            loadField(field, clazz, true);
         }
      }
   }

   private void loadField(Field field, Clazz clazz, boolean dto)
   {
      if (field.isSynthetic())
      {
         return;
      }

      final Link link = field.getAnnotation(Link.class);
      if (link == null)
      {
         loadAttribute(field, clazz, false);
      }
      else if (dto)
      {
         loadAttribute(field, clazz, true);
      }
      else
      {
         loadAssoc(field, link, clazz);
      }
   }

   private void loadAttribute(Field field, Clazz clazz, boolean dto)
   {
      final String name = field.getName();
      final CollectionType collectionType = getCollectionType(field.getType());
      final String type = dto ? STRING : getType(field, collectionType);

      final Attribute attribute = manager.haveAttribute(clazz, name, type);
      attribute.setCollectionType(collectionType);
      attribute.setDescription(getDescription(field));
      attribute.setSince(getSince(field));

      if (!dto)
      {
         final InitialValue initialValue = field.getAnnotation(InitialValue.class);
         if (initialValue != null)
         {
            attribute.setInitialization(initialValue.value());
         }
      }
   }

   private String getType(Field field, CollectionType collectionType)
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
         String.format("%s.%s: cannot determine element type of %s", declaringClass.getSimpleName(), field.getName(),
                       field.getType().getSimpleName()));
   }

   private String toSource(Class<?> base, Type type)
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

   private void toSource(Class<?> base, String className, StringBuilder out)
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
         else if (resolved.getEnclosingClass() != null && ClassModelDecorator.class.isAssignableFrom(
            resolved.getEnclosingClass()))
         {
            // resolved is nested class within another GenModel
            out
               .append("import(")
               .append(resolvedPackage.getName())
               .append('.')
               .append(resolved.getSimpleName())
               .append(')');
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

   private CollectionType getCollectionType(Class<?> type)
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

   private void loadAssoc(Field field, Link link, Clazz clazz)
   {
      final Class<?> owner = field.getDeclaringClass();
      final String name = field.getName();
      final CollectionType collectionType = getCollectionType(field.getType());

      String otherName = link.value();
      if (otherName.isEmpty())
      {
         otherName = null;
      }

      final Class<?> other = getOther(field, collectionType);
      validateTargetClass(owner, name, other);

      if (otherName != null)
      {
         validateLinkTarget(owner, name, otherName, other);
      }

      final String otherClazzName = other.getSimpleName();
      final Clazz otherClazz = manager.haveClass(otherClazzName);

      final AssocRole role = manager.associate(clazz, name, collectionType != null ? MANY : ONE, otherClazz, otherName,
                                               0);
      role.setCollectionType(collectionType);
      role.setDescription(getDescription(field));
      role.setSince(getSince(field));
   }

   private void validateTargetClass(Class<?> owner, String name, Class<?> other)
   {
      if (owner.getPackage() != other.getPackage())
      {
         throw new InvalidClassModelException(
            String.format("%s.%s: invalid link: target class %s (%s) must be in the same package (%s)",
                          owner.getSimpleName(), name, other.getSimpleName(), other.getPackage().getName(),
                          owner.getPackage().getName()));
      }
   }

   private void validateLinkTarget(Class<?> owner, String name, String otherName, Class<?> other)
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

   private Class<?> getOther(Field field, CollectionType collectionType)
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

   private Class<?> getSiblingClass(Field field, String simpleSiblingName)
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

   private String getDescription(Field field)
   {
      final Description annotation = field.getAnnotation(Description.class);
      return annotation != null ? annotation.value() : null;
   }

   private String getSince(Field field)
   {
      final Since annotation = field.getAnnotation(Since.class);
      return annotation != null ? annotation.value() : null;
   }
}
