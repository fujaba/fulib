package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.StrUtil;
import org.fulib.classmodel.*;
import org.fulib.yaml.EventSource;
import org.fulib.yaml.Yamler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.fulib.builder.ClassModelBuilder.COLLECTION_ARRAY_LIST;
import static org.fulib.builder.ClassModelBuilder.POJO;
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
        ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

        ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", mb.STRING);
      * <!-- end_code_fragment:  -->
 * </pre>
 *
 */
public class ClassModelManager implements IModelManager
{
   // =============== Constants ===============

   public static final String THE_CLASS_MODEL = "theClassModel";
   public static final String HAVE_PACKAGE_NAME = "havePackageName";
   public static final String HAVE_MAIN_JAVA_DIR = "haveMainJavaDir";
   public static final String HAVE_CLASS = "haveClass";
   public static final String HAVE_ATTRIBUTE = "haveAttribute";
   public static final String CLASS_NAME = "className";
   public static final String ATTR_NAME = "attrName";
   public static final String ATTR_TYPE = "attrType";
   public static final String HAVE_ROLE = "haveRole";
   public static final String SRC_CLASS_NAME = "srcClassName";
   public static final String TGT_CLASS_NAME = "tgtClassName";
   public static final String TGT_CARDINALITY = "tgtCardinality";
   public static final String SRC_ROLE = "srcRole";
   public static final String SRC_SIZE = "srcSize";
   public static final String TGT_ROLE = "tgtRole";
   public static final String TGT_SIZE = "tgtSize";
   public static final String HAVE_METHOD = "haveMethod";
   public static final String METHOD_NAME = "methodName";
   public static final String PARAMS = "params";
   public static final String METHOD_BODY = "methodBody";
   public static final String DECLARATION = "declaration";
   public static final String HAVE_SUPER = "haveSuper";
   public static final String SUB_CLASS = "subClass";
   public static final String SUPER_CLASS = "superClass";

   // =============== Fields ===============

   private ClassModel classModel;
   private ModelEventManager mem;

   // =============== Constructors ===============

   /**
    * ClassModelManager is used to manage fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}. Managed class models may
    * be merged from multiple inputs<br>
    */
   public ClassModelManager()
   {
      mem = new ModelEventManager();
      mem.setModelManager(this);

      this.classModel = new ClassModel()
            .setDefaultPropertyStyle(POJO)
            .setDefaultRoleType(COLLECTION_ARRAY_LIST);
   }

   /**
    * ClassModelManager is used to manage fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}. Managed class models may
    * be merged from multiple inputs<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
        ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

        ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", mb.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    */
   public ClassModelManager(ModelEventManager classModelEventManager)
   {
      mem = classModelEventManager;

      this.classModel = new ClassModel()
            .setDefaultPropertyStyle(POJO)
            .setDefaultRoleType(COLLECTION_ARRAY_LIST);
   }

   // =============== Properties ===============

   public ClassModel getClassModel()
   {
      return classModel;
   }

   // =============== Methods ===============

   // --------------- Settings ---------------

