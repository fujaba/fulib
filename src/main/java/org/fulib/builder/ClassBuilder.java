package org.fulib.builder;

import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;

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
      Clazz clazz = new Clazz();
      clazz.setModel(classModel);
      clazz.setName(className);
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
    * @param name
    * @param type
    * @param initialValue
    * @return this class builder, for fluent style
    */
   public ClassBuilder buildAttribute(String name, String type, String initialValue)
   {
      Attribute attribute = new Attribute();
      attribute.setClazz(this.clazz);
      attribute.setName(name);
      attribute.setType(type);
      attribute.setInitialization(initialValue);

      return this;
   }

   /**
    *
    * @param otherClass
    * @param myRoleName
    * @param myCardinality
    * @param otherRoleName
    * @param otherCardinality
    */
   public void buildAssociation(ClassBuilder otherClass, String myRoleName, int myCardinality, String otherRoleName, int otherCardinality, String... roleTypes)
   {
      AssocRole myRole = this.getClazz().createRoles()
            .withName(myRoleName)
            .withCardinality(myCardinality);

      if (roleTypes != null && roleTypes.length > 0)
      {
         myRole.setRoleType(roleTypes[0]);
      }
      else
      {
         myRole.setRoleType(ClassModelBuilder.ARRAY_LIST_T_);
      }

      AssocRole otherRole = otherClass.getClazz().createRoles()
            .withName(otherRoleName)
            .withCardinality(otherCardinality);

      if (roleTypes != null && roleTypes.length > 1)
      {
         otherRole.setRoleType(roleTypes[1]);
      }
      else
      {
         otherRole.setRoleType(ClassModelBuilder.ARRAY_LIST_T_);
      }

      myRole.setOther(otherRole);
   }
}
