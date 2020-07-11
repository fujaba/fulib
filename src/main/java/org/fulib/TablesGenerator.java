package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.util.AbstractGenerator4ClassFile;
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
public class TablesGenerator extends AbstractGenerator
{
   // =============== Constants ===============

   private static final String MODEL_FILE_NAME = "tablesClassModel.yaml";

   // =============== Properties ===============

   /**
    * @since 1.2
    */
   @Override
   public TablesGenerator setCustomTemplatesFile(String customFileName)
   {
      super.setCustomTemplatesFile(customFileName);
      return this;
   }

   @Override
   protected String getModelFileName()
   {
      return MODEL_FILE_NAME;
   }

   @Override
   protected AbstractGenerator4ClassFile createGenerator4ClassFile()
   {
      return new Generator4TableClassFile();
   }

   // =============== Methods ===============

   @Override
   protected void generateExtraClasses(ClassModel model, AbstractGenerator4ClassFile generator)
   {
      this.generatePrimitiveTable(model, generator, "int", "Integer");
      this.generatePrimitiveTable(model, generator, "long", "Long");
      this.generatePrimitiveTable(model, generator, "double", "Double");
      this.generatePrimitiveTable(model, generator, "float", "Float");

      STGroup group = generator.getSTGroup("org/fulib/templates/tables/StringTable.stg");
      ST st = group.getInstanceOf("StringTable");
      st.add("packageName", model.getPackageName() + ".tables");
      this.writeToFile(model.getPackageSrcFolder() + "/tables/StringTable.java", st);
   }

   private void generatePrimitiveTable(ClassModel model, AbstractGenerator4ClassFile generator, String primitiveType,
      String objectType)
   {
      STGroup group = generator.getSTGroup("org/fulib/templates/tables/intTable.stg");
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
}
