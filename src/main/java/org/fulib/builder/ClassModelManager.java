package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.StrUtil;
import org.fulib.classmodel.*;
import org.fulib.util.Validator;
import org.fulib.yaml.EventSource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.fulib.builder.Type.BEAN;
import static org.fulib.classmodel.ClassModel.PROPERTY_mainJavaDir;
import static org.fulib.classmodel.ClassModel.PROPERTY_packageName;
import static org.fulib.classmodel.Clazz.PROPERTY_name;
import static org.fulib.yaml.EventSource.EVENT_KEY;
import static org.fulib.yaml.EventSource.EVENT_TYPE;

/**
 * ClassModelbuilder is used to create fulib class models that are input for
 * fulib code generation {@link Fulib#generator()}.<br>
 * Typical usage:
 * <pre>
 * <!-- insert_code_fragment: ClassModelBuilder -->
 * ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);
 *
 * ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
 * <!-- end_code_fragment:  -->
 * </pre>
 */
public class ClassModelManager implements IModelManager
{
   // =============== Classes ===============

   /**
    * @since 1.2
    */
   public class ClassManager
   {
      private final Clazz clazz;

      public ClassManager(Clazz clazz)
      {
         this.clazz = clazz;
      }

      public void imports(String qualifiedName)
      {
         ClassModelManager.this.haveImport(this.clazz, qualifiedName);
      }

      public void extend(Clazz superClazz)
      {
         ClassModelManager.this.haveSuper(this.clazz, superClazz);
      }

      public Attribute attribute(String name, String type)
      {
         return ClassModelManager.this.haveAttribute(this.clazz, name, type);
      }

      public Attribute attribute(String name, String type, String init)
      {
         return ClassModelManager.this.haveAttribute(this.clazz, name, type, init);
      }
   }

   // =============== Constants ===============

   public static final String THE_CLASS_MODEL = "theClassModel";

   /** @deprecated since 1.2; use {@link #SET_SOURCE_FOLDER} instead */
   @Deprecated
   public static final String HAVE_MAIN_JAVA_DIR = "haveMainJavaDir";
   /** @deprecated since 1.2; use {@link #SET_PACKAGE_NAME} instead */
   @Deprecated
   public static final String HAVE_PACKAGE_NAME = "havePackageName";

   /** @since 1.2 */
   public static final String SET_PACKAGE_NAME = "setPackageName";
   /** @since 1.2 */
   public static final String SET_SOURCE_FOLDER = "setSourceFolder";

   public static final String HAVE_CLASS = "haveClass";

   public static final String HAVE_ATTRIBUTE = "haveAttribute";
   /** @since 1.2 */
   public static final String OWNER_NAME = "ownerName";
   /** @since 1.2 */
   public static final String NAME = "name";
   /** @since 1.2 */
   public static final String TYPE = "type";
   /** @since 1.2 */
   public static final String INIT = "init";
   /** @deprecated since 1.2; use {@link #NAME} instead */
   @Deprecated
   public static final String ATTR_NAME = "attrName";
   /** @deprecated since 1.2; use {@link #TYPE} instead */
   @Deprecated
   public static final String ATTR_TYPE = "attrType";

   public static final String CLASS_NAME = "className";

   /** @since 1.2 */
   public static final String HAVE_IMPORT = "haveImport";
   /** @since 1.2 */
   public static final String QUALIFIED_NAME = "qualifiedName";

   /** @since 1.2 */
   public static final String ASSOCIATE = "associate";
   public static final String SRC_CLASS_NAME = "srcClassName";
   public static final String TGT_CLASS_NAME = "tgtClassName";
   public static final String SRC_ROLE = "srcRole";
   public static final String SRC_SIZE = "srcSize";
   public static final String TGT_ROLE = "tgtRole";
   public static final String TGT_SIZE = "tgtSize";

