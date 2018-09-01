/*
   Copyright (c) 2018 zuend
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
   
package org.fulib.classmodel;

import de.uniks.networkparser.interfaces.SendableEntity;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import de.uniks.networkparser.EntityUtil;
import org.fulib.classmodel.util.AttributeSet;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.util.AssocRoleSet;
import org.fulib.classmodel.AssocRole;

public  class Clazz implements SendableEntity
{

   
   //==========================================================================
   
   protected PropertyChangeSupport listeners = null;
   
   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null) {
   		listeners.firePropertyChange(propertyName, oldValue, newValue);
   		return true;
   	}
   	return false;
   }
   
   public boolean addPropertyChangeListener(PropertyChangeListener listener) 
   {
   	if (listeners == null) {
   		listeners = new PropertyChangeSupport(this);
   	}
   	listeners.addPropertyChangeListener(listener);
   	return true;
   }
   
   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
   	if (listeners == null) {
   		listeners = new PropertyChangeSupport(this);
   	}
   	listeners.addPropertyChangeListener(propertyName, listener);
   	return true;
   }
   
   public boolean removePropertyChangeListener(PropertyChangeListener listener) {
   	if (listeners != null) {
   		listeners.removePropertyChangeListener(listener);
   	}
   	return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
   	if (listeners != null) {
   		listeners.removePropertyChangeListener(propertyName, listener);
   	}
   	return true;
   }

   
   //==========================================================================
   
   
   public void removeYou()
   {
      withoutAttributes(this.getAttributes().toArray(new Attribute[this.getAttributes().size()]));
      setModel(null);
      withoutRoles(this.getRoles().toArray(new AssocRole[this.getRoles().size()]));
      firePropertyChange("REMOVE_YOU", this, null);
   }

   
   //==========================================================================
   
   public static final String PROPERTY_NAME = "name";
   
   private String name;

   public String getName()
   {
      return this.name;
   }
   
   public void setName(String value)
   {
      if ( ! EntityUtil.stringEquals(this.name, value)) {
      
         String oldValue = this.name;
         this.name = value;
         this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      }
   }
   
   public Clazz withName(String value)
   {
      setName(value);
      return this;
   } 


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();
      
      result.append(" ").append(this.getName());
      return result.substring(1);
   }


   
   /********************************************************************
    * <pre>
    *              one                       many
    * Clazz ----------------------------------- Attribute
    *              clazz                   attributes
    * </pre>
    */
   
   public static final String PROPERTY_ATTRIBUTES = "attributes";

   private AttributeSet attributes = null;
   
   public AttributeSet getAttributes()
   {
      if (this.attributes == null)
      {
         return AttributeSet.EMPTY_SET;
      }
   
      return this.attributes;
   }

   public Clazz withAttributes(Attribute... value)
   {
      if(value==null){
         return this;
      }
      for (Attribute item : value)
      {
         if (item != null)
         {
            if (this.attributes == null)
            {
               this.attributes = new AttributeSet();
            }
            
            boolean changed = this.attributes.add (item);

            if (changed)
            {
               item.withClazz(this);
               firePropertyChange(PROPERTY_ATTRIBUTES, null, item);
            }
         }
      }
      return this;
   } 

   public Clazz withoutAttributes(Attribute... value)
   {
      for (Attribute item : value)
      {
         if ((this.attributes != null) && (item != null))
         {
            if (this.attributes.remove(item))
            {
               item.setClazz(null);
               firePropertyChange(PROPERTY_ATTRIBUTES, item, null);
            }
         }
      }
      return this;
   }

   public Attribute createAttributes()
   {
      Attribute value = new Attribute();
      withAttributes(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       one
    * Clazz ----------------------------------- ClassModel
    *              classes                   model
    * </pre>
    */
   
   public static final String PROPERTY_MODEL = "model";

   private ClassModel model = null;

   public ClassModel getModel()
   {
      return this.model;
   }

   public boolean setModel(ClassModel value)
   {
      boolean changed = false;
      
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
         
         firePropertyChange(PROPERTY_MODEL, oldValue, value);
         changed = true;
      }
      
      return changed;
   }

   public Clazz withModel(ClassModel value)
   {
      setModel(value);
      return this;
   } 

   public ClassModel createModel()
   {
      ClassModel value = new ClassModel();
      withModel(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              one                       many
    * Clazz ----------------------------------- AssocRole
    *              clazz                   roles
    * </pre>
    */
   
   public static final String PROPERTY_ROLES = "roles";

   private AssocRoleSet roles = null;
   
   public AssocRoleSet getRoles()
   {
      if (this.roles == null)
      {
         return AssocRoleSet.EMPTY_SET;
      }
   
      return this.roles;
   }

   public Clazz withRoles(AssocRole... value)
   {
      if(value==null){
         return this;
      }
      for (AssocRole item : value)
      {
         if (item != null)
         {
            if (this.roles == null)
            {
               this.roles = new AssocRoleSet();
            }
            
            boolean changed = this.roles.add (item);

            if (changed)
            {
               item.withClazz(this);
               firePropertyChange(PROPERTY_ROLES, null, item);
            }
         }
      }
      return this;
   } 

   public Clazz withoutRoles(AssocRole... value)
   {
      for (AssocRole item : value)
      {
         if ((this.roles != null) && (item != null))
         {
            if (this.roles.remove(item))
            {
               item.setClazz(null);
               firePropertyChange(PROPERTY_ROLES, item, null);
            }
         }
      }
      return this;
   }

   public AssocRole createRoles()
   {
      AssocRole value = new AssocRole();
      withRoles(value);
      return value;
   } 
}
