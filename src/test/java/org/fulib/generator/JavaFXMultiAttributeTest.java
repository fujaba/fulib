package org.fulib.generator;

import org.fulib.builder.ClassModelBuilder;

public class JavaFXMultiAttributeTest extends MultiAttributeTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/javafx/multi-attributes";
   }

   @Override
   protected void configureModel(ClassModelBuilder mb)
   {
      mb.setJavaFXPropertyStyle();
   }
}
