package org.fulib.classmodel;

import java.util.ArrayList;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

/**
 * <img src='doc-files/classDiagram.png' width='663'/>
 */
public class Attribute 
{

   private String name;

   public String getName()
   {
      return name;
   }

   public Attribute setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }


   private String type;

   public String getType()
   {
      return type;
   }

   public Attribute setType(String value)
   {
      if (value == null ? this.type != null : ! value.equals(this.type))
      {
         String oldValue = this.type;
         this.type = value;
         firePropertyChange("type", oldValue, value);
      }
      return this;
   }


   private String initialization;

   public String getInitialization()
   {
      return initialization;
   }

   public Attribute setInitialization(String value)
   {
      if (value == null ? this.initialization != null : ! value.equals(this.initialization))
      {
         String oldValue = this.initialization;
         this.initialization = value;
         firePropertyChange("initialization", oldValue, value);
      }
      return this;
   }


   private Clazz clazz = null;

   public Clazz getClazz()
   {
      return this.clazz;
   }

   public Attribute setClazz(Clazz value)
   {
      if (this.clazz != value)
      {
         Clazz oldValue = this.clazz;
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
         firePropertyChange("clazz", oldValue, value);
      }
      return this;
   }



   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null)
      {
         listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getName());
      result.append(" ").append(this.getType());
      result.append(" ").append(this.getInitialization());


      return result.substring(1);
   }


   private boolean modified = false;

   public boolean getModified()
   {
      return modified;
   }

   public Attribute setModified(boolean value)
   {
      if (value != this.modified)
      {
         boolean oldValue = this.modified;
         this.modified = value;
         firePropertyChange("modified", oldValue, value);
      }
      return this;
   }

   public Attribute markAsModified()
   {
      return setModified(true);
   }


}