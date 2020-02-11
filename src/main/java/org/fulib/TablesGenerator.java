package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.util.Generator4TableClassFile;
import org.stringtemplate.v4.AutoIndentWriter;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The fulib TablesGenerator generates Table classes from a class model.
 * Table classes are used for relational model queries.
 * <pre>
 * <!-- insert_code_fragment: Fulib.tablesGenerator-->
 * ClassModel model = mb.getClassModel();
 * Fulib.tablesGenerator().generate(model);
 * <!-- end_code_fragment:  -->
 * </pre>
 */
public class TablesGenerator
{
   // =============== Constants ===============

   private static final String MODEL_FILE_NAME = "tablesClassModel.yaml";

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
    * <pre>
    * <!-- insert_code_fragment: testCustomTemplates -->
    * Fulib.generator()
    * .setCustomTemplatesFile("templates/custom.stg")
    * .generate(model);
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

   // =============== Methods ===============

   /**
    * The fulib TablesGenerator generates Table classes from a class model.
    * Table classes are used for relational model queries.
    * <pre>
    * <!-- insert_code_fragment: Fulib.tablesGenerator-->
    * ClassModel model = mb.getClassModel();
    * Fulib.tablesGenerator().generate(model);
    * <!-- end_code_fragment:  -->
    * </pre>
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
         new Generator4TableClassFile().setCustomTemplatesFile(this.getCustomTemplateFile()).generate(clazz);
      }

      // generate primitive tables
      Generator4TableClassFile generator4TableClassFile = new Generator4TableClassFile()
         .setCustomTemplatesFile(this.getCustomTemplateFile());

      this.generatePrimitiveTable(model, generator4TableClassFile, "int", "Integer");
      this.generatePrimitiveTable(model, generator4TableClassFile, "long", "Long");
      this.generatePrimitiveTable(model, generator4TableClassFile, "double", "Double");
      this.generatePrimitiveTable(model, generator4TableClassFile, "float", "Float");

      STGroup group = generator4TableClassFile.getSTGroup("org/fulib/templates/tables/StringTable.stg");
      ST st = group.getInstanceOf("StringTable");
      st.add("packageName", model.getPackageName() + ".tables");
      this.writeToFile(model.getPackageSrcFolder() + "/tables/StringTable.java", st);
   }

   private void generatePrimitiveTable(ClassModel model, Generator4TableClassFile generator4TableClassFile,
      String primitiveType, String objectType)
   {
      STGroup group = generator4TableClassFile.getSTGroup("org/fulib/templates/tables/intTable.stg");
      ST st = group.getInstanceOf("intTable");
      st.add("packageName", model.getPackageName() + ".tables");
      st.add("primitiveType", primitiveType);
      st.add("objectType", objectType);
      this.writeToFile(model.getPackageSrcFolder() + "/tables/" + primitiveType + "Table.java", st);
   }

   private void writeToFile(String fileName, ST st)
   {
      try
      {
         Path path = Paths.get(fileName);
         Files.createDirectories(path.getParent());

         try (final Writer writer = Files
            .newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
         {
            st.write(new AutoIndentWriter(writer));
         }
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
      }
   }

   /**
    * Writes the content to the file, using the system-default charset and creating parent directories if needed.
    * <p>
    * If an exception occurs, the stack trace is printed to stderr.
    *
    * @param fileName
    *    the file name
    * @param content
    *    the content
    *
    * @deprecated since 1.2; for internal use only
    */
   @Deprecated
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



   public String getCustomTemplateFile()
   {
      return customTemplateFile;
   }

   /**
    * You may overwrite code generation templates within some custom template file. <br>
    * Provide your templates for code generation as in:
    * <pre>
    * <!-- insert_code_fragment: testCustomTemplates -->
      Fulib.generator().setCustomTemplatesFile("templates/custom.stg").generate(model);
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
