package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;

public class JavaFXAttributeTest extends BeanAttributeTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/javafx/attributes";
   }

   @Override
   protected ClassModel getClassModel(String srcFolder, String packageName)
   {
      return this.getClassModel(Fulib.classModelBuilder(packageName, srcFolder).setDefaultPropertyStyle(Type.JAVA_FX));
   }
}
