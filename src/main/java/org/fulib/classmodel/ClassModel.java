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
import org.fulib.classmodel.util.ClazzSet;
import org.fulib.classmodel.Clazz;

public  class ClassModel implements SendableEntity
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
      withoutClasses(this.getClasses().toArray(new Clazz[this.getClasses().size()]));
      firePropertyChange("REMOVE_YOU", this, null);
   }

   
   //==========================================================================
   
   public static final String PROPERTY_PACKAGENAME = "packageName";
   
   private String packageName;

   public String getPackageName()
   {
      return this.packageName;
   }
   
   public void setPackageName(String value)
   {
      if ( ! EntityUtil.stringEquals(this.packageName, value)) {
      
         String oldValue = this.packageName;
         this.packageName = value;
         this.firePropertyChange(PROPERTY_PACKAGENAME, oldValue, value);
      }
   }
   
   public ClassModel withPackageName(String value)
   {
      setPackageName(value);
      return this;
   } 


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();
      
      result.append(" ").append(this.getPackageName());
      result.append(" ").append(getMainJavaDir());
      result.append(" ").append(this.getMainJavaDir());
      result.append(" ").append(this.getTestJavaDir());
      return result.substring(1);
   }


   
   /********************************************************************
    * <pre>
    *              one                       many
    * ClassModel ----------------------------------- Clazz
    *              model                   classes
    * </pre>
    */
   
   public static final String PROPERTY_CLASSES = "classes";

   private ClazzSet classes = null;
   
   public ClazzSet getClasses()
   {
      if (this.classes == null)
      {
         return ClazzSet.EMPTY_SET;
      }
   
      return this.classes;
   }

   public ClassModel withClasses(Clazz... value)
   {
      if(value==null){
         return this;
      }
      for (Clazz item : value)
      {
         if (item != null)
         {
            if (this.classes == null)
            {
               this.classes = new ClazzSet();
            }
            
            boolean changed = this.classes.add (item);

            if (changed)
            {
               item.withModel(this);
               firePropertyChange(PROPERTY_CLASSES, null, item);
            }
         }
      }
      return this;
   } 

   public ClassModel withoutClasses(Clazz... value)
   {
      for (Clazz item : value)
      {
         if ((this.classes != null) && (item != null))
         {
            if (this.classes.remove(item))
            {
               item.setModel(null);
               firePropertyChange(PROPERTY_CLASSES, item, null);
            }
         }
      }
      return this;
   }

   public Clazz createClasses()
   {
      Clazz value = new Clazz();
      withClasses(value);
      return value;
   } 

   
   //==========================================================================
   
   public static final String PROPERTY_CODEDIR = "codeDir";
   
   //==========================================================================
   
   public static final String PROPERTY_MAINJAVADIR = "mainJavaDir";
   
   private String mainJavaDir = "src/main/java";

   public String getMainJavaDir()
   {
      return this.mainJavaDir;
   }
   
   public void setMainJavaDir(String value)
   {
      if ( ! EntityUtil.stringEquals(this.mainJavaDir, value)) {
      
         String oldValue = this.mainJavaDir;
         this.mainJavaDir = value;
         this.firePropertyChange(PROPERTY_MAINJAVADIR, oldValue, value);
      }
   }
   
   public ClassModel withSrcFolder(String value)
   {
      setMainJavaDir(value);
      return this;
   }

   public String getPackageSrcFolder()
   {
      return this.getMainJavaDir() + "/" + this.getPackageName().replaceAll("\\.", "/");
   }

   public String getTestPackageDirName()
   {
      return this.getTestJavaDir() + "/" + this.getPackageName().replaceAll("\\.", "/");
   }
   
   //==========================================================================
   
   public static final String PROPERTY_TESTJAVADIR = "testJavaDir";
   
   private String testJavaDir = "src/test/java";

   public String getTestJavaDir()
   {
      return this.testJavaDir;
   }
   
   public void setTestJavaDir(String value)
   {
      if ( ! EntityUtil.stringEquals(this.testJavaDir, value)) {
      
         String oldValue = this.testJavaDir;
         this.testJavaDir = value;
         this.firePropertyChange(PROPERTY_TESTJAVADIR, oldValue, value);
      }
   }
   
   public ClassModel withTestJavaDir(String value)
   {
      setTestJavaDir(value);
      return this;
   } 

   
   //==========================================================================
   
   public ClassModel withMainJavaDir(String value)
   {
      setMainJavaDir(value);
      return this;
   } 
}
