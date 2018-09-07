package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;

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
