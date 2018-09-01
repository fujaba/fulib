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
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.util.AssocRoleSet;

public  class AssocRole implements SendableEntity
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
      setClazz(null);
      setOther(null);
      firePropertyChange("REMOVE_YOU", this, null);
   }

   
   //==========================================================================
   
   public static final String PROPERTY_CARDINALITY = "cardinality";
   
   private int cardinality;

   public int getCardinality()
   {
      return this.cardinality;
   }
   
   public void setCardinality(int value)
   {
      if (this.cardinality != value) {
      
         int oldValue = this.cardinality;
         this.cardinality = value;
         this.firePropertyChange(PROPERTY_CARDINALITY, oldValue, value);
      }
   }
   
   public AssocRole withCardinality(int value)
   {
      setCardinality(value);
      return this;
   } 


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();
      
      result.append(" ").append(this.getCardinality());
      result.append(" ").append(this.getName());
      return result.substring(1);
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
   
   public AssocRole withName(String value)
   {
      setName(value);
      return this;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       one
    * AssocRole ----------------------------------- Clazz
    *              roles                   clazz
    * </pre>
    */
   
   public static final String PROPERTY_CLAZZ = "clazz";

   private Clazz clazz = null;

   public Clazz getClazz()
   {
      return this.clazz;
   }

   public boolean setClazz(Clazz value)
   {
      boolean changed = false;
      
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
         
         firePropertyChange(PROPERTY_CLAZZ, oldValue, value);
         changed = true;
      }
      
      return changed;
   }

   public AssocRole withClazz(Clazz value)
   {
      setClazz(value);
      return this;
   } 

   public Clazz createClazz()
   {
      Clazz value = new Clazz();
      withClazz(value);
      return value;
   } 

   
   /********************************************************************
    * <pre>
    *              one                       one
    * AssocRole ----------------------------------- AssocRole
    *              other                   other
    * </pre>
    */
   
   public static final String PROPERTY_OTHER = "other";

   private AssocRole other = null;

   public AssocRole getOther()
   {
      return this.other;
   }
   public AssocRoleSet getOtherTransitive()
   {
      AssocRoleSet result = new AssocRoleSet().with(this);
      return result.getOtherTransitive();
   }


   public boolean setOther(AssocRole value)
   {
      boolean changed = false;
      
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
            value.withOther(this);
         }
         
         firePropertyChange(PROPERTY_OTHER, oldValue, value);
         changed = true;
      }
      
      return changed;
   }

   public AssocRole withOther(AssocRole value)
   {
      setOther(value);
      return this;
   } 

   public AssocRole createOther()
   {
      AssocRole value = new AssocRole();
      withOther(value);
      return value;
   } 
}
