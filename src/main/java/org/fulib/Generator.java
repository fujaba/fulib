package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.util.Generator4ClassFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Generator
{
   public static void generate(ClassModel model)
   {
      new Generator()
            .doGenerate(model);
   }

   public void doGenerate(ClassModel model)
   {
      ClassModel oldModel = loadOldClassModel(model.getPackageSrcFolder());

      if (oldModel != null)
      {
         markModifiedElementsInOldModel(oldModel, model);

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
         new Generator4ClassFile().doGenerate(clazz);
      }
   }


   private ClassModel loadOldClassModel(String modelFolder)
   {
      // store new model
      String fileName = modelFolder + "/classModel.yaml";
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
         Logger.getGlobal().log(Level.SEVERE, "could not load " + fileName, e);
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
         String fileName = modelFolder + "/classModel.yaml";
         Files.createDirectories(Paths.get(modelFolder));
         Files.write(Paths.get(fileName), yamlString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }


   private void markModifiedElementsInOldModel(ClassModel oldModel, ClassModel newModel)
   {
      //  check for changed package name or target folder?

      for (Clazz oldClazz : oldModel.getClasses())
      {
         Clazz newClazz = findClazz(newModel, oldClazz.getName());

         markModifiedElementsInOldClazz(oldClazz, newClazz);
      }
   }

   private void markModifiedElementsInOldClazz(Clazz oldClazz, Clazz newClazz)
   {
      if (newClazz == null)
      {

      }
   }

   private Clazz findClazz(ClassModel model, String name)
   {
      for (Clazz clazz : model.getClasses())
      {
         if (StrUtil.stringEquals(clazz.getName(), name))
         {
            return clazz;
         }
      }
      return null;
   }
}
