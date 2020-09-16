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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
   /** @since 1.3 */
   public static final String PROPERTY_since = "since";
   public static final String PROPERTY_modified = "modified";
   public static final String PROPERTY_clazz = "clazz";

   /** @since 1.3 */
   public static final String PROPERTY_NAME = "name";
   /** @since 1.3 */
   public static final String PROPERTY_TYPE = "type";
   /** @since 1.3 */ // no fulib
   public static final String PROPERTY_COLLECTION_TYPE = "collectionType";
   /** @since 1.3 */
   public static final String PROPERTY_INITIALIZATION = "initialization";
   /** @since 1.3 */
   public static final String PROPERTY_PROPERTY_STYLE = "propertyStyle";
   /** @since 1.3 */
   public static final String PROPERTY_MODIFIED = "modified";
   /** @since 1.3 */
   public static final String PROPERTY_DESCRIPTION = "description";
   /** @since 1.3 */
   public static final String PROPERTY_SINCE = "since";
   /** @since 1.3 */
   public static final String PROPERTY_CLAZZ = "clazz";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private Clazz clazz;
   private String name;
   private String type;
   private CollectionType collectionType;
   private String initialization;
   private String propertyStyle;
   private String description;
   private String since;
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
      this.firePropertyChange(PROPERTY_CLAZZ, oldValue, value);
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
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
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
    *    the collection type
    *
    * @return this
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
      this.firePropertyChange(PROPERTY_COLLECTION_TYPE, oldValue, value);
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
      this.firePropertyChange(PROPERTY_INITIALIZATION, oldValue, value);
      return this;
   }

   /**
    * @return the property style.
    * Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.
    */
   public String getPropertyStyle()
   {
      return this.propertyStyle;
   }

   /**
    * @param value
    *    the property style.
    *    Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.
    *
    * @return this
    */
   public Attribute setPropertyStyle(String value)
   {
      if (Objects.equals(value, this.propertyStyle))
      {
         return this;
      }

      final String oldValue = this.propertyStyle;
      this.propertyStyle = value;
      this.firePropertyChange(PROPERTY_PROPERTY_STYLE, oldValue, value);
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
    * @return this
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
      this.firePropertyChange(PROPERTY_DESCRIPTION, oldValue, value);
      return this;
   }

   /**
    * @return the lines of the description of this attribute, used for generating JavaDocs
    *
    * @since 1.3
    * @deprecated for internal use only
    */
   @Deprecated
   public List<String> getDescriptionLines()
   {
      return this.getDescription() == null ? Collections.emptyList() : Arrays.asList(this.getDescription().split("\n"));
   }

   /**
    * @return the version when this attribute was introduced, used for generating JavaDocs
    *
    * @since 1.3
    */
   public String getSince()
   {
      return this.since;
   }

   /**
    * @param value
    *    the version when this attribute was introduced, used for generating JavaDocs
    *
    * @return this
    *
    * @since 1.3
    */
   public Attribute setSince(String value)
   {
      if (Objects.equals(value, this.since))
      {
         return this;
      }

      final String oldValue = this.since;
      this.since = value;
      this.firePropertyChange(PROPERTY_SINCE, oldValue, value);
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
    * @return this
    */
   public Attribute setModified(boolean value)
   {
      if (value == this.modified)
      {
         return this;
      }

      final boolean oldValue = this.modified;
      this.modified = value;
      this.firePropertyChange(PROPERTY_MODIFIED, oldValue, value);
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
      result.append(' ').append(this.getSince());
      return result.substring(1);
   }
}
