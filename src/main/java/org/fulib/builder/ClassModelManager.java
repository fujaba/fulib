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
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);

      ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
 * <!-- end_code_fragment:  -->
 * </pre>
 */
public class ClassModelManager implements IModelManager
{
   // =============== Classes ===============

   public class ClassManager
   {
      private final Clazz clazz;

      public ClassManager(Clazz clazz)
      {
         this.clazz = clazz;
      }

      public void imports(String qualifiedName)
      {
         ClassModelManager.this.addImport(this.clazz, qualifiedName);
      }

      public void extend(Clazz superClazz)
      {
         ClassModelManager.this.extend(this.clazz, superClazz);
      }

      public Attribute attribute(String name, String type)
      {
         return ClassModelManager.this.attribute(this.clazz, name, type);
      }

      public Attribute attribute(String name, String type, String init)
      {
         return ClassModelManager.this.attribute(this.clazz, name, type, init);
      }
   }

   // =============== Constants ===============

   public static final String THE_CLASS_MODEL = "theClassModel";

   @Deprecated
   public static final String HAVE_MAIN_JAVA_DIR = "haveMainJavaDir";
   @Deprecated
   public static final String HAVE_PACKAGE_NAME  = "havePackageName";

   public static final String USE_PACKAGE_NAME  = "usePackageName";
   public static final String USE_SOURCE_FOLDER = "useSourceFolder";

   public static final String HAVE_CLASS = "haveClass";

   public static final String ATTRIBUTE  = "attribute";
   public static final String OWNER_NAME = "ownerName";
   public static final String NAME       = "name";
   public static final String TYPE       = "type";
   public static final String INIT       = "init";

   @Deprecated
   public static final String HAVE_ATTRIBUTE = "haveAttribute";
   @Deprecated
   public static final String ATTR_NAME      = "attrName";
   @Deprecated
   public static final String ATTR_TYPE      = "attrType";

   public static final String CLASS_NAME = "className";

   public static final String IMPORT         = "import";
   public static final String QUALIFIED_NAME = "qualifiedName";

   public static final String ASSOCIATE      = "associate";
   public static final String SRC_CLASS_NAME = "srcClassName";
   public static final String TGT_CLASS_NAME = "tgtClassName";
   public static final String SRC_ROLE       = "srcRole";
   public static final String SRC_SIZE       = "srcSize";
   public static final String TGT_ROLE       = "tgtRole";
   public static final String TGT_SIZE       = "tgtSize";
   public static final String BOTH_ROLES     = "bothRoles";

   @Deprecated
   public static final String HAVE_ROLE = "haveRole";
   @Deprecated
   public static final String TGT_CARDINALITY = "tgtCardinality";

   public static final String HAVE_METHOD = "haveMethod";
   public static final String METHOD_BODY = "methodBody";
   public static final String DECLARATION = "declaration";

   @Deprecated
   public static final String METHOD_NAME = "methodName";
   @Deprecated
   public static final String PARAMS      = "params";

