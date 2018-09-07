package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.util.Generator4ClassFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Generator
{
   public static void generate(ClassModel model)
   {
      new Generator()
            .doGenerate(model);
   }

   public void doGenerate(ClassModel model)
   {

      // loop through all classes
      for (Clazz clazz : model.getClasses())
      {
         new Generator4ClassFile().doGenerate(clazz);
      }

      // store new model
      YamlIdMap idMap = new YamlIdMap(ClassModel.class.getPackage().getName());
      String yamlString = idMap.encode(model);
      try
      {
         String modelFolder = "src/main/resources/fulib_models";
         String fileName = modelFolder + "/" + model.getPackageName() + ".fulib";
         Files.createDirectories(Paths.get(modelFolder));
         Files.write(Paths.get(fileName), yamlString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }



}
