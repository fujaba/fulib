package org.fulib;

import org.fulib.util.Generator4ClassFile;

/**
 * The fulib Generator generates Java code from a class model
 * <pre>
 * <!-- insert_code_fragment: Fulib.createGenerator-->
      ClassModel model = mb.getClassModel();
      Fulib.generator().generate(model);
 * <!-- end_code_fragment:  -->
 * </pre>
 */
public class Generator extends AbstractGenerator
{
   // =============== Constants ===============

   private static final String MODEL_FILE_NAME = "classModel.yaml";

   // =============== Properties ===============

   /**
    * @since 1.2
    */
   @Override
   public Generator setCustomTemplatesFile(String customFileName)
   {
      super.setCustomTemplatesFile(customFileName);
      return this;
   }

   // =============== Methods ===============

   @Override
   protected String getModelFileName()
   {
      return MODEL_FILE_NAME;
   }

   @Override
   protected Generator4ClassFile createGenerator4ClassFile()
   {
      return new Generator4ClassFile();
   }
}
