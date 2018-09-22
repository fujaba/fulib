package org.fulib.classmodel;

import java.util.ArrayList;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

/**
 * <img src='doc-files/classDiagram.png' width='663'/>
 */
public class AssocRole 
{

   private String name;

   public String getName()
   {
      return name;
   }

   public AssocRole setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }


   private int cardinality;

   public int getCardinality()
   {
      return cardinality;
   }

   public AssocRole setCardinality(int value)
   {
      if (value != this.cardinality)
      {
         int oldValue = this.cardinality;
         this.cardinality = value;
         firePropertyChange("cardinality", oldValue, value);
      }
      return this;
   }


   private String roleType;

   public String getRoleType()
   {
      return roleType;
   }

   public AssocRole setRoleType(String value)
   {
      if (value == null ? this.roleType != null : ! value.equals(this.roleType))
      {
         String oldValue = this.roleType;
         this.roleType = value;
         firePropertyChange("roleType", oldValue, value);
      }
      return this;
   }


   private Clazz clazz = null;

   public Clazz getClazz()
   {
      return this.clazz;
   }

   public AssocRole setClazz(Clazz value)
   {
      if (this.clazz != value)
      {
         Clazz oldValue = this.clazz;
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
         firePropertyChange("clazz", oldValue, value);
      }
      return this;
   }



private AssocRole other = null;

public AssocRole getOther()
   {
      return this.other;
   }

public AssocRole setOther(AssocRole value)
   {
      if (this.other != value)
      {
         AssocRole oldValue = this.other;
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
         firePropertyChange("other", oldValue, value);
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


   private boolean modified = false;

   public boolean getModified()
   {
      return modified;
   }

   public AssocRole setModified(boolean value)
   {
      if (value != this.modified)
      {
         boolean oldValue = this.modified;
         this.modified = value;
         firePropertyChange("modified", oldValue, value);
      }
      return this;
   }

   public AssocRole markAsModified()
   {
      return this.setModified(true);
   }


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getName());
      result.append(" ").append(this.getRoleType());


      return result.substring(1);
   }

   public void removeYou()
   {
      this.setClazz(null);
      this.setOther(null);
      this.setOther(null);

   }

}