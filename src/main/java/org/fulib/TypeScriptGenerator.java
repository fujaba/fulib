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
      ClassModel model = mb.getClassModel();
      Fulib.tablesGenerator().generate(model);
 * <!-- end_code_fragment:  -->
 * </pre>
 */
public class TypeScriptGenerator
{

   private static Logger logger;

   static {
      logger = Logger.getLogger(TypeScriptGenerator.class.getName());
      logger.setLevel(Level.SEVERE);
   }

   private String customTemplateFile = null;

   /**
    * The fulib TypeScriptGenerator generates testCompile classes from a class model.
    */
   public void generate(ClassModel model)
   {
      ClassModel oldModel = loadOldClassModel(model.getPackageSrcFolder());

      if (oldModel != null)
      {
         Fulib.generator().markModifiedElementsInOldModel(oldModel, model);

         // remove code of modfiedElements
         generateClasses(oldModel);
      }

      generateClasses(model);

      saveClassmodel(model);

   }


   private void generateClasses(ClassModel model)
   {
      // loop through all classes
      for (Clazz clazz : model.getClasses())
      {
         new Generator4TypeScriptClassFile()
               .setCustomTemplatesFile(this.getCustomTemplateFile())
               .generate(clazz);
      }
   }



   private ClassModel loadOldClassModel(String modelFolder)
   {
      // store new model
      String fileName = modelFolder + "/typeScriptClassModel.yaml";
      try
      {
         Path path = Paths.get(fileName);

         if ( ! Files.exists(path))
         {
            return null;
         }

         byte[] bytes = Files.readAllBytes(path);
         String yamlString = new String(bytes);

         YamlIdMap idMap = new YamlIdMap(ClassModel.class.getPackage().getName());
         ClassModel model = (ClassModel) idMap.decode(yamlString);
         return model;
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
         Files.write(Paths.get(fileName), yamlString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }



   public String getCustomTemplateFile()
   {
      return customTemplateFile;
   }

   /**
    * You may overwrite code generation templates within some custom template file. <br>
    * Provide your templates for code generation as in:
    *
    * @param customFileName
    * @return
    */
   public TypeScriptGenerator setCustomTemplatesFile(String customFileName)
   {
      this.customTemplateFile = customFileName;
      return this;
   }
}
