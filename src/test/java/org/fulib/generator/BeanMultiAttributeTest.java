package org.fulib.generator;

import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;

public class BeanMultiAttributeTest extends MultiAttributeTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/bean/multi-attributes";
   }

   @Override
   protected void configureModel(ClassModelBuilder mb)
   {
      mb.setDefaultPropertyStyle(Type.BEAN);
   }
}
