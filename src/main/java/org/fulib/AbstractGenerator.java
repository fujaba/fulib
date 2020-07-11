package org.fulib;

import org.fulib.classmodel.*;
import org.fulib.parser.FragmentMapBuilder;
import org.fulib.util.AbstractGenerator4ClassFile;
import org.fulib.yaml.YamlIdMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @since 1.2
 */
public abstract class AbstractGenerator
{
   // =============== Static Fields ===============

   private static Logger logger;

   static
   {
      logger = Logger.getLogger(Generator.class.getName());
      logger.setLevel(Level.SEVERE);
   }

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
    Fulib.generator().setCustomTemplatesFile("templates/custom.stg").generate(model);
    * <!-- end_code_fragment: testCustomTemplates -->
    * </pre>
    *
    * @param customFileName
    *    the custom templates file name
    *
    * @return this instance, to allow call chaining
    */
   public AbstractGenerator setCustomTemplatesFile(String customFileName)
   {
      this.customTemplateFile = customFileName;
      return this;
   }

   protected abstract String getModelFileName();

   // =============== Methods ===============

   /**
    * The fulib Generator generates Java code from a class model
    * <pre>
    * <!-- insert_code_fragment: Fulib.createGenerator-->
    * ClassModel model = mb.getClassModel();
    * Fulib.generator().generate(model);
    * <!-- end_code_fragment:  -->
    * </pre>
    *
    * @param model
    *    providing classes to generate Java implementations for
    */
   public void generate(ClassModel model)
   {
      final String modelFileName = this.getModelFileName();
      final ClassModel oldModel = this.loadClassModel(model.getPackageSrcFolder(), modelFileName);

      final Map<String, FileFragmentMap> files = new HashMap<>();

      final AbstractGenerator4ClassFile generator = this.createGenerator4ClassFile();
      generator.setCustomTemplatesFile(this.getCustomTemplateFile());

      if (oldModel != null)
      {
         this.markModifiedElementsInOldModel(oldModel, model);

         // remove code of modified elements
         this.generateClasses(oldModel, files, generator);

         this.deleteRemovedClassFiles(oldModel, files);
      }

      this.generateClasses(model, files, generator);
      this.generateExtraClasses(model, generator);

      for (final FileFragmentMap fragmentMap : files.values())
      {
         fragmentMap.compressBlankLines();
         fragmentMap.writeFile();
      }

      this.saveNewClassModel(model, modelFileName);
   }

   protected void generateExtraClasses(ClassModel model, AbstractGenerator4ClassFile generator)
   {
   }

   protected abstract AbstractGenerator4ClassFile createGenerator4ClassFile();

   private void deleteRemovedClassFiles(ClassModel oldModel, Map<String, FileFragmentMap> files)
   {
      for (final Clazz clazz : oldModel.getClasses())
      {
         if (clazz.getModified() && files.get(clazz.getName()).isClassBodyEmpty())
         {
            files.remove(clazz.getName());

            final String classFileName = clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".java";
            final Path path = Paths.get(classFileName);
            try
            {
               Files.deleteIfExists(path);
               Logger.getLogger(Generator.class.getName()).info("\n   deleting empty file " + classFileName);
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   private void generateClasses(ClassModel model, Map<String, FileFragmentMap> files,
      AbstractGenerator4ClassFile generator)
   {
      for (Clazz clazz : model.getClasses())
      {
         final FileFragmentMap fragmentMap = files.computeIfAbsent(clazz.getName(), s -> {
            final String sourceFileName = generator.getSourceFileName(clazz);
            return Files.exists(Paths.get(sourceFileName)) ?
               FragmentMapBuilder.parse(sourceFileName) :
               new FileFragmentMap(sourceFileName);
         });
         generator.generate(clazz, fragmentMap);
      }
   }

   private ClassModel loadClassModel(String modelFolder, String modelFileName)
   {
      String fileName = modelFolder + '/' + modelFileName;
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

   private void saveNewClassModel(ClassModel model, String modelFileName)
   {
      YamlIdMap idMap = new YamlIdMap(ClassModel.class.getPackage().getName());
      String yamlString = idMap.encode(model);
      try
      {
         String modelFolder = model.getPackageSrcFolder();
         String fileName = modelFolder + '/' + modelFileName;
         Files.createDirectories(Paths.get(modelFolder));
         Files.write(Paths.get(fileName), yamlString.getBytes(), StandardOpenOption.CREATE,
                     StandardOpenOption.TRUNCATE_EXISTING);
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

         this.markModifiedElementsInOldClazz(oldClazz, newClazz);
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
         if (this.isModified(oldAttr, newClazz))
         {
            oldAttr.markAsModified();
            logger.info("\n   markedAsModified: attribute " + oldAttr.getName());
         }
      }

      for (AssocRole oldRole : oldClazz.getRoles())
      {
         if (this.isModified(oldRole, newClazz))
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

      for (FMethod oldMethod : oldClazz.getMethods())
      {
         if (this.isModified(oldMethod, newClazz))
         {
            oldMethod.setModified(true);
            logger.info("\n   markedAsModified: method " + oldMethod.getDeclaration());
         }
      }
   }

   private boolean isModified(Attribute oldAttr, Clazz newClazz)
   {
      if (newClazz == null)
      {
         return true;
      }

      final Attribute newAttr = newClazz.getAttribute(oldAttr.getName());
      return newAttr == null || !Objects.equals(oldAttr.getType(), newAttr.getType()) //
             || !Objects.equals(oldAttr.getPropertyStyle(), newAttr.getPropertyStyle()) //
             || !collectionTypeMatches(oldAttr.getCollectionType(), newAttr.getCollectionType());
   }

   private static boolean collectionTypeMatches(CollectionType a, CollectionType b)
   {
      return a == b //
             || a != null && b != null && a.getItf() == b.getItf() && a.getImplTemplate().equals(b.getImplTemplate());
   }

   private boolean isModified(AssocRole oldRole, Clazz newClazz)
   {
      if (newClazz == null)
      {
         return true;
      }

      final AssocRole newRole = newClazz.getRole(oldRole.getName());

      return newRole == null || oldRole.getCardinality() != newRole.getCardinality() //
             || !Objects.equals(oldRole.getPropertyStyle(), oldRole.getPropertyStyle()) //
             || !collectionTypeMatches(oldRole.getCollectionType(), newRole.getCollectionType());
   }

   private boolean isModified(FMethod oldMethod, Clazz newClazz)
   {
      if (newClazz == null)
      {
         return true;
      }

      for (FMethod newMethod : newClazz.getMethods())
      {
         if (oldMethod.signatureMatches(newMethod))
         {
            return false;
         }
      }
      return true;
   }
}
