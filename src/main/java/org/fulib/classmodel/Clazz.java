package org.fulib.classmodel;

import org.fulib.builder.Type;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
public class Clazz
{
   // =============== Constants ===============

   /** @deprecated since 1.2; for internal use only */
   @Deprecated
   public static final ArrayList<Attribute> EMPTY_attributes = new ArrayList<Attribute>()
   { @Override public boolean add(Attribute value){ throw new UnsupportedOperationException("No direct add! Use xy.withAttributes(obj)"); }};
   /** @deprecated since 1.2; for internal use only */
   @Deprecated
   public static final ArrayList<AssocRole> EMPTY_roles = new ArrayList<AssocRole>()
   { @Override public boolean add(AssocRole value){ throw new UnsupportedOperationException("No direct add! Use xy.withRoles(obj)"); }};
   /** @deprecated since 1.2; for internal use only */
   @Deprecated
   public static final ArrayList<Clazz> EMPTY_subClasses = new ArrayList<Clazz>()
   { @Override public boolean add(Clazz value){ throw new UnsupportedOperationException("No direct add! Use xy.withSubClasses(obj)"); }};
   /** @deprecated since 1.2; for internal use only */
   @Deprecated
   public static final ArrayList<FMethod> EMPTY_methods = new ArrayList<FMethod>()
   { @Override public boolean add(FMethod value){ throw new UnsupportedOperationException("No direct add! Use xy.withMethods(obj)"); }};

   public static final String PROPERTY_name = "name";
   public static final String PROPERTY_propertyStyle = "propertyStyle";
   public static final String PROPERTY_modified = "modified";
   public static final String PROPERTY_model = "model";
   public static final String PROPERTY_attributes = "attributes" /* no fulib */;
   public static final String PROPERTY_roles = "roles" /* no fulib */;
   public static final String PROPERTY_superClass = "superClass";
   public static final String PROPERTY_subClasses = "subClasses" /* no fulib */;
   public static final String PROPERTY_methods = "methods" /* no fulib */;
   /** @since 1.2 */
   public static final String PROPERTY_imports = "imports";
   /** @deprecated since 1.2; use {@link #PROPERTY_imports} instead */
   @Deprecated
   public static final String PROPERTY_importList = "importList";

