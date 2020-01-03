package org.fulib.generator;

import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;

public class JavaFXAttributeTest extends BeanAttributeTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/javafx/attributes";
   }

   @Override
   protected void configureModel(ClassModelBuilder mb)
   {
      mb.setDefaultPropertyStyle(Type.JAVA_FX);
   }
}
