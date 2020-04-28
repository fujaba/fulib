package org.fulib.classmodel;

import java.util.Collection;

/** @since 1.2 */
@SuppressWarnings("rawtypes")
public enum CollectionItf
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

   CollectionItf(Class<? extends Collection> itf)
   {
      this.itf = itf;
   }

   // =============== Static Methods ===============

   static CollectionItf deriveFrom(String implClassName)
   {
      if (implClassName.contains("List"))
      {
         return CollectionItf.List;
      }
      else if (implClassName.contains("Set"))
      {
         return CollectionItf.Set;
      }
      return CollectionItf.Collection;
   }

   static CollectionItf deriveFrom(Class<? extends Collection> implClass)
   {
      final CollectionItf[] values = values();
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

   public String getSimpleName()
   {
      return this.itf.getSimpleName();
   }

   public String getQualifiedName()
   {
      return this.itf.getName();
   }
}
