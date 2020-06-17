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
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);

      ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
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

   /**
    * @deprecated since 1.2; use {@link #setPropertyStyle(String)} with {@link Type#JAVA_FX}
    */
   @Deprecated
   public AssociationBuilder setJavaFXPropertyStyle()
   {
      return this.setPropertyStyle(Type.JAVA_FX);
   }

   /**
    * @since 1.2
    */
   public AssociationBuilder setPropertyStyle(String propertyStyle)
   {
      this.srcRole.setPropertyStyle(propertyStyle);
      this.srcRole.getOther().setPropertyStyle(propertyStyle);
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
