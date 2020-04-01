package org.fulib.classmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Objects;

/**
 * <img src='doc-files/classDiagram.png' width='663' alt="doc-files/classDiagram.png">
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
public class Clazz
{
   // =============== Constants ===============

   public static final java.util.ArrayList<Attribute> EMPTY_attributes = new java.util.ArrayList<Attribute>()
   { @Override public boolean add(Attribute value){ throw new UnsupportedOperationException("No direct add! Use xy.withAttributes(obj)"); }};
   public static final java.util.ArrayList<AssocRole> EMPTY_roles = new java.util.ArrayList<AssocRole>()
   { @Override public boolean add(AssocRole value){ throw new UnsupportedOperationException("No direct add! Use xy.withRoles(obj)"); }};
   public static final java.util.ArrayList<Clazz> EMPTY_subClasses = new java.util.ArrayList<Clazz>()
   { @Override public boolean add(Clazz value){ throw new UnsupportedOperationException("No direct add! Use xy.withSubClasses(obj)"); }};
   public static final java.util.ArrayList<FMethod> EMPTY_methods = new java.util.ArrayList<FMethod>()
   { @Override public boolean add(FMethod value){ throw new UnsupportedOperationException("No direct add! Use xy.withMethods(obj)"); }};

   public static final String PROPERTY_name = "name";
   public static final String PROPERTY_propertyStyle = "propertyStyle";
   public static final String PROPERTY_modified = "modified";
   public static final String PROPERTY_model = "model";
   public static final String PROPERTY_attributes = "attributes";
   public static final String PROPERTY_roles = "roles";
   public static final String PROPERTY_superClass = "superClass";
   public static final String PROPERTY_subClasses = "subClasses";
   public static final String PROPERTY_methods = "methods";
   public static final String PROPERTY_importList = "importList";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private ClassModel model;
   private String name;
   private Clazz superClass;
   private ArrayList<Clazz> // no fulib
      subClasses;
   private ArrayList<Attribute> // no fulib
      attributes;
   private ArrayList<AssocRole> // no fulib
      roles;
   private ArrayList<FMethod> // no fulib
      methods;
   private java.util.LinkedHashSet<String> importList = new java.util.LinkedHashSet<>();
   private String propertyStyle;
   private boolean modified;

   // =============== Properties ===============

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
      this.firePropertyChange(PROPERTY_model, oldValue, value);
      return this;
   }

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
      this.firePropertyChange(PROPERTY_name, oldValue, value);
      return this;
   }

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
      this.firePropertyChange(PROPERTY_superClass, oldValue, value);
      return this;
   }

   public ArrayList<Clazz> getSubClasses() // no fulib
   {
      return this.subClasses != null ? this.subClasses : EMPTY_subClasses;
   }

   @Deprecated
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
            this.withSubClasses((Clazz) item);
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      return this;
   }

   public Clazz withSubClasses(Clazz value)
   {
      if (this.subClasses == null)
      {
         this.subClasses = new ArrayList<>();
      }
      if (!this.subClasses.contains(value))
      {
         this.subClasses.add(value);
         value.setSuperClass(this);
         this.firePropertyChange(PROPERTY_subClasses, null, value);
      }
      return this;
   }

   public Clazz withSubClasses(Clazz... value)
   {
      for (final Clazz item : value)
      {
         this.withSubClasses(item);
      }
      return this;
   }

   public Clazz withSubClasses(Collection<? extends Clazz> value)
   {
      for (final Clazz item : value)
      {
         this.withSubClasses(item);
      }
      return this;
   }

   @Deprecated
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
            this.withoutSubClasses((Clazz) item);
         }
      }
      return this;
   }

   public Clazz withoutSubClasses(Clazz value)
   {
      if (this.subClasses != null && this.subClasses.remove(value))
      {
         value.setSuperClass(null);
         this.firePropertyChange(PROPERTY_subClasses, value, null);
      }
      return this;
   }

   public Clazz withoutSubClasses(Clazz... value)
   {
      for (final Clazz item : value)
      {
         this.withoutSubClasses(item);
      }
      return this;
   }

   public Clazz withoutSubClasses(Collection<? extends Clazz> value)
   {
      for (final Clazz item : value)
      {
         this.withoutSubClasses(item);
      }
      return this;
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

   public ArrayList<Attribute> getAttributes() // no fulib
   {
      return this.attributes != null ? this.attributes : EMPTY_attributes;
   }

   @Deprecated
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
            this.withAttributes((Attribute) item);
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      return this;
   }

   public Clazz withAttributes(Attribute value)
   {
      if (this.attributes == null)
      {
         this.attributes = new ArrayList<>();
      }
      if (!this.attributes.contains(value))
      {
         this.attributes.add(value);
         value.setClazz(this);
         this.firePropertyChange(PROPERTY_attributes, null, value);
      }
      return this;
   }

   public Clazz withAttributes(Attribute... value)
   {
      for (final Attribute item : value)
      {
         this.withAttributes(item);
      }
      return this;
   }

   public Clazz withAttributes(Collection<? extends Attribute> value)
   {
      for (final Attribute item : value)
      {
         this.withAttributes(item);
      }
      return this;
   }

   @Deprecated
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
            this.withoutAttributes((Attribute) item);
         }
      }
      return this;
   }

   public Clazz withoutAttributes(Attribute value)
   {
      if (this.attributes != null && this.attributes.remove(value))
      {
         value.setClazz(null);
         this.firePropertyChange(PROPERTY_attributes, value, null);
      }
      return this;
   }

   public Clazz withoutAttributes(Attribute... value)
   {
      for (final Attribute item : value)
      {
         this.withoutAttributes(item);
      }
      return this;
   }

   public Clazz withoutAttributes(Collection<? extends Attribute> value)
   {
      for (final Attribute item : value)
      {
         this.withoutAttributes(item);
      }
      return this;
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

   public ArrayList<AssocRole> getRoles() // no fulib
   {
      return this.roles != null ? this.roles : EMPTY_roles;
   }

   @Deprecated
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
            this.withRoles((AssocRole) item);
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      return this;
   }

   public Clazz withRoles(AssocRole value)
   {
      if (this.roles == null)
      {
         this.roles = new ArrayList<>();
      }
      if (!this.roles.contains(value))
      {
         this.roles.add(value);
         value.setClazz(this);
         this.firePropertyChange(PROPERTY_roles, null, value);
      }
      return this;
   }

   public Clazz withRoles(AssocRole... value)
   {
      for (final AssocRole item : value)
      {
         this.withRoles(item);
      }
      return this;
   }

   public Clazz withRoles(Collection<? extends AssocRole> value)
   {
      for (final AssocRole item : value)
      {
         this.withRoles(item);
      }
      return this;
   }

   @Deprecated
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
            this.withoutRoles((AssocRole) item);
         }
      }
      return this;
   }

   public Clazz withoutRoles(AssocRole value)
   {
      if (this.roles != null && this.roles.remove(value))
      {
         value.setClazz(null);
         this.firePropertyChange(PROPERTY_roles, value, null);
      }
      return this;
   }

   public Clazz withoutRoles(AssocRole... value)
   {
      for (final AssocRole item : value)
      {
         this.withoutRoles(item);
      }
      return this;
   }

   public Clazz withoutRoles(Collection<? extends AssocRole> value)
   {
      for (final AssocRole item : value)
      {
         this.withoutRoles(item);
      }
      return this;
   }

   public ArrayList<FMethod> getMethods() // no fulib
   {
      return this.methods != null ? this.methods : EMPTY_methods;
   }

   @Deprecated
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
            this.withMethods((FMethod) item);
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      return this;
   }

   public Clazz withMethods(FMethod value)
   {
      if (this.methods == null)
      {
         this.methods = new ArrayList<>();
      }
      if (!this.methods.contains(value))
      {
         this.methods.add(value);
         value.setClazz(this);
         this.firePropertyChange(PROPERTY_methods, null, value);
      }
      return this;
   }

   public Clazz withMethods(FMethod... value)
   {
      for (final FMethod item : value)
      {
         this.withMethods(item);
      }
      return this;
   }

   public Clazz withMethods(Collection<? extends FMethod> value)
   {
      for (final FMethod item : value)
      {
         this.withMethods(item);
      }
      return this;
   }

   @Deprecated
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
            this.withoutMethods((FMethod) item);
         }
      }
      return this;
   }

   public Clazz withoutMethods(FMethod value)
   {
      if (this.methods != null && this.methods.remove(value))
      {
         value.setClazz(null);
         this.firePropertyChange(PROPERTY_methods, value, null);
      }
      return this;
   }

   public Clazz withoutMethods(FMethod... value)
   {
      for (final FMethod item : value)
      {
         this.withoutMethods(item);
      }
      return this;
   }

   public Clazz withoutMethods(Collection<? extends FMethod> value)
   {
      for (final FMethod item : value)
      {
         this.withoutMethods(item);
      }
      return this;
   }

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
      this.firePropertyChange(PROPERTY_importList, oldValue, value);
      return this;
   }

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
      this.firePropertyChange(PROPERTY_propertyStyle, oldValue, value);
      return this;
   }

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
      this.firePropertyChange(PROPERTY_modified, oldValue, value);
      return this;
   }

   // =============== Methods ===============

   public Clazz markAsModified()
   {
      return this.setModified(true);
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
      this.setModel(null);
      this.withoutAttributes(new ArrayList<>(this.getAttributes()));
      this.withoutRoles(new ArrayList<>(this.getRoles()));
      this.withoutMethods(new ArrayList<>(this.getMethods()));
      this.setSuperClass(null);
      this.withoutSubClasses(new ArrayList<>(this.getSubClasses()));
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
