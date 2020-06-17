package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.util.Validator;

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
public class ClassBuilder
{
   // =============== Fields ===============

   private Clazz clazz;

   // =============== Constructors ===============

   /**
    * Builds a class builder for the given classname and connects it to the model
    *
    * @param classModel
    *    the class model
    * @param className
    *    the class name
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

   public ClassBuilder setSuperClass(ClassBuilder superClass)
   {
      this.clazz.setSuperClass(superClass.getClazz());
      return this;
   }

   public ClassBuilder setJavaFXPropertyStyle()
   {
      this.clazz.setPropertyStyle(Type.JAVA_FX);
      return this;
   }

   /**
    * @since 1.2
    */
   public ClassBuilder setPropertyStyle(String propertyStyle)
   {
      this.clazz.setPropertyStyle(propertyStyle);
      return this;
   }

   // =============== Methods ===============

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
    * @param name
    *    the attribute name
    * @param type
    *    the attribute type
    *
    * @return this class builder, for fluent style
    */
   public ClassBuilder buildAttribute(String name, String type)
   {
      this.buildAttribute(name, type, null);
      return this;
   }

   /**
    * <pre>
    * <!-- insert_code_fragment: ClassBuilder.buildAttribute_init -->
      ClassBuilder student = mb.buildClass("Student").buildAttribute("name", Type.STRING, "\"Karli\"");
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param name
    *    the attribute name
    * @param type
    *    the attribute type
    * @param initialValue
    *    the initialize value; can be any Java expression.
    *
    * @return this class builder, for fluent style
    */
   public ClassBuilder buildAttribute(String name, String type, String initialValue)
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
      attribute.setInitialization(initialValue);

      return this;
   }

   /**
    * <pre>
    * <!-- insert_code_fragment: ClassBuilder.buildAssociation -->
      universitiy.buildAssociation(student, "students", Type.MANY, "uni", Type.ONE);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param otherClass
    *    the ClassBuilder representing the target class
    * @param myRoleName
    *    the role name in this class
    * @param myCardinality
    *    the cardinality in this class
    * @param otherRoleName
    *    the role name in the target class
    * @param otherCardinality
    *    the cardinality in the target class
    *
    * @return an AssociationBuilder that allows further customization of the association
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

      AssocRole myRole = new AssocRole().setClazz(this.getClazz()).setName(myRoleName).setCardinality(myCardinality)
                                        .setPropertyStyle(this.clazz.getPropertyStyle())
                                        .setCollectionType(this.clazz.getModel().getDefaultCollectionType());

      AssocRole otherRole = new AssocRole().setClazz(otherClass.getClazz()).setName(otherRoleName)
                                           .setCardinality(otherCardinality)
                                           .setPropertyStyle(this.clazz.getPropertyStyle())
                                           .setCollectionType(this.clazz.getModel().getDefaultCollectionType());

      myRole.setOther(otherRole);

      return new AssociationBuilder(myRole);
   }
}
