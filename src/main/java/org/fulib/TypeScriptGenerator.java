package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.util.Generator4TypeScriptClassFile;
import org.fulib.yaml.YamlIdMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

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
   // =============== Fields ===============

   private String customTemplateFile = null;

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
      ClassModel oldModel = this.loadOldClassModel(model.getPackageSrcFolder());

      if (oldModel != null)
      {
         Fulib.generator().markModifiedElementsInOldModel(oldModel, model);

         // remove code of modfiedElements
         this.generateClasses(oldModel);
      }

      this.generateClasses(model);

      this.saveClassmodel(model);
   }

   private void generateClasses(ClassModel model)
   {
      // loop through all classes
      for (Clazz clazz : model.getClasses())
      {
         new Generator4TypeScriptClassFile().setCustomTemplatesFile(this.getCustomTemplateFile()).generate(clazz);
      }
   }

   private ClassModel loadOldClassModel(String modelFolder)
   {
      // store new model
      String fileName = modelFolder + "/typeScriptClassModel.yaml";
      try
      {
         Path path = Paths.get(fileName);

         if (!Files.exists(path))
         {
            return null;
         }

         byte[] bytes = Files.readAllBytes(path);
         String yamlString = new String(bytes);

         YamlIdMap idMap = new YamlIdMap(ClassModel.class.getPackage().getName());
         return (ClassModel) idMap.decode(yamlString);
      }
      catch (IOException e)
      {
         Logger.getGlobal().log(Level.SEVERE, "\n   could not load " + fileName, e);
      }

      return null;
   }

   private void saveClassmodel(ClassModel model)
   {
      // store new model
      YamlIdMap idMap = new YamlIdMap(ClassModel.class.getPackage().getName());
      String yamlString = idMap.encode(model);
      try
      {
         String modelFolder = model.getPackageSrcFolder();
         String fileName = modelFolder + "/tablesClassModel.yaml";
         Files.createDirectories(Paths.get(modelFolder));
         Files.write(Paths.get(fileName), yamlString.getBytes(), StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
