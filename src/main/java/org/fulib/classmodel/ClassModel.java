package org.fulib.classmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Objects;

/**
 * <img src='doc-files/classDiagram.png' width='663' alt="doc-files/classDiagram.png">
 */
public class ClassModel
{
   // =============== Constants ===============

   public static final java.util.ArrayList<Clazz> EMPTY_classes = new java.util.ArrayList<Clazz>()
   { @Override public boolean add(Clazz value){ throw new UnsupportedOperationException("No direct add! Use xy.withClasses(obj)"); }};
   public static final String PROPERTY_packageName = "packageName";
   public static final String PROPERTY_mainJavaDir = "mainJavaDir";
   @Deprecated
   public static final String PROPERTY_defaultRoleType = "defaultRoleType";
   public static final String PROPERTY_defaultCollectionType = "defaultCollectionType";
   public static final String PROPERTY_defaultPropertyStyle = "defaultPropertyStyle";
   public static final String PROPERTY_classes = "classes";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners = null;

   private java.util.ArrayList<Clazz> classes = null;

   private String mainJavaDir;
   private String packageName;
   private CollectionType defaultCollectionType;
   private String defaultPropertyStyle = "POJO";

   // =============== Properties ===============

   public String getPackageSrcFolder()
   {
      return this.getMainJavaDir() + "/" + this.getPackageName().replaceAll("\\.", "/");
   }

   public String getMainJavaDir()
   {
      return this.mainJavaDir;
   }

   public ClassModel setMainJavaDir(String value)
   {
      if (Objects.equals(value, this.mainJavaDir))
      {
         return this;
      }

      final String oldValue = this.mainJavaDir;
      this.mainJavaDir = value;
      this.firePropertyChange(PROPERTY_mainJavaDir, oldValue, value);
      return this;
   }

   public String getPackageName()
   {
      return this.packageName;
   }

   public ClassModel setPackageName(String value)
   {
      if (Objects.equals(value, this.packageName))
      {
         return this;
      }

      final String oldValue = this.packageName;
      this.packageName = value;
      this.firePropertyChange(PROPERTY_packageName, oldValue, value);
      return this;
   }

   /**
    * @return the default collection type
    *
    * @since 1.2
    */
   public CollectionType getDefaultCollectionType()
   {
      return this.defaultCollectionType;
   }

   /**
    * @param value
    *    the new default collection type
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public ClassModel setDefaultCollectionType(CollectionType value)
   {
      if (Objects.equals(value, this.defaultCollectionType))
      {
         return this;
      }

      final CollectionType oldValue = this.defaultCollectionType;
      this.defaultCollectionType = value;
      this.firePropertyChange(PROPERTY_defaultCollectionType, oldValue, value);
      return this;
   }

   /**
    * @return the default collection type
    *
    * @deprecated since 1.2; use {@link #getDefaultCollectionType()} instead
    */
   @Deprecated
   public String getDefaultRoleType()
   {
      return this.getDefaultCollectionType().getImplTemplate();
   }

   /**
    * @param value
    *    the new default collection type
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setDefaultCollectionType(CollectionType) setDefaultCollectionType}
    * ({@link CollectionType#of(String) CollectionType.of}(value)) instead
    */
   @Deprecated
   public ClassModel setDefaultRoleType(String value)
   {
      return this.setDefaultCollectionType(CollectionType.of(value));
   }

   public String getDefaultPropertyStyle()
   {
      return this.defaultPropertyStyle;
   }

   public ClassModel setDefaultPropertyStyle(String value)
   {
      if (Objects.equals(value, this.defaultPropertyStyle))
      {
         return this;
      }

      final String oldValue = this.defaultPropertyStyle;
      this.defaultPropertyStyle = value;
      this.firePropertyChange(PROPERTY_defaultPropertyStyle, oldValue, value);
      return this;
   }

   public Clazz getClazz(String name)
   {
      for (Clazz clazz : this.getClasses())
      {
         if (Objects.equals(clazz.getName(), name))
         {
            return clazz;
         }
      }
      return null;
   }

   public java.util.ArrayList<Clazz> getClasses()
   {
      return this.classes != null ? this.classes : EMPTY_classes;
   }

   @Deprecated
   public ClassModel withClasses(Object... value)
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
            this.withClasses(((Collection<?>) item).toArray());
         }
         else if (item instanceof Clazz)
         {
            this.withClasses((Clazz) item);
         }
         else
         {
            throw new IllegalArgumentException();
         }
      }
      return this;
   }

   public ClassModel withClasses(Clazz value)
   {
      if (this.classes == null)
      {
         this.classes = new java.util.ArrayList<Clazz>();
      }
      if (!this.classes.contains(value))
      {
         this.classes.add(value);
         value.setModel(this);
         this.firePropertyChange(PROPERTY_classes, null, value);
      }
      return this;
   }

   public ClassModel withClasses(Clazz... value)
   {
      for (final Clazz item : value)
      {
         this.withClasses(item);
      }
      return this;
   }

   public ClassModel withClasses(Collection<? extends Clazz> value)
   {
      for (final Clazz item : value)
      {
         this.withClasses(item);
      }
      return this;
   }

   @Deprecated
   public ClassModel withoutClasses(Object... value)
   {
      if (this.classes == null || value == null)
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
            this.withoutClasses(((Collection<?>) item).toArray());
         }
         else if (item instanceof Clazz)
         {
            this.withoutClasses((Clazz) item);
         }
      }
      return this;
   }

   public ClassModel withoutClasses(Clazz value)
   {
      if (this.classes != null && this.classes.remove(value))
      {
         value.setModel(null);
         this.firePropertyChange(PROPERTY_classes, value, null);
      }
      return this;
   }

   public ClassModel withoutClasses(Clazz... value)
   {
      for (final Clazz item : value)
      {
         this.withoutClasses(item);
      }
      return this;
   }

   public ClassModel withoutClasses(Collection<? extends Clazz> value)
   {
      for (final Clazz item : value)
      {
         this.withoutClasses(item);
      }
      return this;
   }

   // =============== Methods ===============

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
      this.withoutClasses(new java.util.ArrayList<>(this.getClasses()));
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getPackageName());
      result.append(' ').append(this.getMainJavaDir());
      result.append(' ').append(this.getDefaultCollectionType());
      result.append(' ').append(this.getDefaultPropertyStyle());
      return result.substring(1);
   }
}
