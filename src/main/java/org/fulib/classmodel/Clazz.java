package org.fulib.classmodel;

import org.fulib.StrUtil;

import java.util.ArrayList;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;

/**
 * <img src='doc-files/classDiagram.png' width='663' alt="doc-files/classDiagram.png">
 */
public class Clazz  
{


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



   public static final java.util.ArrayList<Attribute> EMPTY_attributes = new java.util.ArrayList<Attribute>()
   { @Override public boolean add(Attribute value){ throw new UnsupportedOperationException("No direct add! Use xy.withAttributes(obj)"); }};


   private java.util.ArrayList<Attribute> attributes = null;

   public java.util.ArrayList<Attribute> getAttributes()
   {
      if (this.attributes == null)
      {
         return EMPTY_attributes;
      }

      return this.attributes;
   }

   public Attribute getAttribute(String name)
   {
      for (Attribute attr : this.getAttributes())
      {
         if (StrUtil.stringEquals(attr.getName(), name))
         {
            return attr;
         }
      }
      return null;
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


   public static final java.util.ArrayList<AssocRole> EMPTY_roles = new java.util.ArrayList<AssocRole>()
   { @Override public boolean add(AssocRole value){ throw new UnsupportedOperationException("No direct add! Use xy.withRoles(obj)"); }};


   private java.util.ArrayList<AssocRole> roles = null;

   public java.util.ArrayList<AssocRole> getRoles()
   {
      if (this.roles == null)
      {
         return EMPTY_roles;
      }

      return this.roles;
   }

   public AssocRole getRole(String name)
   {
      for (AssocRole role : this.getRoles())
      {
         if (StrUtil.stringEquals(role.getName(), name))
         {
            return role;
         }
      }
      return null;
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

   public Clazz markAsModified()
   {
      return this.setModified(true);
   }



   public static final java.util.ArrayList<Clazz> EMPTY_subClasses = new java.util.ArrayList<Clazz>()
   { @Override public boolean add(Clazz value){ throw new UnsupportedOperationException("No direct add! Use xy.withSubClasses(obj)"); }};


   private java.util.ArrayList<Clazz> subClasses = null;

   public java.util.ArrayList<Clazz> getSubClasses()
   {
      if (this.subClasses == null)
      {
         return EMPTY_subClasses;
      }

      return this.subClasses;
   }

   public Clazz withSubClasses(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withSubClasses(i);
            }
         }
         else if (item instanceof Clazz)
         {
            if (this.subClasses == null)
            {
               this.subClasses = new java.util.ArrayList<Clazz>();
            }
            if ( ! this.subClasses.contains(item))
            {
               this.subClasses.add((Clazz)item);
               ((Clazz)item).setSuperClass(this);
               firePropertyChange("subClasses", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Clazz withoutSubClasses(Object... value)
   {
      if (this.subClasses == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutSubClasses(i);
            }
         }
         else if (item instanceof Clazz)
         {
            if (this.subClasses.contains(item))
            {
               this.subClasses.remove((Clazz)item);
               ((Clazz)item).setSuperClass(null);
               firePropertyChange("subClasses", item, null);
            }
         }
      }
      return this;
   }


   private Clazz superClass = null;

   public Clazz getSuperClass()
   {
      return this.superClass;
   }

   public Clazz setSuperClass(Clazz value)
   {
      if (this.superClass != value)
      {
         Clazz oldValue = this.superClass;
         if (this.superClass != null)
         {
            this.superClass = null;
            oldValue.withoutSubClasses(this);
         }
         this.superClass = value;
         if (value != null)
         {
            value.withSubClasses(this);
         }
         firePropertyChange("superClass", oldValue, value);
      }
      return this;
   }



   public void removeYou()
   {
      this.setModel(null);
      this.setSuperClass(null);

      this.withoutAttributes(this.getAttributes().clone());


      this.withoutRoles(this.getRoles().clone());


      this.withoutMethods(this.getMethods().clone());


      this.withoutSubClasses(this.getSubClasses().clone());


   }


   public static final String PROPERTY_name = "name";

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


   public static final String PROPERTY_propertyStyle = "propertyStyle";

   private String propertyStyle;

   public String getPropertyStyle()
   {
      return propertyStyle;
   }

   public Clazz setPropertyStyle(String value)
   {
      if (value == null ? this.propertyStyle != null : ! value.equals(this.propertyStyle))
      {
         String oldValue = this.propertyStyle;
         this.propertyStyle = value;
         firePropertyChange("propertyStyle", oldValue, value);
      }
      return this;
   }


   public static final String PROPERTY_modified = "modified";

   private boolean modified = false;

   public boolean getModified()
   {
      return modified;
   }

   public Clazz setModified(boolean value)
   {
      if (value != this.modified)
      {
         boolean oldValue = this.modified;
         this.modified = value;
         firePropertyChange("modified", oldValue, value);
      }
      return this;
   }

   public static final String PROPERTY_model = "model";

   public static final String PROPERTY_attributes = "attributes";

   public static final String PROPERTY_roles = "roles";

   public static final String PROPERTY_superClass = "superClass";

   public static final String PROPERTY_subClasses = "subClasses";

   public static final java.util.ArrayList<FMethod> EMPTY_methods = new java.util.ArrayList<FMethod>()
   { @Override public boolean add(FMethod value){ throw new UnsupportedOperationException("No direct add! Use xy.withMethods(obj)"); }};


   public static final String PROPERTY_methods = "methods";

   private java.util.ArrayList<FMethod> methods = null;

   public java.util.ArrayList<FMethod> getMethods()
   {
      if (this.methods == null)
      {
         return EMPTY_methods;
      }

      return this.methods;
   }

   public Clazz withMethods(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withMethods(i);
            }
         }
         else if (item instanceof FMethod)
         {
            if (this.methods == null)
            {
               this.methods = new java.util.ArrayList<FMethod>();
            }
            if ( ! this.methods.contains(item))
            {
               this.methods.add((FMethod)item);
               ((FMethod)item).setClazz(this);
               firePropertyChange("methods", null, item);
            }
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }



   public Clazz withoutMethods(Object... value)
   {
      if (this.methods == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutMethods(i);
            }
         }
         else if (item instanceof FMethod)
         {
            if (this.methods.contains(item))
            {
               this.methods.remove((FMethod)item);
               ((FMethod)item).setClazz(null);
               firePropertyChange("methods", item, null);
            }
         }
      }
      return this;
   }


   public static final String PROPERTY_importList = "importList";

   private java.util.LinkedHashSet<String> importList = new java.util.LinkedHashSet<>();

   public java.util.LinkedHashSet<String> getImportList()
   {
      return importList;
   }

   public Clazz setImportList(java.util.LinkedHashSet<String> value)
   {
      if (value != this.importList)
      {
         java.util.LinkedHashSet<String> oldValue = this.importList;
         this.importList = value;
         firePropertyChange("importList", oldValue, value);
      }
      return this;
   }


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getName());
      result.append(" ").append(this.getPropertyStyle());


      return result.substring(1);
   }

}