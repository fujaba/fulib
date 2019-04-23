package org.fulib.classmodel;

import org.fulib.Parser;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class FMethod  
{

   private LinkedHashMap<String, String> params;

   public LinkedHashMap<String, String> getParams()
   {
      if (params == null)
      {
         params = new LinkedHashMap<>();
      }
      return params;
   }



   public static final String PROPERTY_name = "name";

   private String name;

   public String getName()
   {
      return name;
   }

   public FMethod setName(String value)
   {
      if (value == null ? this.name != null : ! value.equals(this.name))
      {
         String oldValue = this.name;
         this.name = value;
         firePropertyChange("name", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_returnType = "returnType";

   private String returnType;

   public String getReturnType()
   {
      return returnType;
   }

   public FMethod setReturnType(String value)
   {
      if (value == null ? this.returnType != null : ! value.equals(this.returnType))
      {
         String oldValue = this.returnType;
         this.returnType = value;
         firePropertyChange("returnType", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_methodBody = "methodBody";

   private String methodBody;

   public String getMethodBody()
   {
      return methodBody;
   }

   public FMethod setMethodBody(String value)
   {
      if (value == null ? this.methodBody != null : ! value.equals(this.methodBody))
      {
         String oldValue = this.methodBody;
         this.methodBody = value;
         firePropertyChange("methodBody", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_className = "className";

   private String className;

   public String getClassName()
   {
      return className;
   }

   public FMethod setClassName(String value)
   {
      if (value == null ? this.className != null : ! value.equals(this.className))
      {
         String oldValue = this.className;
         this.className = value;
         firePropertyChange("className", oldValue, value);
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

   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
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
      result.append(" ").append(this.getReturnType());
      result.append(" ").append(this.getMethodBody());
      result.append(" ").append(this.getClassName());
      result.append(" ").append(this.getPackageName());
      result.append(" ").append(this.getJavaSrcDir());


      return result.substring(1);
   }

   public void removeYou()
   {
   }


   public static final String PROPERTY_packageName = "packageName";

   private String packageName;

   public String getPackageName()
   {
      return packageName;
   }

   public FMethod setPackageName(String value)
   {
      if (value == null ? this.packageName != null : ! value.equals(this.packageName))
      {
         String oldValue = this.packageName;
         this.packageName = value;
         firePropertyChange("packageName", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_javaSrcDir = "javaSrcDir";

   private String javaSrcDir;

   public String getJavaSrcDir()
   {
      return javaSrcDir;
   }

   public FMethod setJavaSrcDir(String value)
   {
      if (value == null ? this.javaSrcDir != null : ! value.equals(this.javaSrcDir))
      {
         String oldValue = this.javaSrcDir;
         this.javaSrcDir = value;
         firePropertyChange("javaSrcDir", oldValue, value);
      }
      return this;
   }


   public String getSignature()
   {
      this.getParams().remove("this");
      String paramTypes = String.join(",", this.getParams().values());
      String result = String.format(Parser.METHOD + ":%s(%s)",
            this.getName(),
            paramTypes);
      return result;
   }


   public String getDeclaration()
   {
      ArrayList<String> paramList = new ArrayList<>();
      for (Map.Entry<String, String> entry : this.getParams().entrySet())
      {
         String oneParam = entry.getValue() + " " + entry.getKey();
         paramList.add(oneParam);
      }

      String paramTypes = String.join(", ", paramList);

      String result = String.format("public %s %s(%s)",
            this.getReturnType(),
            this.getName(),
            paramTypes);
      return result;
   }
}