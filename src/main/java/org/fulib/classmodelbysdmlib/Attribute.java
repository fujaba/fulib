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
   
package org.fulib.classmodelbysdmlib;

import de.uniks.networkparser.interfaces.SendableEntity;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import de.uniks.networkparser.EntityUtil;

public  class Attribute implements SendableEntity
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
      firePropertyChange("REMOVE_YOU", this, null);
   }

   
   //==========================================================================
   
   public static final String PROPERTY_INITIALIZATION = "initialization";
   
   private String initialization;

   public String getInitialization()
   {
      return this.initialization;
   }
   
   public void setInitialization(String value)
   {
      if ( ! EntityUtil.stringEquals(this.initialization, value)) {
      
         String oldValue = this.initialization;
         this.initialization = value;
         this.firePropertyChange(PROPERTY_INITIALIZATION, oldValue, value);
      }
   }
   
   public Attribute withInitialization(String value)
   {
      setInitialization(value);
      return this;
   } 


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();
      
      result.append(" ").append(this.getInitialization());
      result.append(" ").append(this.getName());
      result.append(" ").append(this.getType());
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
   
   public Attribute withName(String value)
   {
      setName(value);
      return this;
   } 

   
   //==========================================================================
   
   public static final String PROPERTY_TYPE = "type";
   
   private String type;

   public String getType()
   {
      return this.type;
   }
   
   public void setType(String value)
   {
      if ( ! EntityUtil.stringEquals(this.type, value)) {
      
         String oldValue = this.type;
         this.type = value;
         this.firePropertyChange(PROPERTY_TYPE, oldValue, value);
      }
   }
   
   public Attribute withType(String value)
   {
      setType(value);
      return this;
   } 

   
   /********************************************************************
    * <pre>
    *              many                       one
    * Attribute ----------------------------------- Clazz
    *              attributes                   clazz
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
            oldValue.withoutAttributes(this);
         }
         
         this.clazz = value;
         
         if (value != null)
         {
            value.withAttributes(this);
         }
         
         firePropertyChange(PROPERTY_CLAZZ, oldValue, value);
         changed = true;
      }
      
      return changed;
   }

   public Attribute withClazz(Clazz value)
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
}
