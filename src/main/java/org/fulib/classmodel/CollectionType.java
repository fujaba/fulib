package org.fulib.classmodel;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** @since 1.2 */
@SuppressWarnings("rawtypes")
public class CollectionType
{
   // =============== Static Fields ===============

   private static final Map<String, CollectionType> cache = new ConcurrentHashMap<>();

   // =============== Constants ===============

   // must be initialized after cache!

   public static final CollectionType ArrayList = of(java.util.ArrayList.class);
   public static final CollectionType LinkedHashSet = of(java.util.LinkedHashSet.class);

   // =============== Fields ===============

   private CollectionItf itf;
   private String implTemplate;
   private Class<? extends Collection> implClass;

   private final boolean cached;

   // =============== Constructors ===============

   public CollectionType()
   {
      this.cached = false;
   }

   private CollectionType(CollectionItf itf, String implTemplate, Class<? extends Collection> implClass)
   {
      this.cached = true;
      this.itf = itf;
      this.implTemplate = implTemplate;
      this.implClass = implClass;
   }

   // =============== Static Methods ===============

   public static CollectionType of(Class<? extends Collection> implClass)
   {
      return cache.computeIfAbsent(implClass.getName(), n -> create(implClass));
   }

   private static CollectionType create(Class<? extends Collection> implClass)
   {
      final CollectionItf itf = CollectionItf.deriveFrom(implClass);
      final String implTemplate = deriveTemplate(implClass);
      return new CollectionType(itf, implTemplate, implClass);
   }

   public static CollectionType of(String implTemplate)
   {
      final int genericIndex = implTemplate.indexOf('<');
      final String className = genericIndex >= 0 ? implTemplate.substring(0, genericIndex) : implTemplate;

      return cache.computeIfAbsent(className, c -> create(className, implTemplate));
   }

   private static CollectionType create(String className, String implTemplate)
   {
      try
      {
         final Class<?> implClass = Class.forName(className);

         if (!(Collection.class.isAssignableFrom(implClass)))
         {
            throw new IllegalArgumentException("class '" + className + "' is not a sub-type of java.util.Collection");
         }

         final CollectionItf itf = CollectionItf.deriveFrom((Class<? extends Collection>) implClass);
         return new CollectionType(itf, implTemplate, (Class<? extends Collection>) implClass);
      }
      catch (ClassNotFoundException e)
      {
         final CollectionItf itf = CollectionItf.deriveFrom(className);
         return new CollectionType(itf, implTemplate, null);
      }
   }

   private static String deriveTemplate(Class<? extends Collection> implClass)
   {
      if (!Collection.class.isAssignableFrom(implClass))
      {
         throw new IllegalArgumentException(
            "class '" + implClass.getName() + "' is not a sub-type of java.util.Collection");
      }

      final String roleType = implClass.getName();
      final int typeParamCount = implClass.getTypeParameters().length;
      switch (typeParamCount)
      {
      case 0:
         return roleType;
      case 1:
         return roleType + "<%s>";
      default:
         throw new IllegalArgumentException(
            "class '" + implClass.getName() + "' has too many type parameters (" + typeParamCount
            + "), only 0 or 1 are supported");
      }
   }

   // =============== Properties ===============

   public CollectionItf getItf()
   {
      return this.itf;
   }

   public void setItf(CollectionItf itf)
   {
      this.checkCached();
      this.itf = itf;
   }

   public String getImplTemplate()
   {
      return this.implTemplate;
   }

   public void setImplTemplate(String implTemplate)
   {
      this.checkCached();
      this.implTemplate = implTemplate;
   }

   public Class<? extends Collection> getImplClass()
   {
      return this.implClass;
   }

   public void setImplClass(Class<? extends Collection> implClass)
   {
      this.checkCached();
      this.implClass = implClass;
   }

   public String getQualifiedImplName()
   {
      if (this.implClass != null)
      {
         return this.implClass.getCanonicalName();
      }

      return this.implTemplate.substring(0, this.getClassNameEndIndex());
   }

   public String getSimpleImplName()
   {
      if (this.implClass != null)
      {
         return this.implClass.getSimpleName();
      }

      final int endIndex = this.getClassNameEndIndex();
      final int startIndex = this.implTemplate.lastIndexOf('.', endIndex) + 1; // not found, i.e. -1, becomes 0
      return this.implTemplate.substring(startIndex, endIndex);
   }

   public boolean isGeneric()
   {
      return this.implTemplate.indexOf('<') >= 0;
   }

   private int getClassNameEndIndex()
   {
      final int genericIndex = this.implTemplate.indexOf('<');
      if (genericIndex >= 0)
      {
         return genericIndex;
      }
      return this.implTemplate.length();
   }

   private void checkCached()
   {
      if (this.cached)
      {
         throw new UnsupportedOperationException("cannot modify cached CollectionType");
      }
   }
}
