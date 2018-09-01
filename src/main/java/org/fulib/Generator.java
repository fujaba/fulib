package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;

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
         doGenerate(clazz);
      }


   }

   private void doGenerate(Clazz clazz)
   {
      FileFragmentMap fragmentMap = Parser.parse(clazz);

      // doGenerate code for class
      String result = String.format("package %s;", clazz.getModel().getPackageName());
      fragmentMap.add(Parser.PACKAGE, result, 2);

      result = String.format("public class %s\n{", clazz.getName());
      fragmentMap.add(Parser.CLASS, result, 2);

      // doGenerate code for attributes

      // doGenerate code for association

      fragmentMap.add(Parser.CLASS_END, "}", 1);

      fragmentMap.writeFile();
   }
}
