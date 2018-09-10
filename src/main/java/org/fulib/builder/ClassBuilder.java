package org.fulib.builder;

import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;

import java.lang.reflect.TypeVariable;
import java.util.Collection;

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
   public void buildAssociation(ClassBuilder otherClass, String myRoleName, int myCardinality, String otherRoleName, int otherCardinality, Class... collectionClasses)
   {
      AssocRole myRole = new AssocRole()
            .setClazz(this.getClazz())
            .setName(myRoleName)
            .setCardinality(myCardinality);

      if (collectionClasses != null && collectionClasses.length > 0)
      {
         String roleType = deriveRoleType(collectionClasses[0]);

         myRole.setRoleType(roleType);
      }
      else
      {
         myRole.setRoleType(this.clazz.getModel().getDefaultRoleType());
      }

      AssocRole otherRole = new AssocRole()
            .setClazz(otherClass.getClazz())
            .setName(otherRoleName)
            .setCardinality(otherCardinality);

      if (collectionClasses != null && collectionClasses.length > 1)
      {
         String roleType = deriveRoleType(collectionClasses[1]);
         otherRole.setRoleType(roleType);
      }
      else
      {
         otherRole.setRoleType(this.clazz.getModel().getDefaultRoleType());
      }

      myRole.setOther(otherRole);
   }

   private String deriveRoleType(Class collectionClass1)
   {
      Class collectionClass = collectionClass1;
      if ( ! Collection.class.isAssignableFrom(collectionClass))
      {
         throw new IllegalArgumentException("class is no collection");
      }

      String roleType = collectionClass.getName();
      TypeVariable[] typeParameters = collectionClass.getTypeParameters();
      if (typeParameters.length == 1)
      {
         roleType += "<%s>";
      }
      return roleType;
   }
}
