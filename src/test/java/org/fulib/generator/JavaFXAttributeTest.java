package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.classmodel.ClassModel;

public class JavaFXAttributeTest extends AttributeTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/javafx/attributes";
   }

   @Override
   protected ClassModel getAttributesModel(String srcFolder, String packageName)
   {
      return this.getAttributesModel(Fulib.classModelBuilder(packageName, srcFolder).setJavaFXPropertyStyle());
   }
}
