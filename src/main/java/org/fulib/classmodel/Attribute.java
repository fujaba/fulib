package org.fulib.classmodel;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.fulib.builder.Type;
import org.fulib.parser.FragmentMapBuilder;
import org.fulib.parser.FulibClassLexer;
import org.fulib.parser.FulibClassParser;
import org.fulib.util.Validator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class Attribute
{
   // =============== Constants ===============

   public static final String PROPERTY_name = "name";
   public static final String PROPERTY_type = "type";
   /** @since 1.2 */
   public static final String PROPERTY_collectionType = "collectionType";
   public static final String PROPERTY_initialization = "initialization";
   public static final String PROPERTY_propertyStyle = "propertyStyle";
   /** @since 1.3 */
   public static final String PROPERTY_description = "description";
   public static final String PROPERTY_modified = "modified";
   public static final String PROPERTY_clazz = "clazz";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private Clazz clazz;
   private String name;
   private String type;
   private CollectionType collectionType;
   private String initialization;
   private String propertyStyle;
   private String description;
   private boolean modified;

   private String typeSignature;

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

   /**
    * @return a string that uniquely identifies this attribute within the enclosing class model
    *
    * @since 1.2
    */
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

   public Attribute setType(String value) // no fulib
   {
      if (Objects.equals(value, this.type))
      {
         return this;
      }

      final String oldValue = this.type;
      this.type = value;
      this.typeSignature = buildTypeSignature(value);
      this.firePropertyChange(PROPERTY_type, oldValue, value);
      return this;
   }

   private static String buildTypeSignature(String type)
   {
      if (Validator.isSimpleName(type))
      {
         // fast-path - if the type is just an identifier (no generics, no annotations, ...),
         // we can skip all the parser overhead because the signature will be same as the type anyway.
         return type;
      }

      final CharStream input = CharStreams.fromString(type);
      final FulibClassLexer lexer = new FulibClassLexer(input);
      final FulibClassParser parser = new FulibClassParser(new CommonTokenStream(lexer));
      final FulibClassParser.TypeContext typeCtx = parser.type();
      return FragmentMapBuilder.getTypeSignature(typeCtx);
   }

   /**
    * @return the signature of this attribute's {@linkplain #getType() type}
    *
    * @deprecated for internal use only
    *
    * @since 1.2.2
    */
   @Deprecated
   public String getTypeSignature()
   {
      return this.typeSignature;
   }

   /**
    * @return the collection type
    *
    * @since 1.2
    */
   public CollectionType getCollectionType()
   {
      return this.collectionType;
   }

   /**
    * @param value
    *    the new collection type
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
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

   /**
    * @return a boolean indicating whether this is multi-valued attribute
    *
    * @since 1.2
    */
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

   /**
    * @return the property style of this attribute
    */
   public String getPropertyStyle()
   {
      return this.propertyStyle;
   }

   /**
    * @param value
    *    the property style to use for this attribute.
    *    Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.
    *
    * @return this instance, to allow method chaining
    */
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

   /**
    * @return the description of this attribute, used for generating JavaDocs
    *
    * @since 1.3
    */
   public String getDescription()
   {
      return this.description;
   }

   /**
    * @param value
    *    the description of this attribute, used for generating JavaDocs
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.3
    */
   public Attribute setDescription(String value)
   {
      if (Objects.equals(value, this.description))
      {
         return this;
      }

      final String oldValue = this.description;
      this.description = value;
      this.firePropertyChange(PROPERTY_description, oldValue, value);
      return this;
   }

   /**
    * @return a boolean indicating whether this attribute was modified. For internal use only.
    */
   public boolean getModified()
   {
      return this.modified;
   }

   /**
    * @param value
    *    a boolean indicating whether this attribute was modified. For internal use only.
    *
    * @return this instance, to allow method chaining
    */
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

   /**
    * Marks this attribute as modified.
    * Equivalent to calling {@link #setModified(boolean)} with a value of {@code true}.
    *
    * @return this instance, to allow method chaining
    */
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
      result.append(' ').append(this.getDescription());
      return result.substring(1);
   }
}
