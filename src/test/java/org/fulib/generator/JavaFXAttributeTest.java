package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.classmodel.ClassModel;

public class JavaFXAttributeTest extends AttributeTest
{
   @Override
   ClassModel getAttributesModel(String targetFolder, String packageName)
   {
      return this.getAttributesModel(Fulib.classModelBuilder(packageName, targetFolder + "/src")
                                          .setJavaFXPropertyStyle());
   }
}
