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

   public LinkedHashMap<String, String> readParams()
   {
      if (params == null)
      {
         params = new LinkedHashMap<>();
      }
      return params;
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
      this.setClazz(null);

   }

   private String name;

   public String readName() {
      return name;
   }

   public FMethod writeName(String newName) {
      this.name = newName;
      return this;
   }

   public String readSignature()
   {
      this.readParams().remove("this");
      String paramTypes = String.join(",", this.readParams().values());
      String result = String.format(Parser.METHOD + ":%s(%s)",
            this.readName(),
            paramTypes);
      return result;
   }

   private String returnType;

   public String readReturnType() {
      return this.returnType;
   }

   public FMethod writeReturnType(String value) {
      this.returnType = value;
      return this;
   }

   public static final String PROPERTY_declaration = "declaration";

   // private String declaration;

   public FMethod setDeclaration(String value) // no fulib
   {
      // a declaration looks like
      // public void m(T1 p1, T2 p2)
      if (value == null) {
         if (this.getDeclaration() == null) {
            return this;
         } else {
            this.name = null;
            this.returnType = "void";
            this.readParams().clear();
         }
      }

      if (! value.equals(this.getDeclaration()))
      {
         String oldValue = this.getDeclaration();
         int pos = value.indexOf('(');
         String namePart = value.substring(0, pos);
         String params = value.substring(pos+1, value.length()-1);
         String[] split = namePart.split(" ");
         String modifier = split[0];
         if (modifier.startsWith("@")) {
            int publicPos = modifier.indexOf("public");
            String annos = modifier.substring(0, publicPos-1);
            setAnnotations(annos);
         }
         String newName = split[2];
         this.name = newName;
         String newReturnType = split[1];
         this.returnType = newReturnType;
         this.setParamsByString(params);
         firePropertyChange("declaration", oldValue, value);
      }
      return this;
   }

   public String getDeclaration() // no fulib
   {
      // e.g. public void m(T1 p1, T2 p2)
      if (this.name == null) {
         return null;
      }

      String declaration = String.format("public %s %s(%s)",
            this.returnType, this.name, this.readFullParamsString());

      if (this.annotations != null) {
         declaration = this.annotations + "\n   " + declaration;
      }

      return declaration;
   }

   public String readFullParamsString()
   {
      ArrayList<String> paramList = new ArrayList<>();
      for (Map.Entry<String, String> entry : this.readParams().entrySet())
      {
         String paramName = entry.getKey();
         String paramType = entry.getValue();
         paramList.add(paramType + " " + paramName);
      }

      String result = String.join(", ", paramList);

      return result;
   }

   public FMethod setParamsByString(String params)
   {
      String[] split = params.split(", ");
      this.readParams().clear();
      for (String s : split)
      {
         if (s.equals("")) {
            break;
         }
         String[] pair = s.split(" ");
         this.readParams().put(pair[1], pair[0]);
      }

      return this;
   }

   public static final String PROPERTY_clazz = "clazz";

   private Clazz clazz = null;

   public Clazz getClazz()
   {
      return this.clazz;
   }

   public FMethod setClazz(Clazz value)
   {
      if (this.clazz != value)
      {
         Clazz oldValue = this.clazz;
         if (this.clazz != null)
         {
            this.clazz = null;
            oldValue.withoutMethods(this);
         }
         this.clazz = value;
         if (value != null)
         {
            value.withMethods(this);
         }
         firePropertyChange("clazz", oldValue, value);
      }
      return this;
   }

   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getDeclaration());
      result.append(" ").append(this.getMethodBody());
      result.append(" ").append(this.getAnnotations());


      return result.substring(1);
   }

   private String declaration;

   public static final String PROPERTY_modified = "modified";

   private boolean modified = false;

   public boolean getModified()
   {
      return modified;
   }

   public FMethod setModified(boolean value)
   {
      if (value != this.modified)
      {
         boolean oldValue = this.modified;
         this.modified = value;
         firePropertyChange("modified", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_annotations = "annotations";

   private String annotations;

   public String getAnnotations()
   {
      return annotations;
   }

   public FMethod setAnnotations(String value)
   {
      if (value == null ? this.annotations != null : ! value.equals(this.annotations))
      {
         String oldValue = this.annotations;
         this.annotations = value;
         firePropertyChange("annotations", oldValue, value);
      }
      return this;
   }

}