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
 * ClassModelBuilder mb = Fulib.classModelBuilder(packageName);
 *
 * ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
 * <!-- end_code_fragment:  -->
 * </pre>
 */
public class AssociationBuilder
{
   // =============== Fields ===============

   private AssocRole srcRole;

   // =============== Constructors ===============

   /**
    * Allows to define additional properties for associations, e.g. aggregation or Collection to be used
    *
    * @param myRole
    *    the role to operate on
    */
   public AssociationBuilder(AssocRole myRole)
   {
      this.srcRole = myRole;
   }

   // =============== Methods ===============

   public AssociationBuilder setAggregation()
   {
      this.srcRole.setAggregation(true);
      return this;
   }

   public AssociationBuilder setJavaFXPropertyStyle()
   {
      this.srcRole.setPropertyStyle(Type.JAVA_FX);
      this.srcRole.getOther().setPropertyStyle(Type.JAVA_FX);
      return this;
   }

   public AssociationBuilder setSourceRoleCollection(Class<?> collectionClass)
   {
      this.srcRole.setCollectionType(deriveRoleType(collectionClass));
      return this;
   }

   public AssociationBuilder setTargetRoleCollection(Class<?> collectionClass)
   {
      this.srcRole.getOther().setCollectionType(deriveRoleType(collectionClass));
      return this;
   }

   private static String deriveRoleType(Class<?> collectionClass)
   {
      if (!Collection.class.isAssignableFrom(collectionClass))
      {
         throw new IllegalArgumentException("class is not a sub-type of java.util.Collection");
      }

      final String roleType = collectionClass.getName();
      return collectionClass.getTypeParameters().length == 1 ? roleType + "<%s>" : roleType;
   }
}
