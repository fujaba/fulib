package org.fulib;

import org.fulib.util.AbstractGenerator4ClassFile;
import org.fulib.util.Generator4TypeScriptClassFile;

/**
 * The fulib TypeScriptGenerator generates Table classes from a class model.
 * Table classes are used for relational model queries.
 * <pre>
 * <!-- insert_code_fragment: Fulib.tablesGenerator-->
 * ClassModel model = mb.getClassModel();
 * Fulib.tablesGenerator().generate(model);
 * <!-- end_code_fragment:  -->
 * </pre>
 *
 * @deprecated since 1.2; TypeScript support is no longer maintained by the Java implementation of Fulib
 */
@Deprecated
public class TypeScriptGenerator extends AbstractGenerator
{
   // =============== Constants ===============

   private static final String MODEL_FILE_NAME = "typeScriptClassModel.yaml";

   // =============== Properties ===============

   @Override
   public TypeScriptGenerator setCustomTemplatesFile(String customFileName)
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
      return new Generator4TypeScriptClassFile();
   }
}
