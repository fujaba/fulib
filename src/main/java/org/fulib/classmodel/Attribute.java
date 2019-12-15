package org.fulib.classmodel;

import java.util.ArrayList;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;
import java.util.Objects;

/**
 * <img src='doc-files/classDiagram.png' width='663' alt="doc-files/classDiagram.png">
 */
public class Attribute  
{

   private Clazz clazz = null;

   public Clazz getClazz()
   {
      return this.clazz;
   }

   public Attribute setClazz(Clazz value)
   {
      if (this.clazz == value)
      {
         return this;
      }

      final Clazz oldValue = this.clazz;
      if (this.clazz != null)
      {
         this.clazz = null;
         oldValue.withoutAttributes(this);
      }
      this.clazz = value;
      if (value != null)
      {
         value.withAttributes(this);
      }
      this.firePropertyChange("clazz", oldValue, value);
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

   public Attribute markAsModified()
   {
      return setModified(true);
   }

   public void removeYou()
   {
      this.setClazz(null);

   }

   public static final String PROPERTY_name = "name";

   private String name;

   public String getName()
   {
      return this.name;
   }

   public Attribute setName(String value)
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

   public static final String PROPERTY_type = "type";

   private String type;

   public String getType()
   {
      return this.type;
   }

   public Attribute setType(String value)
   {
      if (Objects.equals(value, this.type))
      {
         return this;
      }

      final String oldValue = this.type;
      this.type = value;
      this.firePropertyChange("type", oldValue, value);
      return this;
   }

   public static final String PROPERTY_initialization = "initialization";

   private String initialization;

   public String getInitialization()
   {
      return this.initialization;
   }

   public Attribute setInitialization(String value)
   {
      if (Objects.equals(value, this.initialization))
      {
         return this;
      }

      final String oldValue = this.initialization;
      this.initialization = value;
      this.firePropertyChange("initialization", oldValue, value);
      return this;
   }

   public static final String PROPERTY_propertyStyle = "propertyStyle";

   private String propertyStyle;

   public String getPropertyStyle()
   {
      return this.propertyStyle;
   }

   public Attribute setPropertyStyle(String value)
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

   public Attribute setModified(boolean value)
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
      result.append(' ').append(this.getType());
      result.append(' ').append(this.getInitialization());
      result.append(' ').append(this.getPropertyStyle());


      return result.substring(1);
   }

   public static final String PROPERTY_clazz = "clazz";

}