package org.fulib.classmodel;

import org.fulib.StrUtil;

import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.Collections;

/**
 * <img src='doc-files/classDiagram.png' width='663' alt="doc-files/classDiagram.png">
 */
public class ClassModel  
{

   public static final java.util.ArrayList<Clazz> EMPTY_classes = new java.util.ArrayList<Clazz>()
   { @Override public boolean add(Clazz value){ throw new UnsupportedOperationException("No direct add! Use xy.withClasses(obj)"); }};

   private java.util.ArrayList<Clazz> classes = null;

   public java.util.ArrayList<Clazz> getClasses()
   {
      if (this.classes == null)
      {
         return EMPTY_classes;
      }

      return this.classes;
   }

   public Clazz getClazz(String name)
   {
      for (Clazz clazz : this.getClasses())
      {
         if (StrUtil.stringEquals(clazz.getName(), name))
         {
            return clazz;
         }
      }
      return null;
   }

   public ClassModel withClasses(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withClasses(i);
            }
         }
         else if (item instanceof Clazz)
         {
            if (this.classes == null)
            {
               this.classes = new java.util.ArrayList<Clazz>();
            }
            if ( ! this.classes.contains(item))
            {
               this.classes.add((Clazz)item);
               ((Clazz)item).setModel(this);
               firePropertyChange("classes", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }

   public ClassModel withoutClasses(Object... value)
   {
      if (this.classes == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutClasses(i);
            }
         }
         else if (item instanceof Clazz)
         {
            if (this.classes.contains(item))
            {
               this.classes.remove((Clazz)item);
               ((Clazz)item).setModel(null);
               firePropertyChange("classes", item, null);
            }
         }
      }
      return this;
   }

   public String getPackageSrcFolder()
   {
      return this.getMainJavaDir() + "/" + this.getPackageName().replaceAll("\\.", "/");
   }



   //=======================================================================================================
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

   public void removeYou()
   {
      this.withoutClasses(this.getClasses().clone());


   }

   public static final String PROPERTY_packageName = "packageName";

   private String packageName;

   public String getPackageName()
   {
      return packageName;
   }

   public ClassModel setPackageName(String value)
   {
      if (value == null ? this.packageName != null : ! value.equals(this.packageName))
      {
         String oldValue = this.packageName;
         this.packageName = value;
         firePropertyChange("packageName", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_mainJavaDir = "mainJavaDir";

   private String mainJavaDir;

   public String getMainJavaDir()
   {
      return mainJavaDir;
   }

   public ClassModel setMainJavaDir(String value)
   {
      if (value == null ? this.mainJavaDir != null : ! value.equals(this.mainJavaDir))
      {
         String oldValue = this.mainJavaDir;
         this.mainJavaDir = value;
         firePropertyChange("mainJavaDir", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_defaultRoleType = "defaultRoleType";

   private String defaultRoleType;

   public String getDefaultRoleType()
   {
      return defaultRoleType;
   }

   public ClassModel setDefaultRoleType(String value)
   {
      if (value == null ? this.defaultRoleType != null : ! value.equals(this.defaultRoleType))
      {
         String oldValue = this.defaultRoleType;
         this.defaultRoleType = value;
         firePropertyChange("defaultRoleType", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_defaultPropertyStyle = "defaultPropertyStyle";

   private String defaultPropertyStyle = "POJO";

   public String getDefaultPropertyStyle()
   {
      return defaultPropertyStyle;
   }

   public ClassModel setDefaultPropertyStyle(String value)
   {
      if (value == null ? this.defaultPropertyStyle != null : ! value.equals(this.defaultPropertyStyle))
      {
         String oldValue = this.defaultPropertyStyle;
         this.defaultPropertyStyle = value;
         firePropertyChange("defaultPropertyStyle", oldValue, value);
      }
      return this;
   }

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getPackageName());
      result.append(" ").append(this.getMainJavaDir());
      result.append(" ").append(this.getDefaultRoleType());
      result.append(" ").append(this.getDefaultPropertyStyle());


      return result.substring(1);
   }

   public static final String PROPERTY_classes = "classes";

}