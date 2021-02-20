package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.classmodel.*;
import org.fulib.util.Validator;

/**
 * ClassModelbuilder is used to create fulib class models that are input for
 * fulib code generation {@link Fulib#generator()}.<br>
 * Typical usage:
 * <pre>
 * <!-- insert_code_fragment: ClassModelBuilder -->
 * ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);
 *
 * ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
 * <!-- end_code_fragment:  -->
 * </pre>
 *
 * @deprecated since 1.5; use {@link ClassModelManager} instead
 */
@Deprecated
public class ClassBuilder
{
   // =============== Fields ===============

   private final Clazz clazz;

   // =============== Constructors ===============

   /**
    * Creates a class builder for a newly created class with the given class name and model.
    * The class uses the same property style as the class model.
    *
    * @param classModel
    *    the class model
    * @param className
    *    the class name
    *
    * @throws IllegalArgumentException
    *    if a class with the name already exists within the class model,
    *    or the class name is not a valid Java identifier,
    *    or the class name clashed with a class in the {@link java.lang} package
    */
   public ClassBuilder(ClassModel classModel, String className)
   {
      Validator.checkSimpleName(className);
      Validator.checkJavaLangNameClash(className);

      if (classModel.getClazz(className) != null)
      {
         throw new IllegalArgumentException("duplicate class name" + className);
      }

      final Clazz clazz = new Clazz();
      clazz.setModel(classModel);
      clazz.setName(className);
      clazz.setPropertyStyle(classModel.getDefaultPropertyStyle());
      this.clazz = clazz;
   }

   /**
    * Creates a class builder that operates on the given class.
    *
    * @param clazz
    *    the class to operate on
    *
    * @since 1.2
    */
   public ClassBuilder(Clazz clazz)
   {
      this.clazz = clazz;
   }

   // =============== Properties ===============

   /**
    * @return the clazz this builder is responsible for
    */
   public Clazz getClazz()
   {
      return this.clazz;
   }

   /**
    * Sets the super class.
    *
    * @param superClass
    *    the class builder containing the super class
    *
    * @return this instance, to allow method chaining
    */
   public ClassBuilder setSuperClass(ClassBuilder superClass)
   {
      this.clazz.setSuperClass(superClass.getClazz());
      return this;
   }

   /**
    * Sets the property style for the class to {@link Type#JAVA_FX}.
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setPropertyStyle(String)} with {@link Type#JAVA_FX}
    */
   @Deprecated
   public ClassBuilder setJavaFXPropertyStyle()
   {
      this.clazz.setPropertyStyle(Type.JAVA_FX);
      return this;
   }

   /**
    * @param propertyStyle
    *    the property style for the class
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public ClassBuilder setPropertyStyle(String propertyStyle)
   {
      this.clazz.setPropertyStyle(propertyStyle);
      return this;
   }

   // =============== Methods ===============

   /**
    * Creates an attribute with the given name and type and no initial value.
    * The attribute uses the same property style as the class.
    *
    * @param name
    *    the attribute name
    * @param type
    *    the attribute type
    *
    * @return this instance, to allow method chaining
    *
    * @throws IllegalArgumentException
    *    if an attribute or role with the same name already exists within the class
    * @see #buildAttribute(String, String, String)
    */
   public ClassBuilder buildAttribute(String name, String type)
   {
      this.buildAttribute(name, type, null);
      return this;
   }

   /**
    * Creates an attribute with the given name, type and initial value.
    * The attribute uses the same property style as the class.
    *
    * @param name
    *    the attribute name
    * @param type
    *    the attribute type
    * @param initialValue
    *    the initial value; can be any Java expression or {@code null}.
    *
    * @return this instance, to allow method chaining
    *
    * @throws IllegalArgumentException
    *    if an attribute or role with the same name already exists within the class,
    *    or the name is not a valid Java identifier
    */
   public ClassBuilder buildAttribute(String name, String type, String initialValue)
   {
      return this.buildAttribute(name, type, null, initialValue);
   }

   /**
    * Creates an attribute with the given name, type and initial value.
    * By passing a non-{@code null} collection type, the attribute can be made multi-valued.
    * This will generate {@code with*} and {@code without*} methods instead of a setter.
    * The attribute uses the same property style as the class.
    *
    * @param name
    *    the attribute name
    * @param type
    *    the attribute type; serves as the element type if {@code collectionType} is specified
    * @param collectionType
    *    the collection type; can be {@code null} for simple attributes
    * @param initialValue
    *    the initial value; can be any Java expression or {@code null}.
    *
    * @return this instance, to allow method chaining
    *
    * @throws IllegalArgumentException
    *    if an attribute or role with the same name already exists within the class,
    *    or the name is not a valid Java identifier
    *
    * @since 1.2
    */
   public ClassBuilder buildAttribute(String name, String type, CollectionType collectionType, String initialValue)
   {
      Validator.checkSimpleName(name);
      if (this.clazz.getAttribute(name) != null || this.clazz.getRole(name) != null)
      {
         throw new IllegalArgumentException("duplicate attribute / role name");
      }

      Attribute attribute = new Attribute();
      attribute.setClazz(this.clazz);
      attribute.setName(name);
      attribute.setType(type);
      attribute.setPropertyStyle(this.clazz.getPropertyStyle());
      attribute.setCollectionType(collectionType);
      attribute.setInitialization(initialValue);

      return this;
   }

   /**
    * Creates an association between this class and the target class.
    * Both source and target roles use the same property style as the class,
    * and the same collection type as the class model default.
    *
    * @param otherClass
    *    the ClassBuilder representing the target class
    * @param myRoleName
    *    the role name in this class
    * @param myCardinality
    *    the cardinality in this class, must be either {@link Type#ONE} or {@link Type#MANY}
    * @param otherRoleName
    *    the role name in the target class, or {@code null} if the association should be unidirectional
    * @param otherCardinality
    *    the cardinality in the target class, must be either {@link Type#ONE} or {@link Type#MANY}
    *
    * @return an AssociationBuilder that allows further customization of the association
    *
    * @throws IllegalArgumentException
    *    if either role name is not a valid Java identifier,
    *    or a role or attribute with same name already exists within the class,
    *    or both names are the same but the cardinalities differ
    */
   public AssociationBuilder buildAssociation(ClassBuilder otherClass, String myRoleName, int myCardinality,
      String otherRoleName, int otherCardinality)
   {
      Validator.checkSimpleName(myRoleName);

      if (otherRoleName != null)
      {
         Validator.checkSimpleName(otherRoleName);
      }

      if (this.clazz.getAttribute(myRoleName) != null || this.clazz.getRole(myRoleName) != null)
      {
         throw new IllegalArgumentException("duplicate attribute / role name");
      }

      if (myRoleName.equals(otherRoleName) && myCardinality != otherCardinality)
      {
         throw new IllegalArgumentException("duplicate attribute / role name");
      }

      AssocRole myRole = new AssocRole()
         .setClazz(this.getClazz())
         .setName(myRoleName)
         .setCardinality(myCardinality)
         .setPropertyStyle(this.clazz.getPropertyStyle())
         .setCollectionType(this.clazz.getModel().getDefaultCollectionType());

      AssocRole otherRole = new AssocRole()
         .setClazz(otherClass.getClazz())
         .setName(otherRoleName)
         .setCardinality(otherCardinality)
         .setPropertyStyle(this.clazz.getPropertyStyle())
         .setCollectionType(this.clazz.getModel().getDefaultCollectionType());

      myRole.setOther(otherRole);

      return new AssociationBuilder(myRole);
   }
}
