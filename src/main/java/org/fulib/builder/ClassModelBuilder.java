package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.classmodel.ClassModel;
import org.fulib.util.Validator;

import java.lang.reflect.TypeVariable;
import java.util.Collection;

/**
 * ClassModelbuilder is used to create fulib class models that are input for
 * fulib code generation {@link Fulib#generator()}.<br>
 * Typical usage:
 * <pre>
 * <!-- insert_code_fragment: ClassModelBuilder -->
        ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

        ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
      * <!-- end_code_fragment:  -->
 * </pre>
 *
 */
public class ClassModelBuilder
{
   @Deprecated public static final String STRING = "String";
   @Deprecated public static final String LONG = "long";
   @Deprecated public static final String INT = "int";
   @Deprecated public static final String FLOAT = "float";
   @Deprecated public static final String DOUBLE = "double";
   @Deprecated public static final String BOOLEAN = "boolean";
   @Deprecated public static final String __LIST = "__list";
   @Deprecated public static final int ONE = 1;
   @Deprecated public static final int MANY = 42;
   @Deprecated public static final String COLLECTION_ARRAY_LIST = "java.util.ArrayList<%s>";
   @Deprecated public static final String COLLECTION_LINKED_HASH_SET = "java.util.LinkedHashSet<%s>";
   @Deprecated public static final String POJO = "POJO";
   @Deprecated public static final String JAVA_FX = "JavaFX";

   private ClassModel classModel;

   /**
    * ClassModelbuilder is used to create fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}.<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
        ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

        ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param packagename
    *    the package name
    */
   public ClassModelBuilder(String packagename)
   {
      this(packagename, "src/main/java");
   }


   /**
    * ClassModelbuilder is used to create fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}.<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
        ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

        ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param packagename
    *    the package name
    * @param sourceFolder
    *    the source folder
    */
   public ClassModelBuilder(String packagename, String sourceFolder)
   {
      Validator.checkValidJavaId(packagename);

      ClassModel classModel = new ClassModel()
            .setPackageName(packagename)
            .setMainJavaDir(sourceFolder)
            .setDefaultPropertyStyle(Type.POJO)
            .setDefaultRoleType(Type.COLLECTION_ARRAY_LIST);

      this.setClassModel(classModel);
   }

   /**
    * Checks whether the string is a valid Java identifier, and throws an {@link IllegalArgumentException} if not.
    *
    * @param string
    *    the string to check
    *
    * @throws IllegalArgumentException
    *    if the string is not a valid Java identifier
    * @deprecated since 1.2; use {@link Validator#checkValidJavaId(String)} instead
    */
   @Deprecated
   static void checkValidJavaId(String string)
   {
      Validator.checkValidJavaId(string);
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
    * default is Type.COLLECTION_ARRAY_LIST
    * alternative is e.g.: ClassModelBuilder.
    *
    * @param collectionClass
    *    the collection class
    * @return
    *    this instance, to allow call chaining
    */
   public ClassModelBuilder setDefaultCollectionClass(Class collectionClass)
   {
      if ( ! Collection.class.isAssignableFrom(collectionClass))
      {
         throw new IllegalArgumentException("class is no collection");
      }

      String defaultRoleType = collectionClass.getName();
      TypeVariable[] typeParameters = collectionClass.getTypeParameters();
      if (typeParameters.length == 1)
      {
         defaultRoleType += "<%s>";
      }
      this.classModel.setDefaultRoleType(defaultRoleType);
      return this;
   }


   public ClassModelBuilder setJavaFXPropertyStyle()
   {
      classModel.setDefaultPropertyStyle(Type.JAVA_FX);
      return this;
   }


   /**
    * Builds and returns a class builder for the given classname and connects it to the model
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder.twoParams -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, "src/main/java")
            .setJavaFXPropertyStyle();

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", Type.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param className
    *    the class name
    *
    * @return new class builder
    */
   public ClassBuilder buildClass(String className)
   {
      return new ClassBuilder(this.classModel, className);
   }
}
