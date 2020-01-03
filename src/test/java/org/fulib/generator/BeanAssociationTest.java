package org.fulib.generator;

import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;

public class BeanAssociationTest extends AssociationTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/bean/associations";
   }

   @Override
   protected void configureModel(ClassModelBuilder mb)
   {
      mb.setDefaultPropertyStyle(Type.BEAN);
   }
}
