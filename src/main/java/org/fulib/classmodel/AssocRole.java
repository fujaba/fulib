package org.fulib.classmodel;

import org.fulib.builder.Type;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Objects;

/**
 * <img src='doc-files/classDiagram.png' width='663' alt="doc-files/classDiagram.png">
 */
public class AssocRole
{
   // =============== Constants ===============

   public static final String PROPERTY_name = "name";
   public static final String PROPERTY_cardinality = "cardinality";
   @Deprecated
   public static final String PROPERTY_roleType = "roleType";
   public static final String PROPERTY_collectionType = "collectionType";
   public static final String PROPERTY_aggregation = "aggregation";
   public static final String PROPERTY_propertyStyle = "propertyStyle";
   public static final String PROPERTY_modified = "modified";
   public static final String PROPERTY_clazz = "clazz";
   public static final String PROPERTY_other = "other";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners = null;

   private Clazz clazz = null;
   private AssocRole other = null;
   private String name;
   private int cardinality;
   private String collectionType;
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

   public boolean isToOne()
   {
      return this.cardinality == Type.ONE;
   }

   public boolean isToMany()
   {
      return this.cardinality != Type.ONE;
   }

   /**
    * @return the name of the collection interface derived from {@link #getCollectionType()},
    * i.e. either "List", "Set" or "Collection".
    *
    * @since 1.2
    */
   public String getCollectionInterfaceName()
   {
      return ClassModel.deriveCollectionInterfaceName(this.getCollectionType());
   }

   /**
    * @return the collection type
    *
    * @since 1.2
    */
   public String getCollectionType()
   {
      return this.collectionType;
   }

   /**
    * @param value
    *    the new collection type
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public AssocRole setCollectionType(String value)
   {
      if (Objects.equals(value, this.collectionType))
      {
         return this;
      }

      final String oldValue = this.collectionType;
      this.collectionType = value;
      this.firePropertyChange(PROPERTY_collectionType, oldValue, value);
      return this;
   }

   /**
    * @param value
    *    the new collection class
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public AssocRole setCollectionClass(@SuppressWarnings("rawtypes") Class<? extends Collection> value)
   {
      return this.setCollectionType(ClassModel.deriveCollectionType(value));
   }

   /**
    * @return the collection type
    *
    * @deprecated since 1.2; use {@link #getCollectionType()} instead
    */
   @Deprecated
   public String getRoleType()
   {
      return this.getCollectionType();
   }

   /**
    * @param value
    *    the new collection type
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setCollectionType(String)} instead
    */
   @Deprecated
   public AssocRole setRoleType(String value)
   {
      return this.setCollectionType(value);
   }

   public boolean getAggregation()
   {
      return this.aggregation;
   }

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

   public String getPropertyStyle()
   {
      return this.propertyStyle;
   }

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

   public boolean isJavaFX()
   {
      return Type.JAVA_FX.equals(this.getPropertyStyle());
   }

   public boolean getModified()
   {
      return this.modified;
   }

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
      this.setOther(null);
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getCollectionType());
      result.append(' ').append(this.getPropertyStyle());
      return result.substring(1);
   }
}
