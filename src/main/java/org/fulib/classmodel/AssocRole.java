package org.fulib.classmodel;

import org.fulib.builder.Type;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class AssocRole
{
   // =============== Constants ===============

   public static final String PROPERTY_name = "name";
   public static final String PROPERTY_cardinality = "cardinality";
   /** @deprecated since 1.2; use {@link #PROPERTY_collectionType} instead */
   @Deprecated
   public static final String PROPERTY_roleType = "roleType";
   /** @since 1.2 */
   public static final String PROPERTY_collectionType = "collectionType";
   public static final String PROPERTY_aggregation = "aggregation";
   public static final String PROPERTY_propertyStyle = "propertyStyle";
   public static final String PROPERTY_modified = "modified";
   public static final String PROPERTY_clazz = "clazz";
   public static final String PROPERTY_other = "other";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private Clazz clazz;
   private AssocRole other;
   private String name;
   private int cardinality;
   private CollectionType collectionType;
   private boolean aggregation;
   private String propertyStyle;
   private boolean modified;

   // =============== Properties ===============

   public Clazz getClazz()
   {
      return this.clazz;
   }

   public AssocRole setClazz(Clazz value)
   {
      if (this.clazz == value)
      {
         return this;
      }

      final Clazz oldValue = this.clazz;
      if (this.clazz != null)
      {
         this.clazz = null;
         oldValue.withoutRoles(this);
      }
      this.clazz = value;
      if (value != null)
      {
         value.withRoles(this);
      }
      this.firePropertyChange(PROPERTY_clazz, oldValue, value);
      return this;
   }

   public AssocRole getOther()
   {
      return this.other;
   }

   public AssocRole setOther(AssocRole value)
   {
      if (this.other == value)
      {
         return this;
      }

      final AssocRole oldValue = this.other;
      if (this.other != null)
      {
         this.other = null;
         oldValue.setOther(null);
      }
      this.other = value;
      if (value != null)
      {
         value.setOther(this);
      }
      this.firePropertyChange(PROPERTY_other, oldValue, value);
      return this;
   }

   /**
    * @return a string that uniquely identifies this role within the enclosing class model
    *
    * @since 1.2
    */
   public String getId()
   {
      final String className = this.getClazz() == null ? "___" : this.getClazz().getName();
      return className + "_" + this.getName();
   }

   public String getName()
   {
      return this.name;
   }

   public AssocRole setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_name, oldValue, value);
      return this;
   }

   public int getCardinality()
   {
      return this.cardinality;
   }

   public AssocRole setCardinality(int value)
   {
      if (value == this.cardinality)
      {
         return this;
      }

      final int oldValue = this.cardinality;
      this.cardinality = value;
      this.firePropertyChange(PROPERTY_cardinality, oldValue, value);
      return this;
   }

   /**
    * @return a boolean indicating whether this is a to-one role
    *
    * @since 1.2
    */
   public boolean isToOne()
   {
      return this.cardinality == Type.ONE;
   }

   /**
    * @return a boolean indicating whether this is a to-many role
    *
    * @since 1.2
    */
   public boolean isToMany()
   {
      return this.cardinality != Type.ONE;
   }

   /**
    * @return the collection type
    *
    * @since 1.2
    */
   public CollectionType getCollectionType()
   {
      return this.collectionType;
   }

   /**
    * @param value
    *    the collection type
    *
    * @return this
    *
    * @since 1.2
    */
   public AssocRole setCollectionType(CollectionType value)
   {
      if (Objects.equals(value, this.collectionType))
      {
         return this;
      }

      final CollectionType oldValue = this.collectionType;
      this.collectionType = value;
      this.firePropertyChange(PROPERTY_collectionType, oldValue, value);
      return this;
   }

   /**
    * @return the collection type
    *
    * @deprecated since 1.2; use {@link #getCollectionType()} instead
    */
   @Deprecated
   public String getRoleType()
   {
      return this.getCollectionType().getImplTemplate();
   }

   /**
    * @param value
    *    the new collection type
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setCollectionType(CollectionType)} with {@link CollectionType#of(String)} instead
    */
   @Deprecated
   public AssocRole setRoleType(String value)
   {
      return this.setCollectionType(CollectionType.of(value));
   }

   /**
    * @return a boolean indicating whether this role is an aggregation,
    * i.e. whether the target objects are {@code removeYou}'d completely when using {@code without*} methods or
    * {@code removeYou} on the source object
    */
   public boolean getAggregation()
   {
      return this.aggregation;
   }

   /**
    * @param value
    *    a boolean indicating whether this role is an aggregation,
    *    i.e. whether the target objects are {@code removeYou}'d completely when using {@code without*} methods or
    *    {@code removeYou} on the source object
    *
    * @return this
    */
   public AssocRole setAggregation(boolean value)
   {
      if (value == this.aggregation)
      {
         return this;
      }

      final boolean oldValue = this.aggregation;
      this.aggregation = value;
      this.firePropertyChange(PROPERTY_aggregation, oldValue, value);
      return this;
   }

   /**
    * @return the property style.
    * Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.
    */
   public String getPropertyStyle()
   {
      return this.propertyStyle;
   }

   /**
    * @param value
    *    the property style.
    *    Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.
    *
    * @return this
    */
   public AssocRole setPropertyStyle(String value)
   {
      if (Objects.equals(value, this.propertyStyle))
      {
         return this;
      }

      final String oldValue = this.propertyStyle;
      this.propertyStyle = value;
      this.firePropertyChange(PROPERTY_propertyStyle, oldValue, value);
      return this;
   }

   /**
    * @return a boolean indicating whether this role was modified. For internal use only.
    */
   public boolean getModified()
   {
      return this.modified;
   }

   /**
    * @param value
    *    a boolean indicating whether this role was modified. For internal use only.
    *
    * @return this
    */
   public AssocRole setModified(boolean value)
   {
      if (value == this.modified)
      {
         return this;
      }

      final boolean oldValue = this.modified;
      this.modified = value;
      this.firePropertyChange(PROPERTY_modified, oldValue, value);
      return this;
   }

   // =============== Methods ===============

   /**
    * Marks this role as modified.
    * Equivalent to calling {@link #setModified(boolean)} with a value of {@code true}.
    *
    * @return this instance, to allow method chaining
    */
   public AssocRole markAsModified()
   {
      return this.setModified(true);
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public void removeYou()
   {
      this.setClazz(null);
      this.setOther(null);
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getPropertyStyle());
      return result.substring(1);
   }
}
