package org.fulib.generator;

import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;

public class JavaFXMultiAttributeTest extends BeanMultiAttributeTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/javafx/multi-attributes";
   }

   @Override
   protected void configureModel(ClassModelBuilder mb)
   {
      mb.setDefaultPropertyStyle(Type.JAVA_FX);
   }
}
