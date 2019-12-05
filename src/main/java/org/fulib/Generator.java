package org.fulib;

import org.fulib.classmodel.*;
import org.fulib.util.Generator4ClassFile;
import org.fulib.yaml.YamlIdMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The fulib Generator generates Java code from a class model
 * <pre>
 * <!-- insert_code_fragment: Fulib.createGenerator-->
      ClassModel model = mb.getClassModel();
      Fulib.generator().generate(model);
 * <!-- end_code_fragment:  -->
 * </pre>
 */
public class Generator
{

   private static Logger logger;

   static {
      logger = Logger.getLogger(Generator.class.getName());
      logger.setLevel(Level.SEVERE);
   }

   private String customTemplateFile = null;

   /**
    * The fulib Generator generates Java code from a class model
    * <pre>
    * <!-- insert_code_fragment: Fulib.createGenerator-->
      ClassModel model = mb.getClassModel();
      Fulib.generator().generate(model);
    * <!-- end_code_fragment:  -->
    * </pre>
    * @param model providing classes to generate Java implementations for
    */
   public void generate(ClassModel model)
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
         new Generator4ClassFile()
               .setCustomTemplatesFile(this.getCustomTemplateFile())
               .generate(clazz);
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


   public void markModifiedElementsInOldModel(ClassModel oldModel, ClassModel newModel)
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
      if (newClazz == null) {
         oldClazz.markAsModified();
         logger.info("\n   markedAsModified: class " + oldClazz.getName());
      }

      for (Attribute oldAttr : oldClazz.getAttributes()) {
         boolean modified = newClazz == null;

         if ( ! modified) {
            Attribute newAttr = newClazz.getAttribute(oldAttr.getName());

            modified = newAttr == null
                  || ! StrUtil.stringEquals(oldAttr.getType(), newAttr.getType())
                  || ! StrUtil.stringEquals(oldAttr.getPropertyStyle(), newAttr.getPropertyStyle());
         }

         if (modified) {
            oldAttr.markAsModified();
            logger.info("\n   markedAsModified: attribute " + oldAttr.getName());
         }
      }

      for (AssocRole oldRole : oldClazz.getRoles()) {
         boolean modified = newClazz == null;

         if ( ! modified) {
            AssocRole newRole = newClazz.getRole(oldRole.getName());

            modified = newRole == null
                  || oldRole.getCardinality() != newRole.getCardinality()
                  || ! StrUtil.stringEquals(oldRole.getPropertyStyle(), oldRole.getPropertyStyle());
         }

         if (modified) {
            oldRole.markAsModified();
            logger.info("\n   markedAsModified: role " + oldRole.getName());
            if (oldRole.getOther() != null)
            {
               oldRole.getOther().markAsModified();
               logger.info("\n   markedAsModified: role " + oldRole.getOther().getName());
            }
         }
      }

      for (FMethod oldMethod : oldClazz.getMethods())
      {
         boolean modified = newClazz == null;

         String oldDeclaration = oldMethod.getDeclaration();

         if ( ! modified) {
            for (FMethod newMethod : newClazz.getMethods()) {
               if (newMethod.getDeclaration().equals(oldDeclaration)) {
                  modified = false;
                  break;
               }
            }
            modified = true;
         }

         if (modified) {
            oldMethod.setModified(true);
            logger.info("\n   markedAsModified: method " + oldMethod.getDeclaration());
         }
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
    * @return this instance, to allow call chaining
    */
   public Generator setCustomTemplatesFile(String customFileName)
   {
      this.customTemplateFile = customFileName;
      return this;
   }
}
