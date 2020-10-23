package org.fulib.builder;

import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.CollectionInterface;
import org.fulib.classmodel.CollectionType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import static org.fulib.builder.Type.MANY;
import static org.fulib.builder.Type.ONE;

class ReflectiveClassBuilder
{
   static void load(Class<?> classDef, ClassModelManager manager)
   {
      final Clazz clazz = manager.haveClass(classDef.getSimpleName());

      for (final Field field : classDef.getDeclaredFields())
      {
         loadField(field, clazz, manager);
      }
   }

   private static void loadField(Field field, Clazz clazz, ClassModelManager manager)
   {
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

      manager.haveAttribute(clazz, name, type).setCollectionType(collectionType);
   }

   private static String getType(Field field, CollectionType collectionType)
   {
      if (collectionType == null)
      {
         return toSource(field.getGenericType());
      }

      final Type genericType = field.getGenericType();
      if (genericType instanceof ParameterizedType)
      {
         return toSource(((ParameterizedType) genericType).getActualTypeArguments()[0]);
      }

      throw new InvalidClassModelException("cannot determine element type for " + field);
   }

   private static String toSource(Type type)
   {
      return type
         .toString()
         .replaceAll("class java\\.lang\\.(\\w+)", "$1")
         .replaceAll("class (\\w+(?:\\.\\w+)*)", "import($1)");
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

      final String otherClazzName = getOther(field, collectionType);
      final Clazz other = manager.haveClass(otherClazzName);

      final String otherName = link.value();

      final AssocRole role = manager.associate(clazz, name, collectionType != null ? MANY : ONE, other, otherName, 0);
      role.setCollectionType(collectionType);
   }

   private static String getOther(Field field, CollectionType collectionType)
   {
      if (collectionType == null)
      {
         return field.getType().getSimpleName();
      }

      final Type genericType = field.getGenericType();
      if (genericType instanceof ParameterizedType)
      {
         final Type typeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
         if (typeArgument instanceof Class)
         {
            return ((Class<?>) typeArgument).getSimpleName();
         }
      }

      throw new InvalidClassModelException("cannot determine element type for " + field);
   }
}
