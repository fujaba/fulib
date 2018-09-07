package org.fulib.classmodel;

import java.util.ArrayList;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

public class Clazz
{

   private String name;

   public String getName()
   {
      return name;
   }

   public Clazz setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }


   private ClassModel model = null;

   public ClassModel getModel()
   {
      return this.model;
   }

   public Clazz setModel(ClassModel value)
   {
      if (this.model != value)
      {
         ClassModel oldValue = this.model;
         if (this.model != null)
         {
            this.model = null;
            oldValue.withoutClasses(this);
         }
         this.model = value;
         if (value != null)
         {
            value.withClasses(this);
         }
         firePropertyChange("model", oldValue, value);
      }
      return this;
   }



   public static final java.util.ArrayList<Attribute> EMPTY_attributes = new java.util.ArrayList<Attribute>();


   private java.util.ArrayList<Attribute> attributes = null;

   public java.util.ArrayList<Attribute> getAttributes()
   {
      if (this.attributes == null)
      {
         return EMPTY_attributes;
      }

      return this.attributes;
   }

   public Clazz withAttributes(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withAttributes(i);
            }
         }
         else if (item instanceof Attribute)
         {
            if (this.attributes == null)
            {
               this.attributes = new java.util.ArrayList<Attribute>();
            }
            if ( ! this.attributes.contains(item))
            {
               this.attributes.add((Attribute)item);
               ((Attribute)item).setClazz(this);
               firePropertyChange("attributes", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Clazz withoutAttributes(Object... value)
   {
      if (this.attributes == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutAttributes(i);
            }
         }
         else if (item instanceof Attribute)
         {
            if (this.attributes.contains(item))
            {
               this.attributes.remove((Attribute)item);
               ((Attribute)item).setClazz(null);
               firePropertyChange("attributes", item, null);
            }
         }
      }
      return this;
   }


   public static final java.util.ArrayList<AssocRole> EMPTY_roles = new java.util.ArrayList<AssocRole>();


   private java.util.ArrayList<AssocRole> roles = null;

   public java.util.ArrayList<AssocRole> getRoles()
   {
      if (this.roles == null)
      {
         return EMPTY_roles;
      }

      return this.roles;
   }

   public Clazz withRoles(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withRoles(i);
            }
         }
         else if (item instanceof AssocRole)
         {
            if (this.roles == null)
            {
               this.roles = new java.util.ArrayList<AssocRole>();
            }
            if ( ! this.roles.contains(item))
            {
               this.roles.add((AssocRole)item);
               ((AssocRole)item).setClazz(this);
               firePropertyChange("roles", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Clazz withoutRoles(Object... value)
   {
      if (this.roles == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutRoles(i);
            }
         }
         else if (item instanceof AssocRole)
         {
            if (this.roles.contains(item))
            {
               this.roles.remove((AssocRole)item);
               ((AssocRole)item).setClazz(null);
               firePropertyChange("roles", item, null);
            }
         }
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


      return result.substring(1);
   }


}