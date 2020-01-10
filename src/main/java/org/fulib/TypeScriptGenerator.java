package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.util.Generator4TypeScriptClassFile;

/**
 * The fulib TypeScriptGenerator generates Table classes from a class model.
 * Table classes are used for relational model queries.
 * <pre>
 * <!-- insert_code_fragment: Fulib.tablesGenerator-->
 * ClassModel model = mb.getClassModel();
 * Fulib.tablesGenerator().generate(model);
 * <!-- end_code_fragment:  -->
 * </pre>
 *
 * @deprecated since 1.2; TypeScript support is no longer maintained by the Java implementation of Fulib
 */
@Deprecated
public class TypeScriptGenerator
{
   // =============== Constants ===============

   private static final String MODEL_FILE_NAME = "typeScriptClassModel.yaml";

   // =============== Fields ===============

   private String customTemplateFile;

   // =============== Properties ===============

   public String getCustomTemplateFile()
   {
      return this.customTemplateFile;
   }

   /**
    * You may overwrite code generation templates within some custom template file. <br>
    * Provide your templates for code generation as in:
    *
    * @param customFileName
    *    the custom templates file name
    *
    * @return this instance, to allow call chaining
    */
   public TypeScriptGenerator setCustomTemplatesFile(String customFileName)
   {
      this.customTemplateFile = customFileName;
      return this;
   }

   // =============== Methods ===============

   /**
    * The fulib TypeScriptGenerator generates testCompile classes from a class model.
    *
    * @param model
    *    the class model
    */
   public void generate(ClassModel model)
   {
      ClassModel oldModel = Generator.loadClassModel(model.getPackageSrcFolder(), MODEL_FILE_NAME);

      if (oldModel != null)
      {
         Fulib.generator().markModifiedElementsInOldModel(oldModel, model);

         // remove code of modfiedElements
         this.generateClasses(oldModel);
      }

      this.generateClasses(model);

      Generator.saveNewClassModel(model, MODEL_FILE_NAME);
   }

   private void generateClasses(ClassModel model)
   {
      // loop through all classes
      for (Clazz clazz : model.getClasses())
      {
         new Generator4TypeScriptClassFile().setCustomTemplatesFile(this.getCustomTemplateFile()).generate(clazz);
      }
   }
}
