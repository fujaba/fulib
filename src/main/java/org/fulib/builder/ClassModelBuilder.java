package org.fulib.builder;

import org.fulib.classmodelbysdmlib.ClassModel;

public class ClassModelBuilder
{
   public static final String STRING = "String";
   public static final String LONG = "long";
   public static final String INT = "int";
   public static final String FLOAT = "float";
   public static final String DOUBLE = "double";
   public static final String BOOLEAN = "boolean";
   public static final int ONE = 1;
   public static final int MANY = 42;
   public static final String COLLECTION_ARRAY_LIST = "java.util.ArrayList<%s>";
   public static final String COLLECTION_LINKED_HASH_SET = "java.util.LinkedHashSet<%s>";

   private ClassModel classModel;

   /**
    * @param packagename
    * @return a class model builder for the given package name and with the default source folder "src/main/java"
    */
   public static ClassModelBuilder get(String packagename)
   {
      return get(packagename, "src/main/java");
   }


   /**
    * @param packagename
    * @param sourceFolder
    * @return a class model builder for the given package name and source folder 
    */
   public static ClassModelBuilder get(String packagename, String sourceFolder)
   {
      ClassModelBuilder classModelBuilder = new ClassModelBuilder();

      ClassModel classModel = new ClassModel();
      classModel.setPackageName(packagename);
      classModel.setMainJavaDir(sourceFolder);
      classModel.setDefaultRoleType(COLLECTION_ARRAY_LIST);

      classModelBuilder.setClassModel(classModel);
      return classModelBuilder;
   }


   private void setClassModel(ClassModel classModel)
   {
      this.classModel = classModel;
   }


   /**
    * @return the class model this builder is responsible for 
    */
   public ClassModel getClassModel()
   {
      return classModel;
   }

   /**
    * set container class to be used for to-many associations,
    * default is ClassModelBuilder.COLLECTION_ARRAY_LIST
    * alternative is e.g.: ClassModelBuilder.
    * @param defaultRoleType
    * @return
    */
   public ClassModelBuilder setDefaultRoleType(String defaultRoleType)
   {
      this.classModel.setDefaultRoleType(defaultRoleType);
      return this;
   }


   /**
    * Builds and returns a class builder for the given classname and connects it to the model
    * @param className
    * @return new class builder
    */
   public ClassBuilder buildClass( String className)
   {
      ClassBuilder classBuilder = new ClassBuilder(this.classModel, className);
      return classBuilder;
   }

}
