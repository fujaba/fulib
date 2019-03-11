package org.fulib.builder;

import org.fulib.Fulib;
import org.fulib.StrUtil;
import org.fulib.classmodel.ClassModel;
import org.fulib.yaml.EventSource;
import org.fulib.yaml.Yamler;

import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.LinkedHashMap;

import static org.fulib.builder.ClassModelBuilder.*;

/**
 * ClassModelbuilder is used to create fulib class models that are input for
 * fulib code generation {@link Fulib#generator()}.<br>
 * Typical usage:
 * <pre>
 * <!-- insert_code_fragment: ClassModelBuilder -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
      * <!-- end_code_fragment:  -->
 * </pre>
 *
 */
public class ClassModelManager
{
   public static final String THE_CLASS_MODEL = "theClassModel";
   public static final String HAVE_PACKAGE_NAME = "havePackageName";
   public static final String HAVE_MAIN_JAVA_DIR = "haveMainJavaDir";

   private ClassModel classModel;
   private ClassModelEventManager mem;

   /**
    * ClassModelManager is used to manage fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}. Managed class models may
    * be merged from multiple inputs<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    */
   public ClassModelManager(ClassModelEventManager classModelEventManager)
   {
      mem = classModelEventManager;

      this.classModel = new ClassModel()
            .setDefaultPropertyStyle(POJO)
            .setDefaultRoleType(COLLECTION_ARRAY_LIST);
   }


   public ClassModelManager havePackageName(String packagename)
   {
      String oldPackageName = classModel.getPackageName();

      if (StrUtil.stringEquals(oldPackageName, packagename)) return this;

      classModel.setPackageName(packagename);

      LinkedHashMap<String, String> event = new LinkedHashMap<>();
      event.put(EventSource.EVENT_TYPE, HAVE_PACKAGE_NAME);
      event.put(EventSource.EVENT_KEY, Yamler.encapsulate(THE_CLASS_MODEL + "_" + ClassModel.PROPERTY_packageName));
      event.put(ClassModel.PROPERTY_packageName, Yamler.encapsulate(packagename));
      mem.append(event);

      return this;
   }



   public ClassModelManager haveMainJavaDir(String sourceFolder)
   {
      String mainJavaDir = classModel.getMainJavaDir();

      if (StrUtil.stringEquals(mainJavaDir, sourceFolder)) return this;

      classModel.setMainJavaDir(sourceFolder);

      LinkedHashMap<String, String> event = new LinkedHashMap<>();
      event.put(EventSource.EVENT_TYPE, HAVE_MAIN_JAVA_DIR);
      event.put(EventSource.EVENT_KEY, Yamler.encapsulate(THE_CLASS_MODEL + "_" + ClassModel.PROPERTY_mainJavaDir));
      event.put(ClassModel.PROPERTY_mainJavaDir, Yamler.encapsulate(sourceFolder));
      mem.append(event);

      return this;
   }



   /**
    * ClassModelbuilder is used to create fulib class models that are input for
    * fulib code generation {@link Fulib#generator()}.<br>
    * Typical usage:
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    * @param packagename
    * @param sourceFolder
    */
   public ClassModelManager(String packagename, String sourceFolder)
   {
      checkValidJavaId(packagename);

      ClassModel classModel = new ClassModel()
            .setPackageName(packagename)
            .setMainJavaDir(sourceFolder)
            .setDefaultPropertyStyle(POJO)
            .setDefaultRoleType(COLLECTION_ARRAY_LIST);

      this.setClassModel(classModel);
   }


   static void checkValidJavaId(String myRoleName)
   {
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("" + myRoleName + " is not an valid java identifier");

      if (myRoleName == null) throw illegalArgumentException;

      if (myRoleName.endsWith(".") || myRoleName.startsWith(".")) throw illegalArgumentException;

      if (myRoleName.indexOf('.') >= 0)
      {
         for (String s : myRoleName.split("\\."))
         {
            checkValidJavaId(s);
         }
         return;
      }

      if ( ! myRoleName.matches("[a-zA-Z_]\\w*")) throw illegalArgumentException;

      String javaKeyWords = " abstract assert boolean break " +
            "byte case catch char " +
            "class const continue default " +
            "do double else enum " +
            "extends final finally float " +
            "for goto if implements " +
            "import instanceof int interface " +
            "long native new package " +
            "private protected public return " +
            "short static strictfp super " +
            "switch synchronized this throw " +
            "throws transient try void " +
            "volatile while  true  false " +
            "null ";

      if (javaKeyWords.indexOf(" " + myRoleName + " ") >= 0 ) throw illegalArgumentException;

      // hm, myRoleName seems valid
   }


   private void setClassModel(ClassModel classModel)
   {
      this.classModel = classModel;
   }


   /**
    * @return the class model this builder is responsible for 
    */
   public ClassModel getClassModel()
   {
      return classModel;
   }

   /**
    * set container class to be used for to-many associations,
    * default is ClassModelBuilder.COLLECTION_ARRAY_LIST
    * alternative is e.g.: ClassModelBuilder.
    * @param collectionClass
    * @return
    */
   public ClassModelManager setDefaultCollectionClass(Class collectionClass)
   {
      if ( ! Collection.class.isAssignableFrom(collectionClass))
      {
         throw new IllegalArgumentException("class is no collection");
      }

      String defaultRoleType = collectionClass.getName();
      TypeVariable[] typeParameters = collectionClass.getTypeParameters();
      if (typeParameters.length == 1)
      {
         defaultRoleType += "<%s>";
      }
      this.classModel.setDefaultRoleType(defaultRoleType);
      return this;
   }


   public ClassModelManager setJavaFXPropertyStyle()
   {
      classModel.setDefaultPropertyStyle(JAVA_FX);
      return this;
   }


   /**
    * Builds and returns a class builder for the given classname and connects it to the model
    * <pre>
    * <!-- insert_code_fragment: ClassModelBuilder.twoParams -->
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, "src/main/java")
            .setJavaFXPropertyStyle();

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
    * <!-- end_code_fragment:  -->
    * </pre>
    * @param className
    * @return new class builder
    */
   public ClassBuilder buildClass(String className)
   {
      ClassBuilder classBuilder = new ClassBuilder(this.classModel, className);
      return classBuilder;
   }


}
