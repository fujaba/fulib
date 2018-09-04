package org.fulib;

import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.stringtemplate.v4.*;

import java.util.logging.Logger;

public class Generator
{
   public static void generate(ClassModel model)
   {
      new Generator()
            .doGenerate(model);
   }

   public void doGenerate(ClassModel model)
   {
      // loop through all classes
      for (Clazz clazz : model.getClasses())
      {
         new Generator4ClassFile().doGenerate(clazz);
      }
   }



}
