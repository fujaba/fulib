package org.fulib.classmodel;

import org.fulib.builder.Type;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.Objects;

/**
 * <img src='doc-files/classDiagram.png' width='663' alt="doc-files/classDiagram.png">
 */
public class Attribute
{
   // =============== Constants ===============

   public static final String PROPERTY_name = "name";
   public static final String PROPERTY_type = "type";
   public static final String PROPERTY_collectionType = "collectionType";
   public static final String PROPERTY_initialization = "initialization";
   public static final String PROPERTY_propertyStyle = "propertyStyle";
   public static final String PROPERTY_modified = "modified";
   public static final String PROPERTY_clazz = "clazz";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners = null;

   private Clazz clazz = null;
   private String name;
   private String type;
   private CollectionType collectionType;
   private String initialization;
   private String propertyStyle;
   private boolean modified;

   // =============== Properties ===============

   public Clazz getClazz()
   {
      return this.clazz;
   }

   public Attribute setClazz(Clazz value)
   {
      if (this.clazz == value)
      {
         return this;
      }

      final Clazz oldValue = this.clazz;
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
      this.firePropertyChange(PROPERTY_clazz, oldValue, value);
      return this;
   }

   public String getId()
   {
      final String className = this.getClazz() != null ? this.getClazz().getName() : "___";
      return className + "_" + this.getName();
   }

   public String getName()
   {
      return this.name;
   }

   public Attribute setName(String value)
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

   public String getType()
   {
      return this.type;
   }

   public Attribute setType(String value)
   {
      if (Objects.equals(value, this.type))
      {
         return this;
      }

      final String oldValue = this.type;
      this.type = value;
      this.firePropertyChange(PROPERTY_type, oldValue, value);
      return this;
   }

   public String getBoxType()
   {
      return getBoxType(this.getType());
   }

   private static String getBoxType(String attrType)
   {
      switch (attrType)
      {
      case "boolean":
         return "Boolean";
      case "byte":
         return "Byte";
      case "short":
         return "Short";
      case "char":
         return "Character"; // !
      case "int":
         return "Integer"; // !
      case "long":
         return "Long";
      case "float":
         return "Float";
      case "double":
         return "Double";
      }
      return attrType;
   }

   public boolean isPrimitive()
   {
      return isPrimitive(this.getType());
   }

   private static boolean isPrimitive(String attrType)
   {
      return !attrType.equals(getBoxType(attrType));
   }

   public CollectionType getCollectionType()
   {
      return this.collectionType;
   }

   public Attribute setCollectionType(CollectionType value)
   {
      if (Objects.equals(value, this.collectionType))
      {
         return this;
      }

      final CollectionType oldValue = this.collectionType;
      this.collectionType = value;
      this.firePropertyChange(PROPERTY_collectionType, oldValue, value);
      return this;
   }

   public boolean isCollection()
   {
      return this.getCollectionType() != null;
   }

   public String getInitialization()
   {
      return this.initialization;
   }

   public Attribute setInitialization(String value)
   {
      if (Objects.equals(value, this.initialization))
      {
         return this;
      }

      final String oldValue = this.initialization;
      this.initialization = value;
      this.firePropertyChange(PROPERTY_initialization, oldValue, value);
      return this;
   }

   public String getPropertyStyle()
   {
      return this.propertyStyle;
   }

   public Attribute setPropertyStyle(String value)
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

   public boolean isJavaFX()
   {
      return Type.JAVA_FX.equals(this.getPropertyStyle());
   }

   public boolean getModified()
   {
      return this.modified;
   }

   public Attribute setModified(boolean value)
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

   public Attribute markAsModified()
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
      this.setClazz(null);
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getType());
      result.append(' ').append(this.getInitialization());
      result.append(' ').append(this.getPropertyStyle());
      return result.substring(1);
   }
}