   /** @deprecated since 1.2; use {@link #ASSOCIATE} instead */
   @Deprecated
   public static final String HAVE_ROLE = "haveRole";
   /** @deprecated since 1.2; use {@link #TGT_SIZE} instead */
   @Deprecated
   public static final String TGT_CARDINALITY = "tgtCardinality";

   public static final String HAVE_METHOD = "haveMethod";
   public static final String METHOD_BODY = "methodBody";
   public static final String DECLARATION = "declaration";

   /** @deprecated since 1.2; unused */
   @Deprecated
   public static final String METHOD_NAME = "methodName";
   /** @deprecated since 1.2; unused */
   @Deprecated
   public static final String PARAMS = "params";

   /** @since 1.2 */
   public static final String HAVE_SUPER = "haveSuper";
   /** @since 1.2 */
   public static final String SUB_CLASS = "subClass";
   /** @since 1.2 */
   public static final String SUPER_CLASS = "superClass";

   // =============== Fields ===============

   private final ClassModel classModel;

   private ModelEventManager mem;

   // =============== Constructors ===============

   /**
    * ClassModelManager is used to manage fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}. Managed class models may
    * be merged from multiple inputs<br>
    */
   public ClassModelManager()
   {
      this(null);
   }

   /**
    * ClassModelManager is used to manage fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}. Managed class models may
    * be merged from multiple inputs<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
    * ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);
    *
    * ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param classModelEventManager
    *    the model event manager
    */
   public ClassModelManager(ModelEventManager classModelEventManager)
   {
      this.setModelEventManager(classModelEventManager);

      this.classModel = new ClassModel()
         .setDefaultPropertyStyle(BEAN)
         .setDefaultCollectionType(CollectionType.ArrayList);
   }

   // =============== Properties ===============

   public ClassModel getClassModel()
   {
      return this.classModel;
   }

   /**
    * @return the model event manager
    *
    * @since 1.2
    */
   public ModelEventManager getModelEventManager()
   {
      return this.mem;
   }

   /**
    * @param mem
    *    the model event manager
    *
    * @since 1.2
    */
   public void setModelEventManager(ModelEventManager mem)
   {
      if (mem == this.mem)
      {
         return;
      }

      if (this.mem != null)
      {
         this.mem.setModelManager(null);
      }
      this.mem = mem;
      if (mem != null)
      {
         mem.setModelManager(this);
      }
   }

   // =============== Methods ===============

   /**
    * Creates a {@link ClassModelBuilder} that operates on the same underlying class model as this manager.
    * Note that changes made using the builder are not recorded as events.
    *
    * @return a {@link ClassModelBuilder} that operates on the same underlying class model as this manager
    *
    * @since 1.2
    */
   public ClassModelBuilder asBuilder()
   {
      return new ClassModelBuilder(this.classModel);
   }

   // --------------- Settings ---------------

   /**
    * @param packagename
    *    the package name
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setPackageName(String)} instead
    */
   @Deprecated
   public ClassModelManager havePackageName(String packagename)
   {
      this.setPackageName(packagename);
      return this;
   }

   /**
    * @param packageName
    *    the package name to use for generated classes
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public ClassModelManager setPackageName(String packageName)
   {
      final String oldPackageName = this.classModel.getPackageName();

      if (Objects.equals(oldPackageName, packageName))
      {
         return this;
      }

      this.classModel.setPackageName(packageName);

      this.event(e -> {
         e.put(EVENT_TYPE, SET_PACKAGE_NAME);
         e.put(EVENT_KEY, THE_CLASS_MODEL + "_" + PROPERTY_packageName);
         e.put(PROPERTY_packageName, packageName);
      });

      return this;
   }

   /**
    * @param sourceDir
    *    the source directory
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setMainJavaDir(String)} instead
    */
   @Deprecated
   public ClassModelManager haveMainJavaDir(String sourceDir)
   {
      this.setMainJavaDir(sourceDir);
      return this;
   }

