package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;

public class BeanAssociationTest extends AssociationTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/bean/associations";
   }

   @Override
   protected ClassModel getClassModel(String srcFolder, String packageName)
   {
      return this.getClassModel(Fulib.classModelBuilder(packageName, srcFolder).setDefaultPropertyStyle(Type.BEAN));
   }
}
