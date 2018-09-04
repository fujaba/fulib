package org.fulib;

import org.fulib.Parser;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.FileFragmentMap;

public class Generator4CodeGenTests
{
   public static void generate(ClassModel model)
   {
      new Generator4CodeGenTests().generateSelfTest(model);
   }


   private void generateSelfTest(ClassModel model)
   {
      String fileName = model.getTestPackageDirName() + "/TestModelCode.java";
      FileFragmentMap fragmentMap = Parser.parse(fileName);

      String result = String.format("package %s;", model.getPackageName());
      fragmentMap.add(Parser.PACKAGE, result, 2);

      result = "" +
            "// @Test\n" +
            "public class TestModelCode\n{\n";
      fragmentMap.add(Parser.CLASS, result, 2);

      fragmentMap.add(Parser.CLASS_END, "}", 1);

      fragmentMap.writeFile();

   }

}
