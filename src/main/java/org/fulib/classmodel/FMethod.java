package org.fulib.classmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FMethod
{
   // =============== Constants ===============

   public static final String PROPERTY_methodBody  = "methodBody";
   public static final String PROPERTY_declaration = "declaration";
   public static final String PROPERTY_clazz       = "clazz";
   public static final String PROPERTY_modified    = "modified";
   public static final String PROPERTY_annotations = "annotations";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners = null;

   private Clazz                         clazz = null;
   private String                        name;
   private String                        annotations;
   private LinkedHashMap<String, String> params;
   private String                        returnType;
   private String                        methodBody;

   private boolean modified;

   // =============== Properties ===============

   public Clazz getClazz()
   {
      return this.clazz;
   }

   public FMethod setClazz(Clazz value)
   {
      if (this.clazz == value)
      {
         return this;
      }

      final Clazz oldValue = this.clazz;
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
      this.firePropertyChange("clazz", oldValue, value);
      return this;
   }

   public String getName()
   {
      return this.name;
   }

   public FMethod setName(String newName)
   {
      this.name = newName;
      return this;
   }

   public String getDeclaration() // no fulib
   {
      // e.g. public void m(T1 p1, T2 p2)
      if (this.name == null)
      {
         return null;
      }

      final StringBuilder builder = new StringBuilder();
      if (this.annotations != null && !this.annotations.isEmpty())
      {
         builder.append(this.annotations);
         builder.append("\n   ");
      }

      builder.append("public ");
      builder.append(this.returnType);
      builder.append(' ');
      builder.append(this.name);
      builder.append('(');
      builder.append(this.getParamsString());
      builder.append(')');

      return builder.toString();
   }

   public FMethod setDeclaration(String value) // no fulib
   {
      // a declaration looks like
      // public void m(T1 p1, T2 p2)
      if (value == null)
      {
         if (this.getDeclaration() == null)
         {
            return this;
         }
         else
         {
            this.name = null;
            this.returnType = "void";
            this.getParams().clear();
         }
      }

      if (!value.equals(this.getDeclaration()))
      {
         // TODO properly parse

         String oldValue = this.getDeclaration();
         int pos = value.indexOf('(');
         String namePart = value.substring(0, pos);
         String params = value.substring(pos + 1, value.length() - 1);
         String[] split = namePart.split(" ");
         String modifier = split[0];
         if (modifier.startsWith("@"))
         {
            int publicPos = modifier.indexOf("public");
            String annos = modifier.substring(0, publicPos - 1);
            setAnnotations(annos);
         }
         String newName = split[2];
         this.name = newName;
         String newReturnType = split[1];
         this.returnType = newReturnType;
         this.setParamsString(params);
         firePropertyChange("declaration", oldValue, value);
      }
      return this;
   }

   public String getAnnotations()
   {
      return this.annotations;
   }

   public FMethod setAnnotations(String value)
   {
      if (Objects.equals(value, this.annotations))
      {
         return this;
      }

      final String oldValue = this.annotations;
      this.annotations = value;
      this.firePropertyChange("annotations", oldValue, value);
      return this;
   }

   public LinkedHashMap<String, String> getParams()
   {
      if (this.params == null)
      {
         this.params = new LinkedHashMap<>();
      }
      return this.params;
   }

   public String getParamsString()
   {
      return this.getParams().entrySet().stream().map(e -> e.getValue() + " " + e.getKey())
                 .collect(Collectors.joining(", "));
   }

   public FMethod setParamsString(String params)
   {
      // TODO properly parse the string
      String[] split = params.split(", ");
      this.getParams().clear();
      for (String s : split)
      {
         if (s.isEmpty())
         {
            break;
         }
         String[] pair = s.split(" ");
         this.getParams().put(pair[1], pair[0]);
      }

      return this;
   }

   public String getSignature()
   {
      String paramTypes = this.getParams().entrySet().stream().filter(e -> !"this".equals(e.getKey()))
                              .map(Map.Entry::getValue).collect(Collectors.joining(","));
      return FileFragmentMap.METHOD + ":" + this.getName() + "(" + paramTypes + ")";
   }

   public String getReturnType()
   {
      return this.returnType;
   }

   public FMethod setReturnType(String value)
   {
      this.returnType = value;
      return this;
   }

   public String getMethodBody()
   {
      return this.methodBody;
   }

   public FMethod setMethodBody(String value)
   {
      if (Objects.equals(value, this.methodBody))
      {
         return this;
      }

      final String oldValue = this.methodBody;
      this.methodBody = value;
      this.firePropertyChange("methodBody", oldValue, value);
      return this;
   }

   public boolean getModified()
   {
      return this.modified;
   }

   public FMethod setModified(boolean value)
   {
      if (value == this.modified)
      {
         return this;
      }

      final boolean oldValue = this.modified;
      this.modified = value;
      this.firePropertyChange("modified", oldValue, value);
      return this;
   }

   // =============== Methods ===============

   public boolean signatureMatches(FMethod that)
   {
      if (!Objects.equals(this.getName(), that.getName()) || this.getParams().size() != that.getParams().size())
      {
         return false;
      }

      // unfortunately this.params.values().equals(that.params.value()) does not work
      final Iterator<String> it1 = this.getParams().values().iterator();
      final Iterator<String> it2 = that.getParams().values().iterator();
      while (it1.hasNext() && it2.hasNext())
      {
         final String type1 = it1.next();
         final String type2 = it2.next();
         if (!type1.equals(type2))
         {
            return false;
         }
      }

      return true;
   }

   @Deprecated
   public String readName()
   {
      return this.getName();
   }

   @Deprecated
   public FMethod writeName(String newName)
   {
      return this.setName(newName);
   }

   @Deprecated
   public String readSignature()
   {
      return this.getSignature();
   }

   @Deprecated
   public String readReturnType()
   {
      return this.getReturnType();
   }

   @Deprecated
   public FMethod writeReturnType(String newReturnType)
   {
      return this.setReturnType(newReturnType);
   }

   @Deprecated
   public LinkedHashMap<String, String> readParams()
   {
      return this.getParams();
   }

   @Deprecated
   public String readFullParamsString()
   {
      return this.getParamsString();
   }

   @Deprecated
   public FMethod setParamsByString(String params)
   {
      return this.setParamsString(params);
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public void removeYou()
   {
      this.setClazz(null);
   }

   @Override
   public String toString() // no fulib
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getDeclaration());
      result.append(' ').append(this.getMethodBody());
      result.append(' ').append(this.getAnnotations());
      return result.substring(1);
   }
}
