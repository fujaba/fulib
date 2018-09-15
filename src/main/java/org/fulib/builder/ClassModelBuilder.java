package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.classmodel.ClassModel;

import java.lang.reflect.TypeVariable;
import java.util.Collection;

/**
 * ClassModelbuilder is used to create fulib class models that are input for
 * fulib code generation {@link Fulib#generator()}.<br>
 * Typical usage:
 * <pre>
 * <!-- insert_code_fragment: ClassModelBuilder -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
      * <!-- end_code_fragment:  -->
 * </pre>
 *
 */
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
    * ClassModelbuilder is used to create fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}.<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    * @param packagename
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

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    * @param packagename
    * @param sourceFolder
    */
   public ClassModelBuilder(String packagename, String sourceFolder)
   {
      checkValidJavaId(packagename);

      ClassModel classModel = new ClassModel()
            .setPackageName(packagename)
            .setMainJavaDir(sourceFolder)
            .setDefaultRoleType(COLLECTION_ARRAY_LIST);

      this.setClassModel(classModel);
   }


   static void checkValidJavaId(String myRoleName)
   {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("" + myRoleName + " is not an valid java identifier");

      if (myRoleName == null) throw illegalArgumentException;

      if (myRoleName.endsWith(".") || myRoleName.startsWith(".")) throw illegalArgumentException;

      if (myRoleName.indexOf('.') >= 0)
      {
         for (String s : myRoleName.split("\\."))
         {
            checkValidJavaId(s);
         }
         return;
      }

      if ( ! myRoleName.matches("[a-zA-Z_]\\w*")) throw illegalArgumentException;

      String javaKeyWords = " abstract assert boolean break " +
            "byte case catch char " +
            "class const continue default " +
            "do double else enum " +
            "extends final finally float " +
            "for goto if implements " +
            "import instanceof int interface " +
            "long native new package " +
            "private protected public return " +
            "short static strictfp super " +
            "switch synchronized this throw " +
            "throws transient try void " +
            "volatile while  true  false " +
            "null ";

      if (javaKeyWords.indexOf(" " + myRoleName + " ") >= 0 ) throw illegalArgumentException;

      // hm, myRoleName seems valid
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
    * @param collectionClass
    * @return
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


   /**
    * Builds and returns a class builder for the given classname and connects it to the model
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder.twoParams -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, "src/main/java");

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    * @param className
    * @return new class builder
    */
   public ClassBuilder buildClass(String className)
   {
      ClassBuilder classBuilder = new ClassBuilder(this.classModel, className);
      return classBuilder;
   }


}