   /**
    * @param mainJavaDir
    *    the source directory to generate model classes into
    *
    * @return this instance, to allow method chaining
    *
    * @since 1.2
    */
   public ClassModelManager setMainJavaDir(String mainJavaDir)
   {
      final String old = this.classModel.getMainJavaDir();

      if (Objects.equals(old, mainJavaDir))
      {
         return this;
      }

      this.classModel.setMainJavaDir(mainJavaDir);

      this.event(e -> {
         e.put(EVENT_TYPE, SET_SOURCE_FOLDER);
         e.put(EVENT_KEY, THE_CLASS_MODEL + "_" + PROPERTY_mainJavaDir);
         e.put(PROPERTY_mainJavaDir, mainJavaDir);
      });

      return this;
   }

   // --------------- Classes ---------------

   public Clazz haveClass(String name)
   {
      Clazz clazz = this.classModel.getClazz(name);

      if (clazz != null)
      {
         return clazz;
      }

      Validator.checkSimpleName(name);
      Validator.checkJavaLangNameClash(name);

      clazz = new Clazz();
      clazz.setModel(classModel);
      clazz.setName(name);
      clazz.setPropertyStyle(classModel.getDefaultPropertyStyle());

      this.event(e -> {
         e.put(EventSource.EVENT_TYPE, HAVE_CLASS);
         e.put(EventSource.EVENT_KEY, name);
         e.put(Clazz.PROPERTY_name, name);
      });

      return clazz;
   }

   /**
    * Gets or creates a class with the given name.
    * In either case, the consumer is invoked to configure the resulting class using the {@link ClassManager} API.
    *
    * @param name
    *    the class name
    * @param body
    *    a lambda expression that configures the class
    *
    * @return the class with the given name
    *
    * @since 1.2
    */
   public Clazz haveClass(String name, Consumer<? super ClassManager> body)
   {
      final Clazz clazz = this.haveClass(name);
      body.accept(new ClassManager(clazz));
      return clazz;
   }

   /**
    * Gets or creates a class with the given name and super class.
    *
    * @param name
    *    the class name
    * @param superClass
    *    the super class
    *
    * @return the class with the given name
    *
    * @since 1.4
    */
   public Clazz haveClass(String name, Clazz superClass)
   {
      final Clazz clazz = this.haveClass(name);
      this.haveSuper(clazz, superClass);
      return clazz;
   }

   /**
    * Gets or creates a class with the given name and super class.
    * In either case, the consumer is invoked to configure the resulting class using the {@link ClassManager} API.
    *
    * @param name
    *    the class name
    * @param superClass
    *    the super class
    * @param body
    *    a lambda expression that configures the class
    *
    * @return the class with the given name
    *
    * @since 1.2
    */
   public Clazz haveClass(String name, Clazz superClass, Consumer<? super ClassManager> body)
   {
      final Clazz clazz = this.haveClass(name, superClass);
      body.accept(new ClassManager(clazz));
      return clazz;
   }

