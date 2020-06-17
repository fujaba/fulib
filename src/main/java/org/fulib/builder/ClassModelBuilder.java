package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.CollectionType;
import org.fulib.util.Validator;

import java.util.Collection;

/**
 * ClassModelbuilder is used to create fulib class models that are input for
 * fulib code generation {@link Fulib#generator()}.<br>
 * Typical usage:
 * <pre>
 * <!-- insert_code_fragment: ClassModelBuilder -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);

      ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
 * <!-- end_code_fragment:  -->
 * </pre>
 */
public class ClassModelBuilder
{
   // =============== Constants ===============

   public static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

   // replacements for the deprecated constants below can be found in org.fulib.builder.Type.

   // @formatter:off
   /** @deprecated since 1.2; use {@link Type#STRING} instead */ @Deprecated public static final String STRING = "String";
   /** @deprecated since 1.2; use {@link Type#LONG} instead */ @Deprecated public static final String LONG = "long";
   /** @deprecated since 1.2; use {@link Type#INT} instead */ @Deprecated public static final String INT = "int";
   /** @deprecated since 1.2; use {@link Type#FLOAT} instead */ @Deprecated public static final String FLOAT = "float";
   /** @deprecated since 1.2; use {@link Type#DOUBLE} instead */ @Deprecated public static final String DOUBLE = "double";
   /** @deprecated since 1.2; use {@link Type#BOOLEAN} instead */ @Deprecated public static final String BOOLEAN = "boolean";
   /** @deprecated since 1.2; use {@link Type#ONE} instead */ @Deprecated public static final int ONE = 1;
   /** @deprecated since 1.2; use {@link Type#MANY} instead */ @Deprecated public static final int MANY = 42;
   /** @deprecated since 1.2; use {@link Type#POJO} instead */ @Deprecated public static final String POJO = "POJO";
   /** @deprecated since 1.2; use {@link Type#JAVA_FX} instead */ @Deprecated public static final String JAVA_FX = "JavaFX";
   // @formatter:on

   /**
    * @deprecated since 1.2; use {@link CollectionType#ArrayList} in conjunction with
    * {@link AssocRole#setCollectionType(CollectionType)} or
    * {@link ClassModel#setDefaultCollectionType(CollectionType)}
    */
   @Deprecated public static final String COLLECTION_ARRAY_LIST = "java.util.ArrayList<%s>";

   /**
    * @deprecated since 1.2; use {@link CollectionType#LinkedHashSet} in conjunction with
    * {@link AssocRole#setCollectionType(CollectionType)} or
    * {@link ClassModel#setDefaultCollectionType(CollectionType)}
    */
   @Deprecated public static final String COLLECTION_LINKED_HASH_SET = "java.util.LinkedHashSet<%s>";

   // =============== Fields ===============

   private ClassModel classModel;

   // =============== Constructors ===============

   /**
    * ClassModelbuilder is used to create fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}.<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);

      ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param packageName
    *    the package name
    */
   public ClassModelBuilder(String packageName)
   {
      this(packageName, DEFAULT_SOURCE_FOLDER);
   }

   /**
    * ClassModelbuilder is used to create fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}.<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);

      ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param packageName
    *    the package name
    * @param sourceFolder
    *    the source folder
    */
   public ClassModelBuilder(String packageName, String sourceFolder)
   {
      Validator.checkQualifiedName(packageName);

      final ClassModel classModel = new ClassModel();
      classModel.setPackageName(packageName);
      classModel.setMainJavaDir(sourceFolder);
      classModel.setDefaultPropertyStyle(Type.BEAN);
      classModel.setDefaultCollectionType(CollectionType.ArrayList);

      this.classModel = classModel;
   }

   /**
    * Allows using the builder APIs on an existing class model.
    *
    * @param classModel
    *    the class model to operate on
    *
    * @since 1.2
    */
   public ClassModelBuilder(ClassModel classModel)
   {
      this.classModel = classModel;
   }

   // =============== Static Methods ===============

   /**
    * Checks whether the string is a valid Java identifier, and throws an {@link IllegalArgumentException} if not.
    *
    * @param string
    *    the string to check
    *
    * @throws IllegalArgumentException
    *    if the string is not a valid Java identifier
    * @deprecated since 1.2; use {@link Validator#checkQualifiedName(String)} instead
    */
   @Deprecated
   static void checkValidJavaId(String string)
   {
      Validator.checkQualifiedName(string);
   }

   // =============== Properties ===============

   /**
    * @return the class model this builder is responsible for
    */
   public ClassModel getClassModel()
   {
      return this.classModel;
   }

   /**
    * set container class to be used for to-many associations,
    * default is Type.COLLECTION_ARRAY_LIST
    * alternative is e.g.: ClassModelBuilder.
    *
    * @param collectionClass
    *    the collection class
    *
    * @return this instance, to allow call chaining
    */
   public ClassModelBuilder setDefaultCollectionClass(
      @SuppressWarnings("rawtypes") Class<? extends Collection> collectionClass)
   {
      this.classModel.setDefaultCollectionType(CollectionType.of(collectionClass));
      return this;
   }

   /**
    * Sets the default property style for the class model being built.
    *
    * @param propertyStyle
    *    the property style
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public ClassModelBuilder setDefaultPropertyStyle(String propertyStyle)
   {
      this.classModel.setDefaultPropertyStyle(propertyStyle);
      return this;
   }

   /**
    * Sets the default property style for the class model being built to "JavaFX".
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setDefaultPropertyStyle(String) setDefaultPropertyStyle}({@link Type#JAVA_FX}) instead.
    */
   @Deprecated
   public ClassModelBuilder setJavaFXPropertyStyle()
   {
      this.classModel.setDefaultPropertyStyle(Type.JAVA_FX);
      return this;
   }

   // =============== Methods ===============

   /**
    * Builds and returns a class builder for the given classname and connects it to the model
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder.twoParams -->
    * ClassModelBuilder mb = Fulib.classModelBuilder(packageName, "src/main/java")
    * .setJavaFXPropertyStyle();
    *
    * ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", Type.STRING);
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
