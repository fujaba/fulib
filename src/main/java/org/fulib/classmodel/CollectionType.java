package org.fulib.classmodel;

import java.util.Collection;

@SuppressWarnings("rawtypes")
public class CollectionType
{
   // =============== Constants ===============

   public static final CollectionType ArrayList = new CollectionType(CollectionItf.List, "java.util.ArrayList<%s>",
                                                                     java.util.ArrayList.class);

   public static final CollectionType LinkedHashSet = new CollectionType(CollectionItf.Set,
                                                                         "java.util.LinkedHashSet<%s>",
                                                                         java.util.LinkedHashSet.class);

   // =============== Fields ===============

   private CollectionItf               itf;
   private String                      implTemplate;
   private Class<? extends Collection> implClass;

   // =============== Constructors ===============

   public CollectionType()
   {
   }

   private CollectionType(CollectionItf itf, String implTemplate, Class<? extends Collection> implClass)
   {
      this.itf = itf;
      this.implTemplate = implTemplate;
      this.implClass = implClass;
   }

   // =============== Static Methods ===============

   public static CollectionType of(Class<? extends Collection> implClass)
   {
      final CollectionItf itf = CollectionItf.deriveFrom(implClass);
      final String implTemplate = deriveTemplate(implClass);
      return new CollectionType(itf, implTemplate, implClass);
   }

   public static CollectionType of(String implTemplate)
   {
      final int genericIndex = implTemplate.indexOf('<');
      final String className = genericIndex >= 0 ? implTemplate.substring(0, genericIndex) : implTemplate;

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
      this.itf = itf;
   }

   public String getImplTemplate()
   {
      return this.implTemplate;
   }

   public void setImplTemplate(String implTemplate)
   {
      this.implTemplate = implTemplate;
   }

   public Class<? extends Collection> getImplClass()
   {
      return this.implClass;
   }

   public void setImplClass(Class<? extends Collection> implClass)
   {
      this.implClass = implClass;
   }
}
