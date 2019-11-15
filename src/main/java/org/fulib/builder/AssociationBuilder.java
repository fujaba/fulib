package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.classmodel.AssocRole;

import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Objects;

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
public class AssociationBuilder
{

   private AssocRole srcRole;


   /**
    * Allows to define additional properties for associations, e.g. aggregation or Collection to be used
    * @param myRole
    */
   public AssociationBuilder(AssocRole myRole)
   {
      this.srcRole = myRole;
   }

   public AssociationBuilder setAggregation()
   {
      this.srcRole.setAggregation(true);
      return this;
   }

   public AssociationBuilder setSourceRoleCollection(Class collectionClass)
   {
      Objects.requireNonNull(collectionClass);

      if (Collection.class.isAssignableFrom(collectionClass))
      {
         String roleType = deriveRoleType(collectionClass);

         srcRole.setRoleType(roleType);
      }

      return this;
   }

   public AssociationBuilder setTargetRoleCollection(Class collectionClass)
   {
      Objects.requireNonNull(collectionClass);

      if (Collection.class.isAssignableFrom(collectionClass))
      {
         String roleType = deriveRoleType(collectionClass);

         srcRole.getOther().setRoleType(roleType);
      }

      return this;
   }


   public AssociationBuilder setJavaFXPropertyStyle()
   {
      srcRole.setPropertyStyle(Type.JAVA_FX);
      srcRole.getOther().setPropertyStyle(Type.JAVA_FX);
      return this;
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