   public ClassModelManager havePackageName(String packagename)
   {
      String oldPackageName = classModel.getPackageName();

      if (StrUtil.stringEquals(oldPackageName, packagename)) return this;

      classModel.setPackageName(packagename);

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_PACKAGE_NAME);
         e.put(EVENT_KEY, Yamler.encapsulate(THE_CLASS_MODEL + "_" + PROPERTY_packageName));
         e.put(PROPERTY_packageName, Yamler.encapsulate(packagename));
      });

      return this;
   }

   public ClassModelManager haveMainJavaDir(String sourceFolder)
   {
      String mainJavaDir = classModel.getMainJavaDir();

      if (StrUtil.stringEquals(mainJavaDir, sourceFolder)) return this;

      classModel.setMainJavaDir(sourceFolder);

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_MAIN_JAVA_DIR);
         e.put(EVENT_KEY, Yamler.encapsulate(THE_CLASS_MODEL + "_" + PROPERTY_mainJavaDir));
         e.put(PROPERTY_mainJavaDir, Yamler.encapsulate(sourceFolder));
      });

      return this;
   }

   // --------------- Classes ---------------

   public Clazz haveClass(String className)
   {
      Clazz clazz = this.classModel.getClazz(className);

      if (clazz != null)  return clazz; //============================

      clazz = new ClassBuilder(this.classModel, className).getClazz();

      this.event(e -> {
         e.put(EventSource.EVENT_TYPE, HAVE_CLASS);
         e.put(EventSource.EVENT_KEY, Yamler.encapsulate(className));
         e.put(Clazz.PROPERTY_name, Yamler.encapsulate(className));
      });

      return clazz;
   }

   public Clazz haveSuper(Clazz subClass, Clazz superClass)
   {
      subClass.setSuperClass(superClass);

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_SUPER);
         e.put(EVENT_KEY, Yamler.encapsulate(subClass.getName()));
         e.put(SUB_CLASS, Yamler.encapsulate(subClass.getName()));
         e.put(SUPER_CLASS, Yamler.encapsulate(superClass.getName()));
      });

      return subClass;
   }

   // --------------- Attributes ---------------

   public Attribute haveAttribute(Clazz clazz, String attrName, String attrType)
   {
      Attribute attr = clazz.getAttribute(attrName);

      if (attr != null && attr.getType().equals(attrType)) return attr; //==============================

      if (attr == null)
      {
         ClassModelBuilder.checkValidJavaId(attrName);
         if (clazz.getAttribute(attrName) != null
               || clazz.getRole(attrName) != null)
            throw new IllegalArgumentException("duplicate attribute / role name: " + attrName);

         attr = new Attribute();
         attr.setName(attrName);
         attr.setClazz(clazz);
         attr.setPropertyStyle(clazz.getPropertyStyle());
      }

      attr.setType(attrType);

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_ATTRIBUTE);
         e.put(EVENT_KEY, Yamler.encapsulate(clazz.getName() + "." + attrName));
         e.put(CLASS_NAME, Yamler.encapsulate(clazz.getName()));
         e.put(ATTR_NAME, Yamler.encapsulate(attrName));
         e.put(ATTR_TYPE, Yamler.encapsulate(attrType));
      });

      return attr;
   }

   // --------------- Associations ---------------

   /**
    * @deprecated use {@link #haveRole(Clazz, String, int, Clazz)}, which provides better parameter symmetry.
    */
   @Deprecated
   public AssocRole haveRole(Clazz srcClass, String attrName, Clazz tgtClass, int size)
   {
      return this.haveRole(srcClass, attrName, size, tgtClass);
   }

   public AssocRole haveRole(Clazz srcClass, String attrName, int size, Clazz tgtClass)
   {
      String otherRoleName = StrUtil.downFirstChar(srcClass.getName());
      return this.haveRole(srcClass, attrName, size, tgtClass, otherRoleName, ClassModelBuilder.ONE, false);
   }

   /**
    * @deprecated use {@link #haveRole(Clazz, String, int, Clazz, String, int)}, which provides better parameter symmetry.
    */
   @Deprecated
   public AssocRole haveRole(Clazz srcClass, String srcRole, Clazz tgtClass, int srcSize, String tgtRole, int tgtSize)
   {
      return this.haveRole(srcClass, srcRole, srcSize, tgtClass, tgtRole, tgtSize, true);
   }

   public AssocRole haveRole(Clazz srcClass, String srcRole, int srcSize, Clazz tgtClass, String tgtRole, int tgtSize)
   {
      return this.haveRole(srcClass, srcRole, srcSize, tgtClass, tgtRole, tgtSize, true);
   }

   private AssocRole haveRole(Clazz srcClass, String srcRole, int srcSize, Clazz tgtClass, String tgtRole, int tgtSize,
       boolean bothRoles)
   {
      AssocRole role = srcClass.getRole(srcRole);

      if (role != null
            && role.getCardinality() >= srcSize
            && role.getOther().getClazz() == tgtClass
            && ( ! bothRoles || role.getOther().getName().equals(tgtRole))
            && ( ! bothRoles || role.getOther().getCardinality() >= tgtSize)
      )
         return role; //===============================================================

      if (StrUtil.stringEquals(srcRole, tgtRole)) tgtSize = srcSize;

      if (role == null)
      {
         role = new AssocRole()
               .setClazz(srcClass)
               .setName(srcRole)
               .setCardinality(srcSize)
               .setPropertyStyle(srcClass.getPropertyStyle())
               .setRoleType(srcClass.getModel().getDefaultRoleType());

         AssocRole otherRole = new AssocRole()
               .setClazz(tgtClass)
               .setName(tgtRole)
               .setCardinality(tgtSize)
               .setPropertyStyle(tgtClass.getPropertyStyle())
               .setRoleType(tgtClass.getModel().getDefaultRoleType());

         role.setOther(otherRole);
      }

      int maxSize = Math.max(role.getCardinality(), srcSize);
      role.setCardinality(maxSize);

      int maxTgtSize = Math.max(role.getOther().getCardinality(), tgtSize);
      if (bothRoles) role.getOther().setName(tgtRole);
      role.getOther().setCardinality(tgtSize);

      // mm.haveRole(currentRegisterClazz, srcRole, tgtClass, srcSize, tgtRole, ClassModelBuilder.ONE);
      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_ROLE);
         e.put(EVENT_KEY, Yamler.encapsulate(srcClass.getName() + "." + srcRole));
         e.put(SRC_CLASS_NAME, Yamler.encapsulate(srcClass.getName()));
         e.put(SRC_ROLE, Yamler.encapsulate(srcRole));
         e.put(TGT_CLASS_NAME, Yamler.encapsulate(tgtClass.getName()));
         e.put(SRC_SIZE, "" + maxSize);
         e.put(TGT_ROLE, Yamler.encapsulate(tgtRole));
         e.put(TGT_SIZE, "" + maxTgtSize);
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
         if (fMethod.getDeclaration().equals(declaration)) {
            method = fMethod;
               break;
         }
      }

      if (method != null) {
         if (body == null || body.equals(method.getMethodBody())) {
            return method;
         }
      }

      if (method == null) {
         method = new FMethod();
      }

      // need a final variable due to use in lambda expression below.
      final FMethod foundMethod = method;

      foundMethod.setClazz(clazz)
            .setDeclaration(declaration)
            .setMethodBody(body);

      String key = clazz.getName() + "." + foundMethod.getDeclaration();

      this.event(e -> {
         e.put(EVENT_TYPE, HAVE_METHOD);
         e.put(EVENT_KEY, Yamler.encapsulate(key));
         e.put(CLASS_NAME, Yamler.encapsulate(clazz.getName()));
         e.put(DECLARATION, Yamler.encapsulate(foundMethod.getDeclaration()));
         e.put(METHOD_BODY, Yamler.encapsulate(foundMethod.getMethodBody()));
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
      final LinkedHashMap<String, String> map = new LinkedHashMap<>();
      populator.accept(map);
      this.mem.append(map);
   }

   @Override
   public void initConsumers(LinkedHashMap<String, Consumer<LinkedHashMap<String, String>>> consumerMap)
   {
      consumerMap.put(HAVE_PACKAGE_NAME, map -> {
         String packageName = map.get(PROPERTY_packageName);
         this.havePackageName(packageName);
      });

      consumerMap.put(HAVE_MAIN_JAVA_DIR, map -> {
         String sourceFolder = map.get(PROPERTY_mainJavaDir);
         this.haveMainJavaDir(sourceFolder);
      });

      consumerMap.put(HAVE_CLASS, map -> {
         String name = map.get(PROPERTY_name);
         this.haveClass(name);
      });

      consumerMap.put(HAVE_ATTRIBUTE, map -> {
         String className = map.get(CLASS_NAME);
         String attrName = map.get(ATTR_NAME);
         String attrType = map.get(ATTR_TYPE);

         Clazz clazz = this.haveClass(className);
         this.haveAttribute(clazz, attrName, attrType);
      });

      // haveRole(Clazz srcClass, String attrName, Clazz tgtClass, int size)
      consumerMap.put(HAVE_ROLE, map -> {
         String srcClassName = map.get(SRC_CLASS_NAME);
         String attrName = map.get(ATTR_NAME);
         String tgtClassName = map.get(TGT_CLASS_NAME);
         String sizeName = map.get(TGT_CARDINALITY);

         Clazz srcClazz = this.haveClass(srcClassName);
         Clazz tgClazz = this.haveClass(tgtClassName);
         int size = Integer.parseInt(sizeName);

         this.haveRole(srcClazz, attrName, tgClazz, size);
      });
   }
}
