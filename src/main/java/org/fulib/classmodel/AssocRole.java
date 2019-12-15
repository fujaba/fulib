package org.fulib.classmodel;

import java.util.ArrayList;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;
import java.util.Objects;

/**
 * <img src='doc-files/classDiagram.png' width='663' alt="doc-files/classDiagram.png">
 */
public class AssocRole  
{

   private Clazz clazz = null;

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
      this.firePropertyChange("clazz", oldValue, value);
      return this;
   }

private AssocRole other = null;

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
      this.firePropertyChange("other", oldValue, value);
      return this;
   }

   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
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

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   public AssocRole markAsModified()
   {
      return this.setModified(true);
   }

   public void removeYou()
   {
      this.setClazz(null);
      this.setOther(null);
      this.setOther(null);

   }

   public static final String PROPERTY_name = "name";

   private String name;

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
      this.firePropertyChange("name", oldValue, value);
      return this;
   }

   public static final String PROPERTY_cardinality = "cardinality";

   private int cardinality;

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
      this.firePropertyChange("cardinality", oldValue, value);
      return this;
   }

   public static final String PROPERTY_roleType = "roleType";

   private String roleType;

   public String getRoleType()
   {
      return this.roleType;
   }

   public AssocRole setRoleType(String value)
   {
      if (Objects.equals(value, this.roleType))
      {
         return this;
      }

      final String oldValue = this.roleType;
      this.roleType = value;
      this.firePropertyChange("roleType", oldValue, value);
      return this;
   }

   public static final String PROPERTY_aggregation = "aggregation";

   private boolean aggregation;

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
      this.firePropertyChange("aggregation", oldValue, value);
      return this;
   }

   public static final String PROPERTY_propertyStyle = "propertyStyle";

   private String propertyStyle;

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
      this.firePropertyChange("propertyStyle", oldValue, value);
      return this;
   }

   public static final String PROPERTY_modified = "modified";

   private boolean modified;

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
      this.firePropertyChange("modified", oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();

      result.append(' ').append(this.getName());
      result.append(' ').append(this.getRoleType());
      result.append(' ').append(this.getPropertyStyle());


      return result.substring(1);
   }

   public static final String PROPERTY_clazz = "clazz";

public static final String PROPERTY_other = "other";

}