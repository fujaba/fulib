package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.CollectionType;

import java.util.Collection;

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

   public AssociationBuilder setSourceRoleCollection(
      @SuppressWarnings("rawtypes") Class<? extends Collection> collectionClass)
   {
      this.srcRole.setCollectionType(CollectionType.of(collectionClass));
      return this;
   }

   public AssociationBuilder setTargetRoleCollection(
      @SuppressWarnings("rawtypes") Class<? extends Collection> collectionClass)
   {
      this.srcRole.getOther().setCollectionType(CollectionType.of(collectionClass));
      return this;
   }
}