   /** @since 1.3 */
   public static final String PROPERTY_NAME = "name";
   /** @since 1.3 */
   public static final String PROPERTY_PROPERTY_STYLE = "propertyStyle";
   /** @since 1.3 */
   public static final String PROPERTY_MODIFIED = "modified";
   /** @since 1.3 */ // no fulib
   public static final String PROPERTY_IMPORTS = "imports";
   /** @since 1.3 */
   public static final String PROPERTY_MODEL = "model";
   /** @since 1.3 */ // no fulib
   public static final String PROPERTY_ATTRIBUTES = "attributes";
   /** @since 1.3 */ // no fulib
   public static final String PROPERTY_ROLES = "roles";
   /** @since 1.3 */ // no fulib
   public static final String PROPERTY_METHODS = "methods";
   /** @since 1.3 */ // no fulib
   public static final String PROPERTY_SUB_CLASSES = "subClasses";
   /** @since 1.3 */
   public static final String PROPERTY_SUPER_CLASS = "superClass";

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
   private LinkedHashSet<String> // no fulib
      imports;
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
      this.firePropertyChange(PROPERTY_MODEL, oldValue, value);
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
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
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
      this.firePropertyChange(PROPERTY_SUPER_CLASS, oldValue, value);
      return this;
   }

   public ArrayList<Clazz> getSubClasses() // no fulib
   {
      return this.subClasses != null ? this.subClasses : EMPTY_subClasses;
   }

   /** @deprecated since 1.2; use one of the type-safe overloads */
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

   /**
    * @param value
    *    the subclasses
    *
    * @return this
    *
    * @see Clazz#setSuperClass(Clazz)
    * @since 1.2
    */
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
         this.firePropertyChange(PROPERTY_SUB_CLASSES, null, value);
      }
      return this;
   }

   /**
    * @param value
    *    the subclasses
    *
    * @return this
    *
    * @see Clazz#setSuperClass(Clazz)
    * @since 1.2
    */
   public Clazz withSubClasses(Clazz... value)
   {
      for (final Clazz item : value)
      {
         this.withSubClasses(item);
      }
      return this;
   }

   /**
    * @param value
    *    the subclasses
    *
    * @return this
    *
    * @see Clazz#setSuperClass(Clazz)
    * @since 1.2
    */
   public Clazz withSubClasses(Collection<? extends Clazz> value)
   {
      for (final Clazz item : value)
      {
         this.withSubClasses(item);
      }
      return this;
   }

   /** @deprecated since 1.2; use one of the type-safe overloads */
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

   /**
    * @param value
    *    the subclasses
    *
    * @return this
    *
    * @see Clazz#setSuperClass(Clazz)
    * @since 1.2
    */
   public Clazz withoutSubClasses(Clazz value)
   {
      if (this.subClasses != null && this.subClasses.remove(value))
      {
         value.setSuperClass(null);
         this.firePropertyChange(PROPERTY_SUB_CLASSES, value, null);
      }
      return this;
   }

   /**
    * @param value
    *    the subclasses
    *
    * @return this
    *
    * @see Clazz#setSuperClass(Clazz)
    * @since 1.2
    */
   public Clazz withoutSubClasses(Clazz... value)
   {
      for (final Clazz item : value)
      {
         this.withoutSubClasses(item);
      }
      return this;
   }

   /**
    * @param value
    *    the subclasses
    *
    * @return this
    *
    * @see Clazz#setSuperClass(Clazz)
    * @since 1.2
    */
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

   /** @deprecated since 1.2; use one of the type-safe overloads */
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

   /**
    * @param value
    *    the attributes
    *
    * @return this
    *
    * @see Attribute#setClazz(Clazz)
    * @since 1.2
    */
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
         this.firePropertyChange(PROPERTY_ATTRIBUTES, null, value);
      }
      return this;
   }

   /**
    * @param value
    *    the attributes
    *
    * @return this
    *
    * @see Attribute#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withAttributes(Attribute... value)
   {
      for (final Attribute item : value)
      {
         this.withAttributes(item);
      }
      return this;
   }

   /**
    * @param value
    *    the attributes
    *
    * @return this
    *
    * @see Attribute#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withAttributes(Collection<? extends Attribute> value)
   {
      for (final Attribute item : value)
      {
         this.withAttributes(item);
      }
      return this;
   }

   /** @deprecated since 1.2; use one of the type-safe overloads */
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

   /**
    * @param value
    *    the attributes
    *
    * @return this
    *
    * @see Attribute#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withoutAttributes(Attribute value)
   {
      if (this.attributes != null && this.attributes.remove(value))
      {
         value.setClazz(null);
         this.firePropertyChange(PROPERTY_ATTRIBUTES, value, null);
      }
      return this;
   }

   /**
    * @param value
    *    the attributes
    *
    * @return this
    *
    * @see Attribute#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withoutAttributes(Attribute... value)
   {
      for (final Attribute item : value)
      {
         this.withoutAttributes(item);
      }
      return this;
   }

   /**
    * @param value
    *    the attributes
    *
    * @return this
    *
    * @see Attribute#setClazz(Clazz)
    * @since 1.2
    */
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

   /** @deprecated since 1.2; use one of the type-safe overloads */
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

   /**
    * @param value
    *    the roles
    *
    * @return this
    *
    * @see AssocRole#setClazz(Clazz)
    * @since 1.2
    */
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
         this.firePropertyChange(PROPERTY_ROLES, null, value);
      }
      return this;
   }

   /**
    * @param value
    *    the roles
    *
    * @return this
    *
    * @see AssocRole#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withRoles(AssocRole... value)
   {
      for (final AssocRole item : value)
      {
         this.withRoles(item);
      }
      return this;
   }

   /**
    * @param value
    *    the roles
    *
    * @return this
    *
    * @see AssocRole#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withRoles(Collection<? extends AssocRole> value)
   {
      for (final AssocRole item : value)
      {
         this.withRoles(item);
      }
      return this;
   }

   /** @deprecated since 1.2; use one of the type-safe overloads */
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

   /**
    * @param value
    *    the roles
    *
    * @return this
    *
    * @see AssocRole#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withoutRoles(AssocRole value)
   {
      if (this.roles != null && this.roles.remove(value))
      {
         value.setClazz(null);
         this.firePropertyChange(PROPERTY_ROLES, value, null);
      }
      return this;
   }

   /**
    * @param value
    *    the roles
    *
    * @return this
    *
    * @see AssocRole#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withoutRoles(AssocRole... value)
   {
      for (final AssocRole item : value)
      {
         this.withoutRoles(item);
      }
      return this;
   }

   /**
    * @param value
    *    the roles
    *
    * @return this
    *
    * @see AssocRole#setClazz(Clazz)
    * @since 1.2
    */
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

   /** @deprecated since 1.2; use one of the type-safe overloads */
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

   /**
    * @param value
    *    the methods
    *
    * @return this
    *
    * @see FMethod#setClazz(Clazz)
    * @since 1.2
    */
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
         this.firePropertyChange(PROPERTY_METHODS, null, value);
      }
      return this;
   }

   /**
    * @param value
    *    the methods
    *
    * @return this
    *
    * @see FMethod#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withMethods(FMethod... value)
   {
      for (final FMethod item : value)
      {
         this.withMethods(item);
      }
      return this;
   }

   /**
    * @param value
    *    the methods
    *
    * @return this
    *
    * @see FMethod#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withMethods(Collection<? extends FMethod> value)
   {
      for (final FMethod item : value)
      {
         this.withMethods(item);
      }
      return this;
   }

   /** @deprecated since 1.2; use one of the type-safe overloads */
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

   /**
    * @param value
    *    the methods
    *
    * @return this
    *
    * @see FMethod#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withoutMethods(FMethod value)
   {
      if (this.methods != null && this.methods.remove(value))
      {
         value.setClazz(null);
         this.firePropertyChange(PROPERTY_METHODS, value, null);
      }
      return this;
   }

   /**
    * @param value
    *    the methods
    *
    * @return this
    *
    * @see FMethod#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withoutMethods(FMethod... value)
   {
      for (final FMethod item : value)
      {
         this.withoutMethods(item);
      }
      return this;
   }

   /**
    * @param value
    *    the methods
    *
    * @return this
    *
    * @see FMethod#setClazz(Clazz)
    * @since 1.2
    */
   public Clazz withoutMethods(Collection<? extends FMethod> value)
   {
      for (final FMethod item : value)
      {
         this.withoutMethods(item);
      }
      return this;
   }

   /** @deprecated since 1.2; use {@link #getImports()} instead */
   @Deprecated
   public LinkedHashSet<String> getImportList()
   {
      return this.imports != null ? this.imports : (this.imports = new LinkedHashSet<>());
   }

   /** @deprecated since 1.2; use {@link #withImports(Collection)} instead */
   @Deprecated
   public Clazz setImportList(LinkedHashSet<String> value)
   {
      this.imports = value;
      return this;
   }

   /**
    * @return the set of imported members.
    * Elements can have one of the formats {@code org.example.Foo}, {@code static org.example.Foo.bar},
    * {@code import org.example.Foo;} or {@code import static org.example.Foo.bar;}
    *
    * @since 1.2
    */
   public Set<String> getImports()
   {
      return this.imports != null ? Collections.unmodifiableSet(this.imports) : Collections.emptySet();
   }

   /**
    * @param value
    *    the set of imported members.
    *    Elements can have one of the formats {@code org.example.Foo}, {@code static org.example.Foo.bar},
    *    {@code import org.example.Foo;} or {@code import static org.example.Foo.bar;}
    *
    * @return this
    *
    * @since 1.2
    */
   public Clazz withImports(String value)
   {
      if (this.imports == null)
      {
         this.imports = new LinkedHashSet<>();
      }
      if (this.imports.add(value))
      {
         this.firePropertyChange(PROPERTY_IMPORTS, null, value);
      }
      return this;
   }

   /**
    * @param value
    *    the set of imported members.
    *    Elements can have one of the formats {@code org.example.Foo}, {@code static org.example.Foo.bar},
    *    {@code import org.example.Foo;} or {@code import static org.example.Foo.bar;}
    *
    * @return this
    *
    * @since 1.2
    */
   public Clazz withImports(String... value)
   {
      for (final String item : value)
      {
         this.withImports(item);
      }
      return this;
   }

   /**
    * @param value
    *    the set of imported members.
    *    Elements can have one of the formats {@code org.example.Foo}, {@code static org.example.Foo.bar},
    *    {@code import org.example.Foo;} or {@code import static org.example.Foo.bar;}
    *
    * @return this
    *
    * @since 1.2
    */
   public Clazz withImports(Collection<? extends String> value)
   {
      for (final String item : value)
      {
         this.withImports(item);
      }
      return this;
   }

   /**
    * @param value
    *    the set of imported members.
    *    Elements can have one of the formats {@code org.example.Foo}, {@code static org.example.Foo.bar},
    *    {@code import org.example.Foo;} or {@code import static org.example.Foo.bar;}
    *
    * @return this
    *
    * @since 1.2
    */
   public Clazz withoutImports(String value)
   {
      if (this.imports != null && this.imports.removeAll(Collections.singleton(value)))
      {
         this.firePropertyChange(PROPERTY_IMPORTS, value, null);
      }
      return this;
   }

   /**
    * @param value
    *    the set of imported members.
    *    Elements can have one of the formats {@code org.example.Foo}, {@code static org.example.Foo.bar},
    *    {@code import org.example.Foo;} or {@code import static org.example.Foo.bar;}
    *
    * @return this
    *
    * @since 1.2
    */
   public Clazz withoutImports(String... value)
   {
      for (final String item : value)
      {
         this.withoutImports(item);
      }
      return this;
   }

   /**
    * @param value
    *    the set of imported members.
    *    Elements can have one of the formats {@code org.example.Foo}, {@code static org.example.Foo.bar},
    *    {@code import org.example.Foo;} or {@code import static org.example.Foo.bar;}
    *
    * @return this
    *
    * @since 1.2
    */
   public Clazz withoutImports(Collection<? extends String> value)
   {
      for (final String item : value)
      {
         this.withoutImports(item);
      }
      return this;
   }

   /**
    * @return the default property style for attributes and roles.
    * Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.
    */
   public String getPropertyStyle()
   {
      return this.propertyStyle;
   }

   /**
    * @param value
    *    the default property style for attributes and roles.
    *    Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.
    *
    * @return this
    */
   public Clazz setPropertyStyle(String value)
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
   public Clazz setModified(boolean value)
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
      this.withoutSubClasses(new ArrayList<>(this.getSubClasses()));
      this.setSuperClass(null);
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getPropertyStyle());
      result.append(' ').append(this.getImports());
      return result.substring(1);
   }
}
