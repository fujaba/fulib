package org.fulib.classmodel;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.Collection;

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
      if (this.model == value)
      {
         return this;
      }

      final ClassModel oldValue = this.model;
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
      this.firePropertyChange("model", oldValue, value);
      return this;
   }

   public static final java.util.ArrayList<Attribute> EMPTY_attributes = new java.util.ArrayList<Attribute>()
   { @Override public boolean add(Attribute value){ throw new UnsupportedOperationException("No direct add! Use xy.withAttributes(obj)"); }};

   private java.util.ArrayList<Attribute> attributes = null;

   public java.util.ArrayList<Attribute> getAttributes()
   {
      return this.attributes != null ? this.attributes : EMPTY_attributes;
   }

   public Attribute getAttribute(String name)
   {
      for (Attribute attr : this.getAttributes())
      {
	      if (Objects.equals(attr.getName(), name))
         {
            return attr;
         }
      }
      return null;
   }

   public Clazz withAttributes(Object... value)
   {
      if (value == null)
      {
         return this;
      }
      for (Object item : value)
      {
         if (item == null)
         {
            continue;
         }
         if (item instanceof Collection)
         {
            this.withAttributes(((Collection<?>) item).toArray());
         }
         else if (item instanceof Attribute)
         {
            if (this.attributes == null)
            {
               this.attributes = new java.util.ArrayList<Attribute>();
            }
            if (!this.attributes.contains(item))
            {
               this.attributes.add((Attribute)item);
               ((Attribute)item).setClazz(this);
               this.firePropertyChange("attributes", null, item);
            }
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      return this;
   }

   public Clazz withoutAttributes(Object... value)
   {
      if (this.attributes == null || value == null)
      {
         return this;
      }
      for (Object item : value)
      {
         if (item == null)
         {
            continue;
         }
         if (item instanceof Collection)
         {
            this.withoutAttributes(((Collection<?>) item).toArray());
         }
         else if (item instanceof Attribute)
         {
            if (this.attributes.remove(item))
            {
               ((Attribute)item).setClazz(null);
               this.firePropertyChange("attributes", item, null);
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
      return this.roles != null ? this.roles : EMPTY_roles;
   }

   public AssocRole getRole(String name)
   {
      for (AssocRole role : this.getRoles())
      {
	      if (Objects.equals(role.getName(), name))
         {
            return role;
         }
      }
      return null;
   }

   public Clazz withRoles(Object... value)
   {
      if (value == null)
      {
         return this;
      }
      for (Object item : value)
      {
         if (item == null)
         {
            continue;
         }
         if (item instanceof Collection)
         {
            this.withRoles(((Collection<?>) item).toArray());
         }
         else if (item instanceof AssocRole)
         {
            if (this.roles == null)
            {
               this.roles = new java.util.ArrayList<AssocRole>();
            }
            if (!this.roles.contains(item))
            {
               this.roles.add((AssocRole)item);
               ((AssocRole)item).setClazz(this);
               this.firePropertyChange("roles", null, item);
            }
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      return this;
   }

   public Clazz withoutRoles(Object... value)
   {
      if (this.roles == null || value == null)
      {
         return this;
      }
      for (Object item : value)
      {
         if (item == null)
         {
            continue;
         }
         if (item instanceof Collection)
         {
            this.withoutRoles(((Collection<?>) item).toArray());
         }
         else if (item instanceof AssocRole)
         {
            if (this.roles.remove(item))
            {
               ((AssocRole)item).setClazz(null);
               this.firePropertyChange("roles", item, null);
            }
         }
      }
      return this;
   }

   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
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

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
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
      return this.subClasses != null ? this.subClasses : EMPTY_subClasses;
   }

   public Clazz withSubClasses(Object... value)
   {
      if (value == null)
      {
         return this;
      }
      for (Object item : value)
      {
         if (item == null)
         {
            continue;
         }
         if (item instanceof Collection)
         {
            this.withSubClasses(((Collection<?>) item).toArray());
         }
         else if (item instanceof Clazz)
         {
            if (this.subClasses == null)
            {
               this.subClasses = new java.util.ArrayList<Clazz>();
            }
            if (!this.subClasses.contains(item))
            {
               this.subClasses.add((Clazz)item);
               ((Clazz)item).setSuperClass(this);
               this.firePropertyChange("subClasses", null, item);
            }
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      return this;
   }

   public Clazz withoutSubClasses(Object... value)
   {
      if (this.subClasses == null || value == null)
      {
         return this;
      }
      for (Object item : value)
      {
         if (item == null)
         {
            continue;
         }
         if (item instanceof Collection)
         {
            this.withoutSubClasses(((Collection<?>) item).toArray());
         }
         else if (item instanceof Clazz)
         {
            if (this.subClasses.remove(item))
            {
               ((Clazz)item).setSuperClass(null);
               this.firePropertyChange("subClasses", item, null);
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
      if (this.superClass == value)
      {
         return this;
      }

      final Clazz oldValue = this.superClass;
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
      this.firePropertyChange("superClass", oldValue, value);
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
      return this.name;
   }

   public Clazz setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange("name", oldValue, value);
      return this;
   }

   public static final String PROPERTY_propertyStyle = "propertyStyle";

   private String propertyStyle;

   public String getPropertyStyle()
   {
      return this.propertyStyle;
   }

   public Clazz setPropertyStyle(String value)
   {
      if (Objects.equals(value, this.propertyStyle))
      {
         return this;
      }

      final String oldValue = this.propertyStyle;
      this.propertyStyle = value;
      this.firePropertyChange("propertyStyle", oldValue, value);
      return this;
   }

   public static final String PROPERTY_modified = "modified";

   private boolean modified;

   public boolean getModified()
   {
      return this.modified;
   }

   public Clazz setModified(boolean value)
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
      return this.methods != null ? this.methods : EMPTY_methods;
   }

   public Clazz withMethods(Object... value)
   {
      if (value == null)
      {
         return this;
      }
      for (Object item : value)
      {
         if (item == null)
         {
            continue;
         }
         if (item instanceof Collection)
         {
            this.withMethods(((Collection<?>) item).toArray());
         }
         else if (item instanceof FMethod)
         {
            if (this.methods == null)
            {
               this.methods = new java.util.ArrayList<FMethod>();
            }
            if (!this.methods.contains(item))
            {
               this.methods.add((FMethod)item);
               ((FMethod)item).setClazz(this);
               this.firePropertyChange("methods", null, item);
            }
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      return this;
   }

   public Clazz withoutMethods(Object... value)
   {
      if (this.methods == null || value == null)
      {
         return this;
      }
      for (Object item : value)
      {
         if (item == null)
         {
            continue;
         }
         if (item instanceof Collection)
         {
            this.withoutMethods(((Collection<?>) item).toArray());
         }
         else if (item instanceof FMethod)
         {
            if (this.methods.remove(item))
            {
               ((FMethod)item).setClazz(null);
               this.firePropertyChange("methods", item, null);
            }
         }
      }
      return this;
   }

   public static final String PROPERTY_importList = "importList";

   private java.util.LinkedHashSet<String> importList = new java.util.LinkedHashSet<>();

   public java.util.LinkedHashSet<String> getImportList()
   {
      return this.importList;
   }

   public Clazz setImportList(java.util.LinkedHashSet<String> value)
   {
      if (Objects.equals(value, this.importList))
      {
         return this;
      }

      final java.util.LinkedHashSet<String> oldValue = this.importList;
      this.importList = value;
      this.firePropertyChange("importList", oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();

      result.append(' ').append(this.getName());
      result.append(' ').append(this.getPropertyStyle());


      return result.substring(1);
   }

}
