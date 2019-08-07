package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;

/**
 * ClassModelbuilder is used to create fulib class models that are input for
 * fulib code generation {@link Fulib#generator()}.<br>
 * Typical usage:
 * <pre>
 * <!-- insert_code_fragment: ClassModelBuilder -->
        ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

        ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", mb.STRING);
 * <!-- end_code_fragment:  -->
 * </pre>
 *
 */
public class ClassBuilder
{

   private Clazz clazz;


   /**
    * Builds a class builder for the given classname and connects it to the model
    * @param classModel
    * @param className
    */
   public ClassBuilder(ClassModel classModel, String className)
   {
      ClassModelBuilder.checkValidJavaId(className);

      // java lang classes like Object, String, ...
      String javaLangName = "java.lang." + className;
      try
      {
         Class<?> javaLangClass = this.getClass().getClassLoader().loadClass(javaLangName);
         // that is no good
         throw new IllegalArgumentException("name clash with " + javaLangName);
      }
      catch (ClassNotFoundException e)
      {
         // that is good
      }

      if (classModel.getClazz(className) != null) throw new IllegalArgumentException("duplicate class name" + className);

      Clazz clazz = new Clazz();
      clazz.setModel(classModel);
      clazz.setName(className);
      clazz.setPropertyStyle(classModel.getDefaultPropertyStyle());
      this.setClazz(clazz);
   }


   /**
    * @param clazz
    */
   private void setClazz(Clazz clazz)
   {
      this.clazz = clazz;
   }


   /**
    * @return the clazz this builder is responsible for 
    */
   public Clazz getClazz()
   {
      return this.clazz;
   }


   /**
    * ClassModelbuilder is used to create fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}.<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
        ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

        ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", mb.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param name
    * @param type
    * @return this class builder, for fluent style
    */
   public ClassBuilder buildAttribute(String name, String type)
   {
      buildAttribute(name, type, null);

      return this;
   }


   /**
    * <pre>
    * <!-- insert_code_fragment: ClassBuilder.buildAttribute_init -->
        ClassBuilder student = mb.buildClass("Student")
                .buildAttribute("name", mb.STRING, "\"Karli\"");
    * <!-- end_code_fragment:  -->
    * </pre>
    * @param name
    * @param type
    * @param initialValue
    * @return this class builder, for fluent style
    */
   public ClassBuilder buildAttribute(String name, String type, String initialValue)
   {
      ClassModelBuilder.checkValidJavaId(name);
      if (clazz.getAttribute(name) != null
      || clazz.getRole(name) != null)
         throw new IllegalArgumentException("duplicate attribute / role name");

      Attribute attribute = new Attribute();
      attribute.setClazz(this.clazz);
      attribute.setName(name);
      attribute.setType(type);
      attribute.setPropertyStyle(clazz.getPropertyStyle());
      attribute.setInitialization(initialValue);

      return this;
   }

   /**
    * <pre>
    * <!-- insert_code_fragment: ClassBuilder.buildAssociation -->
        universitiy.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);
    * <!-- end_code_fragment:  -->
    * </pre>
    * @param otherClass
    * @param myRoleName
    * @param myCardinality
    * @param otherRoleName
    * @param otherCardinality
    */
   public AssociationBuilder buildAssociation(ClassBuilder otherClass, String myRoleName, int myCardinality, String otherRoleName, int otherCardinality)
   {
      ClassModelBuilder.checkValidJavaId(myRoleName);

      if (otherRoleName != null) {
         ClassModelBuilder.checkValidJavaId(otherRoleName);
      }

      if (clazz.getAttribute(myRoleName) != null
            || clazz.getRole(myRoleName) != null)
         throw new IllegalArgumentException("duplicate attribute / role name");

      if (myRoleName.equals(otherRoleName) && myCardinality != otherCardinality)
         throw new IllegalArgumentException("duplicate attribute / role name");


      AssocRole myRole = new AssocRole()
            .setClazz(this.getClazz())
            .setName(myRoleName)
            .setCardinality(myCardinality)
            .setPropertyStyle(clazz.getPropertyStyle())
            .setRoleType(this.clazz.getModel().getDefaultRoleType());

      AssocRole otherRole = new AssocRole()
            .setClazz(otherClass.getClazz())
            .setName(otherRoleName)
            .setCardinality(otherCardinality)
            .setPropertyStyle(clazz.getPropertyStyle())
            .setRoleType(this.clazz.getModel().getDefaultRoleType());

      myRole.setOther(otherRole);

      return new AssociationBuilder(myRole);
   }


   public ClassBuilder setSuperClass(ClassBuilder superClass)
   {
      this.clazz.setSuperClass(superClass.getClazz());
      return this;
   }


   public ClassBuilder setJavaFXPropertyStyle()
   {
      clazz.setPropertyStyle(ClassModelBuilder.JAVA_FX);
      return this;
   }

}
