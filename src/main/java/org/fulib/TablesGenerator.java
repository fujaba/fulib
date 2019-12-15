package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.util.Generator4TableClassFile;
import org.fulib.yaml.YamlIdMap;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The fulib TablesGenerator generates Table classes from a class model.
 * Table classes are used for relational model queries.
 * <pre>
 * <!-- insert_code_fragment: Fulib.tablesGenerator-->
      ClassModel model = mb.getClassModel();
      Fulib.tablesGenerator().generate(model);
 * <!-- end_code_fragment:  -->
 * </pre>
 */
public class TablesGenerator
{

   private static Logger logger;

   static {
      logger = Logger.getLogger(TablesGenerator.class.getName());
      logger.setLevel(Level.SEVERE);
   }

   private String customTemplateFile = null;

   /**
    * The fulib TablesGenerator generates Table classes from a class model.
    * Table classes are used for relational model queries.
    * <pre>
    * <!-- insert_code_fragment: Fulib.tablesGenerator-->
    ClassModel model = mb.getClassModel();
    Fulib.tablesGenerator().generate(model);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param model
    *    the class model
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
         new Generator4TableClassFile()
               .setCustomTemplatesFile(this.getCustomTemplateFile())
               .generate(clazz);
      }

      // generate primitive tables
      Generator4TableClassFile generator4TableClassFile = new Generator4TableClassFile()
            .setCustomTemplatesFile(this.getCustomTemplateFile());

      generatePrimitivTable(model, generator4TableClassFile, "int", "Integer");
      generatePrimitivTable(model, generator4TableClassFile, "long", "Long");
      generatePrimitivTable(model, generator4TableClassFile, "double", "Double");
      generatePrimitivTable(model, generator4TableClassFile, "float", "Float");

      STGroup group = generator4TableClassFile.getSTGroup("org/fulib/templates/tables/StringTable.stg");
      ST st = group.getInstanceOf("StringTable");
      st.add("packageName", model.getPackageName() + ".tables");
      String result = st.render();
      writeFile(model.getPackageSrcFolder() + "/tables/StringTable.java", result);
   }

   private void generatePrimitivTable(ClassModel model, Generator4TableClassFile generator4TableClassFile, String primitivType, String objectType)
   {
      STGroup group = generator4TableClassFile.getSTGroup("org/fulib/templates/tables/intTable.stg");
      ST st = group.getInstanceOf("intTable");
      st.add("packageName", model.getPackageName() + ".tables");
      st.add("primitiveType", primitivType);
      st.add("objectType", objectType);
      String result = st.render();
      writeFile(model.getPackageSrcFolder() + "/tables/" + primitivType + "Table.java", result);
   }

   public void writeFile(String fileName, String content)
   {
      try
      {
         Path path = Paths.get(fileName);
         Files.createDirectories(path.getParent());
         Files.write(path, content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   private ClassModel loadOldClassModel(String modelFolder)
   {
      // store new model
      String fileName = modelFolder + "/tablesClassModel.yaml";
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
    * <pre>
    * <!-- insert_code_fragment: testCustomTemplates -->
        Fulib.generator()
                .setCustomTemplatesFile("templates/custom.stg")
                .generate(model);
    * <!-- end_code_fragment: testCustomTemplates -->
    * </pre>
    *
    * @param customFileName
    *    the custom templates file name
    *
    * @return this instance, to allow call chaining.
    */
   public TablesGenerator setCustomTemplatesFile(String customFileName)
   {
      this.customTemplateFile = customFileName;
      return this;
   }
}
