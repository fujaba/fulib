package org.fulib.builder;

import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.CollectionType;

import java.util.Collection;

/**
 * Allows defining additional properties for associations, e.g. aggregation, property style or collection type.
 */
public class AssociationBuilder
{
   // =============== Fields ===============

   private AssocRole role;

   // =============== Constructors ===============

   /**
    * @param role
    *    the role to operate on
    */
   public AssociationBuilder(AssocRole role)
   {
      this.role = role;
   }

   // =============== Methods ===============

   /**
    * Makes the source role an aggregation.
    *
    * @return this instance, to allow method chaining
    */
   public AssociationBuilder setAggregation()
   {
      this.role.setAggregation(true);
      return this;
   }

   /**
    * Sets the property style for both source and target roles to {@link Type#JAVA_FX}.
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setPropertyStyle(String)} with {@link Type#JAVA_FX}
    */
   @Deprecated
   public AssociationBuilder setJavaFXPropertyStyle()
   {
      return this.setPropertyStyle(Type.JAVA_FX);
   }

   /**
    * Sets the property style for both source and target roles.
    *
    * @param propertyStyle
    *    the property style, e.g. {@link Type#BEAN} or {@link Type#JAVA_FX}
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public AssociationBuilder setPropertyStyle(String propertyStyle)
   {
      this.role.setPropertyStyle(propertyStyle);
      this.role.getOther().setPropertyStyle(propertyStyle);
      return this;
   }

   /**
    * Sets the collection type for the target role.
    *
    * @param collectionClass
    *    the collection implementation class, e.g. {@link java.util.ArrayList}
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setSourceRoleCollection(CollectionType)} with {@link CollectionType#of(Class)}
    */
   @Deprecated
   public AssociationBuilder setSourceRoleCollection(
      @SuppressWarnings("rawtypes") Class<? extends Collection> collectionClass)
   {
      this.role.setCollectionType(CollectionType.of(collectionClass));
      return this;
   }

   /**
    * Sets the collection type for the source role.
    *
    * @param type
    *    the collection type
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public AssociationBuilder setSourceRoleCollection(CollectionType type)
   {
      this.role.setCollectionType(type);
      return this;
   }

   /**
    * Sets the collection type for the target role.
    *
    * @param collectionClass
    *    the collection implementation class, e.g. {@link java.util.ArrayList}
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setTargetRoleCollection(CollectionType)} with {@link CollectionType#of(Class)}
    */
   @Deprecated
   public AssociationBuilder setTargetRoleCollection(
      @SuppressWarnings("rawtypes") Class<? extends Collection> collectionClass)
   {
      this.role.getOther().setCollectionType(CollectionType.of(collectionClass));
      return this;
   }

   /**
    * Sets the collection type for the target role.
    *
    * @param type
    *    the collection type
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public AssociationBuilder setTargetRoleCollection(CollectionType type)
   {
      this.role.getOther().setCollectionType(type);
      return this;
   }
}