   /**
    * Configures the sub class to extend the given super class.
    *
    * @param subClass
    *    the class to modify
    * @param superClass
    *    the super class that the sub class should extend
    *
    * @since 1.2
    */
   public void haveSuper(Clazz subClass, Clazz superClass)
   {
      subClass.setSuperClass(superClass);

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_SUPER);
         e.put(EVENT_KEY, subClass.getName());
         e.put(SUB_CLASS, subClass.getName());
         e.put(SUPER_CLASS, superClass.getName());
      });
   }

   /**
    * Configures the class to have an import for the given qualified name.
    *
    * @param clazz
    *    the class to modify
    * @param qualifiedName
    *    the qualified name that should be imported by the class
    *
    * @since 1.2
    */
   public void haveImport(Clazz clazz, String qualifiedName)
   {
      clazz.withImports(qualifiedName);

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_IMPORT);
         e.put(EVENT_KEY, clazz.getName() + ":" + HAVE_IMPORT + ":" + qualifiedName);
         e.put(CLASS_NAME, clazz.getName());
         e.put(QUALIFIED_NAME, qualifiedName);
      });
   }

   // --------------- Attributes ---------------

   /**
    * Creates an attribute with the specified name and type in the owner class.
    *
    * @param owner
    *    the owner class
    * @param name
    *    the name
    * @param type
    *    the type
    *
    * @return the new {@link Attribute}
    */
   public Attribute haveAttribute(Clazz owner, String name, String type)
   {
      return this.haveAttribute(owner, name, type, null);
   }

   /**
    * Creates an attribute with the specified name, type and initialization expression in the owner class.
    *
    * @param owner
    *    the owner class
    * @param name
    *    the name
    * @param type
    *    the type
    * @param init
    *    the initialization expression
    *
    * @return the new {@link Attribute}
    *
    * @since 1.2
    */
   public Attribute haveAttribute(Clazz owner, String name, String type, String init)
   {
      Attribute attr = owner.getAttribute(name);

      if (attr == null)
      {
         Validator.checkSimpleName(name);
         if (owner.getRole(name) != null)
         {
            throw new IllegalArgumentException(
               String.format("cannot create attribute '%s.%s', a role with that name already exists", owner.getName(),
                             name));
         }

         attr = new Attribute();
         attr.setName(name);
         attr.setClazz(owner);
         attr.setPropertyStyle(owner.getPropertyStyle());
      }
      else if (Objects.equals(attr.getType(), type) && Objects.equals(attr.getInitialization(), init))
      {
         return attr;
      }

      attr.setType(type);
      attr.setInitialization(init);

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_ATTRIBUTE);
         e.put(EVENT_KEY, owner.getName() + "." + name);
         e.put(OWNER_NAME, owner.getName());
         e.put(NAME, name);
         e.put(TYPE, type);

         if (init != null)
         {
            e.put(INIT, init);
         }
      });

      return attr;
   }

   // --------------- Associations ---------------

   /**
    * Alias {@link #haveRole(Clazz, String, int, Clazz)}, but with the last two parameters swapped.
    *
    * @param srcClass
    *    the source class
    * @param tgtClass
    *    the target class
    * @param srcRole
    *    the role name in the source class
    * @param srcSize
    *    the cardinality in the source class
    *
    * @return the new {@link AssocRole} in the source class.
    *
    * @deprecated since 1.2; use {@link #haveRole(Clazz, String, int, Clazz)}, which provides better parameter symmetry.
    */
   @Deprecated
   public AssocRole haveRole(Clazz srcClass, String srcRole, Clazz tgtClass, int srcSize)
   {
      return this.associate(srcClass, srcRole, srcSize, tgtClass);
   }

   /**
    * Alias for {@link #associate(Clazz, String, int, Clazz)}.
    *
    * @param srcClass
    *    the source class
    * @param srcRole
    *    the role name in the source class
    * @param srcSize
    *    the cardinality in the source class
    * @param tgtClass
    *    the target class
    *
    * @return the new {@link AssocRole} in the source class.
    *
    * @since 1.3
    */
   public AssocRole haveRole(Clazz srcClass, String srcRole, int srcSize, Clazz tgtClass)
   {
      return this.associate(srcClass, srcRole, srcSize, tgtClass);
   }

   /**
    * Creates an association like {@link #associate(Clazz, String, int, Clazz, String, int)},
    * but with the target role name inferred from the name of the source class,
    * and the target cardinality 1.
    *
    * @param srcClass
    *    the source class
    * @param srcRole
    *    the role name in the source class
    * @param srcSize
    *    the cardinality in the source class
    * @param tgtClass
    *    the target class
    *
    * @return the new {@link AssocRole} in the source class.
    *
    * @since 1.2
    */
   public AssocRole associate(Clazz srcClass, String srcRole, int srcSize, Clazz tgtClass)
   {
      String otherRoleName = StrUtil.downFirstChar(srcClass.getName());
      return this.associate(srcClass, srcRole, srcSize, tgtClass, otherRoleName, Type.ONE);
   }

   /**
    * Alias for {@link #haveRole(Clazz, String, int, Clazz, String, int)}, but with the third and fourth parameters
    * swapped.
    *
    * @param srcClass
    *    the source class
    * @param srcRole
    *    the role name in the source class
    * @param tgtClass
    *    the target class
    * @param srcSize
    *    the cardinality in the source class
    * @param tgtRole
    *    the role name in the target class
    * @param tgtSize
    *    the cardinality in the target class
    *
    * @return the new {@link AssocRole} in the source class.
    *
    * @deprecated since 1.2; use {@link #haveRole(Clazz, String, int, Clazz, String, int)}, which provides better
    * parameter symmetry.
    */
   @Deprecated
   public AssocRole haveRole(Clazz srcClass, String srcRole, Clazz tgtClass, int srcSize, String tgtRole, int tgtSize)
   {
      return this.associate(srcClass, srcRole, srcSize, tgtClass, tgtRole, tgtSize);
   }

   /**
    * Alias for {@link #associate(Clazz, String, int, Clazz, String, int)}.
    *
    * @param srcClass
    *    the source class
    * @param srcRole
    *    the role name in the source class
    * @param srcSize
    *    the cardinality in the source class
    * @param tgtClass
    *    the target class
    * @param tgtRole
    *    the role name in the target class
    * @param tgtSize
    *    the cardinality in the target class
    *
    * @return the new {@link AssocRole} in the source class.
    *
    * @since 1.3
    */
   public AssocRole haveRole(Clazz srcClass, String srcRole, int srcSize, Clazz tgtClass, String tgtRole, int tgtSize)
   {
      return this.associate(srcClass, srcRole, srcSize, tgtClass, tgtRole, tgtSize);
   }

   /**
    * Creates an association from the source class to the target class.
    *
    * @param srcClass
    *    the source class
    * @param srcRole
    *    the role name in the source class
    * @param srcSize
    *    the cardinality in the source class
    * @param tgtClass
    *    the target class
    * @param tgtRole
    *    the role name in the target class
    * @param tgtSize
    *    the cardinality in the target class
    *
    * @return the new {@link AssocRole} in the source class.
    *
    * @since 1.2
    */
   public AssocRole associate(Clazz srcClass, String srcRole, int srcSize, Clazz tgtClass, String tgtRole, int tgtSize)
   {
      final AtomicBoolean modified = new AtomicBoolean(false);

      final AssocRole role = this.haveRole(srcClass, srcRole, srcSize, modified);
      final AssocRole other = this.haveRole(tgtClass, tgtRole, tgtSize, modified);

      this.link(role, other, modified);

      if (!modified.get())
      {
         return role;
      }

      this.event(e -> {
         e.put(EVENT_TYPE, ASSOCIATE);
         e.put(EVENT_KEY, srcClass.getName() + "." + srcRole);

         e.put(SRC_CLASS_NAME, srcClass.getName());
         e.put(SRC_ROLE, srcRole);
         e.put(SRC_SIZE, Integer.toString(srcSize));
         e.put(TGT_CLASS_NAME, tgtClass.getName());
         e.put(TGT_ROLE, tgtRole);
         e.put(TGT_SIZE, Integer.toString(tgtSize));
      });

      return role;
   }

   private AssocRole haveRole(Clazz owner, String name, int cardinality, AtomicBoolean modified)
   {
      AssocRole role = owner.getRole(name);
      if (role == null)
      {
         if (owner.getAttribute(name) != null)
         {
            throw new IllegalArgumentException(
               String.format("cannot create role '%s.%s', an attribute with that name already exists", owner.getName(),
                             name));
         }

         modified.set(true);

         role = new AssocRole()
            .setClazz(owner)
            .setName(name)
            .setCardinality(cardinality)
            .setPropertyStyle(owner.getPropertyStyle())
            .setCollectionType(owner.getModel().getDefaultCollectionType());
      }
      else if (role.getCardinality() == cardinality)
      {
         return role;
      }

      modified.set(true);
      role.setCardinality(cardinality);
      return role;
   }

   private void link(AssocRole src, AssocRole tgt, AtomicBoolean modified)
   {
      final AssocRole oldTgt = src.getOther();
      if (oldTgt == tgt)
      {
         return;
      }

      if (oldTgt != null)
      {
         throw new IllegalArgumentException(
            String.format("role '%s.%s' is already linked to '%s.%s'", src.getClazz().getName(), src.getName(),
                          oldTgt.getClazz().getName(), oldTgt.getName()));
      }

      final AssocRole oldSrc = tgt.getOther();
      if (oldSrc != null)
      {
         throw new IllegalArgumentException(
            String.format("role '%s.%s' is already linked to '%s.%s'", tgt.getClazz().getName(), tgt.getName(),
                          oldSrc.getClazz().getName(), oldSrc.getName()));
      }

      modified.set(true);
      src.setOther(tgt);
   }

   // --------------- Methods ---------------

   public FMethod haveMethod(Clazz owner, String declaration)
   {
      return this.haveMethod(owner, declaration, null);
   }

   public FMethod haveMethod(Clazz owner, String declaration, String body)
   {
      FMethod method = this.getMethod(owner, declaration);

      if (method == null)
      {
         method = new FMethod();
      }
      else if (body == null || body.equals(method.getMethodBody()))
      {
         return method;
      }

      method.setClazz(owner).setDeclaration(declaration).setMethodBody(body);

      final String key = method.getSignature();

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_METHOD);
         e.put(EVENT_KEY, key);
         e.put(CLASS_NAME, owner.getName());
         e.put(DECLARATION, declaration);
         e.put(METHOD_BODY, body);
      });

      return method;
   }

   /**
    * Finds a methods with the given {@linkplain FMethod#getDeclaration() declaration}
    *
    * @param declaration
    *    the {@linkplain FMethod#getDeclaration() declaration} of the method to search for
    *
    * @return a method in any class of the managed class model that has the given declaration,
    * or {@code null} if none is found
    *
    * @deprecated since 1.2; when multiple classes have a method with the same declaration, the result is undefined.
    * Use {@link #getMethod(Clazz, String)} instead.
    */
   @Deprecated
   public FMethod getMethod(String declaration)
   {
      for (Clazz clazz : this.getClassModel().getClasses())
      {
         final FMethod method = this.getMethod(clazz, declaration);
         if (method != null)
         {
            return method;
         }
      }
      return null;
   }

   /**
    * Finds a method with the given {@linkplain FMethod#getDeclaration() declaration} in the given {@code owner} class.
    *
    * @param owner
    *    the class in which to search
    * @param declaration
    *    the {@linkplain FMethod#getDeclaration() declaration} of the method to search for
    *
    * @return a method in the given class that has the given declaration, or {@code null} if none is found
    *
    * @since 1.2
    */
   public FMethod getMethod(Clazz owner, String declaration)
   {
      for (FMethod fMethod : owner.getMethods())
      {
         if (fMethod.getDeclaration().equals(declaration))
         {
            return fMethod;
         }
      }
      return null;
   }

   // --------------- Events ---------------

   private void event(Consumer<? super Map<String, String>> populator)
   {
      if (this.mem == null)
      {
         return;
      }

      final Map<String, String> map = new LinkedHashMap<>();
      populator.accept(map);
      this.mem.append(map);
   }

   /**
    * {@inheritDoc}
    *
    * @deprecated since 1.2; use {@link #initConsumers(Map)} instead
    */
   @Deprecated
   @Override
   public void initConsumers(LinkedHashMap<String, Consumer<LinkedHashMap<String, String>>> consumerMap)
   {
      final Map<String, Consumer<? super Map<String, String>>> map = new HashMap<>();
      this.initConsumers(map);

      for (final Map.Entry<String, Consumer<? super Map<String, String>>> entry : map.entrySet())
      {
         final Consumer<? super Map<String, String>> value = entry.getValue();
         final Consumer<LinkedHashMap<String, String>> bridge = value::accept;
         consumerMap.put(entry.getKey(), bridge);
      }
   }

   /**
    * {@inheritDoc}
    *
    * @since 1.2
    */
   @Override
   public void initConsumers(Map<String, Consumer<? super Map<String, String>>> consumerMap)
   {
      final Consumer<Map<String, String>> usePackageName = map -> {
         final String packageName = map.get(PROPERTY_packageName);
         this.setPackageName(packageName);
      };
      consumerMap.put(SET_PACKAGE_NAME, usePackageName);
      consumerMap.put(HAVE_PACKAGE_NAME, usePackageName); // legacy name

      final Consumer<Map<String, String>> useSourceFolder = map -> {
         final String sourceFolder = map.get(PROPERTY_mainJavaDir);
         this.setMainJavaDir(sourceFolder);
      };
      consumerMap.put(SET_SOURCE_FOLDER, useSourceFolder);
      consumerMap.put(HAVE_MAIN_JAVA_DIR, useSourceFolder); // legacy name

      consumerMap.put(HAVE_CLASS, map -> {
         String name = map.get(PROPERTY_name);
         this.haveClass(name);
      });

      consumerMap.put(HAVE_SUPER, map -> {
         Clazz subClass = this.haveClass(map.get(SUB_CLASS));
         Clazz superClass = this.haveClass(map.get(SUPER_CLASS));
         this.haveSuper(subClass, superClass);
      });

      consumerMap.put(HAVE_IMPORT, map -> {
         final String className = map.get(CLASS_NAME);
         final String qualifiedName = map.get(QUALIFIED_NAME);
         final Clazz clazz = this.haveClass(className);
         this.haveImport(clazz, qualifiedName);
      });

      consumerMap.put(HAVE_ATTRIBUTE, map -> {
         // legacy naming
         final String ownerName = map.getOrDefault(OWNER_NAME, map.get(CLASS_NAME));
         final String name = map.getOrDefault(NAME, map.get(ATTR_NAME));
         final String type = map.getOrDefault(TYPE, map.get(ATTR_TYPE));
         final String init = map.get(INIT);

         final Clazz owner = this.haveClass(ownerName);
         this.haveAttribute(owner, name, type, init);
      });

      consumerMap.put(ASSOCIATE, map -> {
         final String srcClassName = map.get(SRC_CLASS_NAME);
         final String srcRole = map.get(SRC_ROLE);
         final int srcSize = Integer.parseInt(map.get(SRC_SIZE));
         final String tgtClassName = map.get(TGT_CLASS_NAME);
         final String tgtRole = map.get(TGT_ROLE);
         final int tgtSize = Integer.parseInt(map.get(TGT_SIZE));

         final Clazz srcClazz = this.haveClass(srcClassName);
         final Clazz tgtClazz = this.haveClass(tgtClassName);

         this.associate(srcClazz, srcRole, srcSize, tgtClazz, tgtRole, tgtSize);
      });

      // legacy format
      consumerMap.put(HAVE_ROLE, map -> {
         final String srcClassName = map.get(SRC_CLASS_NAME);
         final String attrName = map.get(ATTR_NAME);
         final String tgtClassName = map.get(TGT_CLASS_NAME);

         final Clazz srcClazz = haveClass(srcClassName);
         final Clazz tgClazz = haveClass(tgtClassName);
         final int size = Integer.parseInt(map.get(TGT_CARDINALITY));

         this.haveRole(srcClazz, attrName, tgClazz, size);
      });

      consumerMap.put(HAVE_METHOD, map -> {
         final String className = map.get(CLASS_NAME);
         final String declaration = map.get(DECLARATION);
         final String body = map.get(METHOD_BODY);

         final Clazz clazz = this.haveClass(className);

         this.haveMethod(clazz, declaration, body);
      });
   }
}
