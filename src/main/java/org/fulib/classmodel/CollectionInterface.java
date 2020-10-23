package org.fulib.classmodel;

import java.util.Collection;

/** @since 1.2 */
@SuppressWarnings("rawtypes")
public enum CollectionInterface
{
   // interfaces corresponding to java.util.Collections.unmodifiable* methods, excluding Maps
   // must be ordered super -> subtypes
   Collection(java.util.Collection.class),
   Set(java.util.Set.class),
   SortedSet(java.util.SortedSet.class),
   NavigableSet(java.util.NavigableSet.class),
   List(java.util.List.class);

   // =============== Fields ===============

   private final Class<? extends Collection> itf;

   // =============== Constructors ===============

   CollectionInterface(Class<? extends Collection> itf)
   {
      this.itf = itf;
   }

   // =============== Static Methods ===============

   /**
    * @param itfClass
    *    the interface class
    *
    * @return the {@link CollectionInterface} instance with the given class, or {@code null} if none matches
    *
    * @since 1.4
    */
   public static CollectionInterface valueOf(Class<? extends Collection> itfClass)
   {
      for (final CollectionInterface value : values())
      {
         if (value.getItfClass() == itfClass)
         {
            return value;
         }
      }
      return null;
   }

   static CollectionInterface deriveFrom(String implClassName)
   {
      if (implClassName.contains("List"))
      {
         return CollectionInterface.List;
      }
      else if (implClassName.contains("Set"))
      {
         return CollectionInterface.Set;
      }
      return CollectionInterface.Collection;
   }

   static CollectionInterface deriveFrom(Class<? extends Collection> implClass)
   {
      final CollectionInterface[] values = values();
      for (int i = values.length - 1; i >= 0; i--)
      {
         if (values[i].itf.isAssignableFrom(implClass))
         {
            return values[i];
         }
      }
      return Collection;
   }

   // =============== Properties ===============

   public Class<? extends java.util.Collection> getItfClass()
   {
      return this.itf;
   }

   /**
    * @return the default implementation {@link CollectionType} for this interface
    *
    * @since 1.4
    */
   public CollectionType getDefaultImpl()
   {
      switch (this)
      {
      case Collection:
      case Set:
         return CollectionType.LinkedHashSet;
      case List:
         return CollectionType.ArrayList;
      case SortedSet:
      case NavigableSet:
         return CollectionType.TreeSet;
      default:
         throw new AssertionError(this + " does not have a default implementation type");
      }
   }

   public String getSimpleName()
   {
      return this.itf.getSimpleName();
   }

   public String getQualifiedName()
   {
      return this.itf.getName();
   }
}
