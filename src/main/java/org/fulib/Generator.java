package org.fulib;

import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
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

   private static Logger logger;

   static {
      logger = Logger.getLogger(Generator.class.getName());
      logger.setLevel(Level.SEVERE);
   }

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
         Clazz newClazz = newModel.getClazz(oldClazz.getName());

         markModifiedElementsInOldClazz(oldClazz, newClazz);
      }
   }

   private void markModifiedElementsInOldClazz(Clazz oldClazz, Clazz newClazz)
   {
      logger = Logger.getLogger(Generator.class.getName());
      if (newClazz == null)
      {
         oldClazz.markAsModified();
         logger.info("\n   markedAsModified: class " + oldClazz.getName());
      }

      for (Attribute oldAttr : oldClazz.getAttributes())
      {
         boolean modified = newClazz == null;

         if ( ! modified)
         {
            Attribute newAttr = newClazz.getAttribute(oldAttr.getName());

            modified = newAttr == null
                  || ! StrUtil.stringEquals(oldAttr.getType(), newAttr.getType());
         }

         if (modified)
         {
            oldAttr.markAsModified();
            logger.info("\n   markedAsModified: attribute " + oldAttr.getName());
         }
      }

      for (AssocRole oldRole : oldClazz.getRoles())
      {
         boolean modified = newClazz == null;

         if ( ! modified)
         {
            AssocRole newRole = newClazz.getRole(oldRole.getName());

            modified = newRole == null
                  || oldRole.getCardinality() != newRole.getCardinality();
         }

         if (modified)
         {
            oldRole.markAsModified();
            logger.info("\n   markedAsModified: role " + oldRole.getName());
            if (oldRole.getOther() != null)
            {
               oldRole.getOther().markAsModified();
               logger.info("\n   markedAsModified: role " + oldRole.getOther().getName());
            }
         }
      }
   }
}