   public static final String EXTEND      = "extend";
   public static final String SUB_CLASS   = "subClass";
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
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);

      ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param classModelEventManager
    *    the model event manager
    */
   public ClassModelManager(ModelEventManager classModelEventManager)
   {
      this.setModelEventManager(classModelEventManager);

      this.classModel = new ClassModel().setDefaultPropertyStyle(BEAN).setDefaultCollectionType(CollectionType.ArrayList);
   }

   // =============== Properties ===============

   public ClassModel getClassModel()
   {
      return this.classModel;
   }

   public ModelEventManager getModelEventManager()
   {
      return this.mem;
   }

   public void setModelEventManager(ModelEventManager mem)
   {
      this.mem = mem;
      if (mem != null)
      {
         mem.setModelManager(this);
      }
   }

   // =============== Methods ===============

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
    * Sets the package name to use for generated classes.
    *
    * @param packageName
    *    the package name
    *
    * @since 1.2
    */
   public void setPackageName(String packageName)
   {
      final String oldPackageName = this.classModel.getPackageName();

      if (Objects.equals(oldPackageName, packageName))
      {
         return;
      }

      this.classModel.setPackageName(packageName);

      this.event(e -> {
         e.put(EVENT_TYPE, USE_PACKAGE_NAME);
         e.put(EVENT_KEY, THE_CLASS_MODEL + "_" + PROPERTY_packageName);
         e.put(PROPERTY_packageName, packageName);
      });
   }

   /**
    * @param sourceFolder
    *    the source folder
    *
    * @return this instance, to allow method chaining
    *
    * @deprecated since 1.2; use {@link #setSourceFolder(String)} instead
    */
   @Deprecated
   public ClassModelManager haveMainJavaDir(String sourceFolder)
   {
      this.setSourceFolder(sourceFolder);
      return this;
   }

   /**
    * Sets the source folder to generate model classes into.
    *
    * @param sourceFolder
    *    the source folder
    *
    * @since 1.2
    */
   public void setSourceFolder(String sourceFolder)
   {
      final String mainJavaDir = this.classModel.getMainJavaDir();

      if (Objects.equals(mainJavaDir, sourceFolder))
      {
         return;
      }

      this.classModel.setMainJavaDir(sourceFolder);

      this.event(e -> {
         e.put(EVENT_TYPE, USE_SOURCE_FOLDER);
         e.put(EVENT_KEY, THE_CLASS_MODEL + "_" + PROPERTY_mainJavaDir);
         e.put(PROPERTY_mainJavaDir, sourceFolder);
      });
   }

   // --------------- Classes ---------------

   public Clazz haveClass(String className)
   {
      Clazz clazz = this.classModel.getClazz(className);

      if (clazz != null)
         return clazz; //============================

      clazz = new ClassBuilder(this.classModel, className).getClazz();

      this.event(e -> {
         e.put(EventSource.EVENT_TYPE, HAVE_CLASS);
         e.put(EventSource.EVENT_KEY, className);
         e.put(Clazz.PROPERTY_name, className);
      });

      return clazz;
   }

   public Clazz haveClass(String className, Consumer<? super ClassManager> body)
   {
      final Clazz clazz = this.haveClass(className);
      body.accept(new ClassManager(clazz));
      return clazz;
   }

   public Clazz haveClass(String className, Clazz superClass, Consumer<? super ClassManager> body)
   {
      final Clazz clazz = this.haveClass(className);
      this.extend(clazz, superClass);
      body.accept(new ClassManager(clazz));
      return clazz;
   }

   public void extend(Clazz subClass, Clazz superClass)
   {
      subClass.setSuperClass(superClass);

      this.event(e -> {
         e.put(EVENT_TYPE, EXTEND);
         e.put(EVENT_KEY, subClass.getName());
         e.put(SUB_CLASS, subClass.getName());
         e.put(SUPER_CLASS, superClass.getName());
      });
   }

   public void addImport(Clazz clazz, String qualifiedName)
   {
      clazz.getImportList().add(qualifiedName);

      this.event(e -> {
         e.put(EVENT_TYPE, IMPORT);
         e.put(EVENT_KEY, clazz.getName() + ":" + IMPORT + ":" + qualifiedName);
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
    *
    * @deprecated since 1.2; use {@link #attribute(Clazz, String, String)} instead
    */
   @Deprecated
   public Attribute haveAttribute(Clazz owner, String name, String type)
   {
      return this.attribute(owner, name, type, null);
   }

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
    *
    * @since 1.2
    */
   public Attribute attribute(Clazz owner, String name, String type)
   {
      return this.attribute(owner, name, type, null);
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
   public Attribute attribute(Clazz owner, String name, String type, String init)
   {
      Attribute attr = owner.getAttribute(name);

      if (attr == null)
      {
         Validator.checkSimpleName(name);
         if (owner.getRole(name) != null)
         {
            throw new IllegalArgumentException(
               "cannot create attribute with name '" + name + "', a role with that name already exists");
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
         e.put(EVENT_TYPE, ATTRIBUTE);
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
    * Creates an association like {@link #associate(Clazz, String, int, Clazz, String, int)},
    * but with the target role name inferred from the name of the source class,
    * and the target cardinality 1.
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
    * @deprecated use {@link #associate(Clazz, String, int, Clazz)}, which provides better parameter symmetry.
    */
   @Deprecated
   public AssocRole haveRole(Clazz srcClass, String srcRole, Clazz tgtClass, int srcSize)
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
      return this.associate(srcClass, srcRole, srcSize, tgtClass, otherRoleName, Type.ONE, false);
   }

   /**
    * Creates an association from the source class to the target class.
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
    * @deprecated use {@link #associate(Clazz, String, int, Clazz, String, int)}, which provides better parameter symmetry.
    */
   @Deprecated
   public AssocRole haveRole(Clazz srcClass, String srcRole, Clazz tgtClass, int srcSize, String tgtRole, int tgtSize)
   {
      return this.associate(srcClass, srcRole, srcSize, tgtClass, tgtRole, tgtSize, true);
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
      return this.associate(srcClass, srcRole, srcSize, tgtClass, tgtRole, tgtSize, true);
   }

   private AssocRole associate(Clazz srcClass, String srcRole, int srcSize, Clazz tgtClass, String tgtRole, int tgtSize,
      boolean bothRoles)
   {
      AssocRole role = srcClass.getRole(srcRole);

      if (role != null && role.getCardinality() >= srcSize && role.getOther().getClazz() == tgtClass && (!bothRoles
                                                                                                         || role
                                                                                                            .getOther()
                                                                                                            .getName()
                                                                                                            .equals(
                                                                                                               tgtRole))
          && (!bothRoles || role.getOther().getCardinality() >= tgtSize))
         return role; //===============================================================

      if (Objects.equals(srcRole, tgtRole))
         tgtSize = srcSize;

      if (role == null)
      {
         role = new AssocRole().setClazz(srcClass).setName(srcRole).setCardinality(srcSize)
                               .setPropertyStyle(srcClass.getPropertyStyle())
                               .setCollectionType(srcClass.getModel().getDefaultCollectionType());

         AssocRole otherRole = new AssocRole().setClazz(tgtClass).setName(tgtRole).setCardinality(tgtSize)
                                              .setPropertyStyle(tgtClass.getPropertyStyle())
                                              .setCollectionType(tgtClass.getModel().getDefaultCollectionType());

         role.setOther(otherRole);
      }

      int maxSize = Math.max(role.getCardinality(), srcSize);
      role.setCardinality(maxSize);

      int maxTgtSize = Math.max(role.getOther().getCardinality(), tgtSize);
      if (bothRoles)
         role.getOther().setName(tgtRole);
      role.getOther().setCardinality(tgtSize);

      // mm.haveRole(currentRegisterClazz, srcRole, tgtClass, srcSize, tgtRole, ClassModelBuilder.ONE);
      this.event(e -> {
         e.put(EVENT_TYPE, ASSOCIATE);
         e.put(EVENT_KEY, srcClass.getName() + "." + srcRole);

         e.put(SRC_CLASS_NAME, srcClass.getName());
         e.put(SRC_ROLE, srcRole);
         e.put(SRC_SIZE, Integer.toString(maxSize));
         e.put(TGT_CLASS_NAME, tgtClass.getName());
         e.put(TGT_ROLE, tgtRole);
         e.put(TGT_SIZE, Integer.toString(maxTgtSize));
         e.put(BOTH_ROLES, Boolean.toString(bothRoles));
      });

      return role;
   }

   // --------------- Methods ---------------

   public FMethod haveMethod(Clazz srcClass, String declaration)
   {
      return this.haveMethod(srcClass, declaration, null);
   }

   public FMethod haveMethod(Clazz clazz, String declaration, String body)
   {

      FMethod method = null;
      for (FMethod fMethod : clazz.getMethods())
      {
         if (fMethod.getDeclaration().equals(declaration))
         {
            method = fMethod;
            break;
         }
      }

      if (method != null)
      {
         if (body == null || body.equals(method.getMethodBody()))
         {
            return method;
         }
      }

      if (method == null)
      {
         method = new FMethod();
      }

      // need a final variable due to use in lambda expression below.
      final FMethod foundMethod = method;

      foundMethod.setClazz(clazz).setDeclaration(declaration).setMethodBody(body);

      String key = clazz.getName() + "." + foundMethod.getDeclaration();

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_METHOD);
         e.put(EVENT_KEY, key);
         e.put(CLASS_NAME, clazz.getName());
         e.put(DECLARATION, foundMethod.getDeclaration());
         e.put(METHOD_BODY, foundMethod.getMethodBody());
      });

      return method;
   }

   public FMethod getMethod(String declaration)
   {
      for (Clazz clazz : this.getClassModel().getClasses())
      {
         for (FMethod fMethod : clazz.getMethods())
         {
            if (fMethod.getDeclaration().equals(declaration))
            {
               return fMethod;
            }
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

   @Override
   public void initConsumers(Map<String, Consumer<? super Map<String, String>>> consumerMap)
   {
      final Consumer<Map<String, String>> usePackageName = map -> {
         final String packageName = map.get(PROPERTY_packageName);
         this.setPackageName(packageName);
      };
      consumerMap.put(USE_PACKAGE_NAME, usePackageName);
      consumerMap.put("havePackageName", usePackageName); // legacy name

      final Consumer<Map<String, String>> useSourceFolder = map -> {
         final String sourceFolder = map.get(PROPERTY_mainJavaDir);
         this.setSourceFolder(sourceFolder);
      };
      consumerMap.put(USE_SOURCE_FOLDER, useSourceFolder);
      consumerMap.put("haveMainJavaDir", useSourceFolder); // legacy name

      consumerMap.put(HAVE_CLASS, map -> {
         String name = map.get(PROPERTY_name);
         this.haveClass(name);
      });

      consumerMap.put(EXTEND, map -> {
         Clazz subClass = this.haveClass(map.get(SUB_CLASS));
         Clazz superClass = this.haveClass(map.get(SUPER_CLASS));
         this.extend(subClass, superClass);
      });

      consumerMap.put(IMPORT, map -> {
         final String className = map.get(CLASS_NAME);
         final String qualifiedName = map.get(QUALIFIED_NAME);
         final Clazz clazz = this.haveClass(className);
         this.addImport(clazz, qualifiedName);
      });

      consumerMap.put(ATTRIBUTE, map -> {
         final String ownerName = map.get(OWNER_NAME);
         final String name = map.get(NAME);
         final String type = map.get(TYPE);
         final String init = map.get(INIT);

         final Clazz owner = this.haveClass(ownerName);
         this.attribute(owner, name, type, init);
      });

      // legacy naming
      consumerMap.put("haveAttribute", map -> {
         String className = map.get("className");
         String attrName = map.get("attrName");
         String attrType = map.get("attrType");

         Clazz clazz = this.haveClass(className);
         this.attribute(clazz, attrName, attrType);
      });

      final Consumer<Map<String, String>> associateHandler = map -> {
         final String srcClassName = map.get(SRC_CLASS_NAME);
         final String srcRole = map.get(SRC_ROLE);
         final int srcSize = Integer.parseInt(map.get(SRC_SIZE));
         final String tgtClassName = map.get(TGT_CLASS_NAME);
         final String tgtRole = map.get(TGT_ROLE);
         final int tgtSize = Integer.parseInt(map.get(TGT_SIZE));
         final boolean bothRoles = Boolean.parseBoolean(map.get(BOTH_ROLES));

         final Clazz srcClazz = this.haveClass(srcClassName);
         final Clazz tgtClazz = this.haveClass(tgtClassName);

         this.associate(srcClazz, srcRole, srcSize, tgtClazz, tgtRole, tgtSize, bothRoles);
      };

      consumerMap.put(ASSOCIATE, associateHandler);
      consumerMap.put("haveRole", associateHandler); // legacy name
   }
}
