package org.fulib;

public class AbstractGenerator
{
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

}
