package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestGenerator
{
   @Test
   public void testGenerator()
   {

      ClassModel model = new ClassModel()
            .withPackageName("org.fulib.test.studyright")
            .withSrcFolder("src/test/java");

      Clazz uni = model.createClasses().withName("University");
      uni.createAttributes().withName("name").withType("String");

      Clazz studi = model.createClasses().withName("Student");
      studi.createAttributes().withName("name").withType("String").withInitialization("\"Karli\"");

      deleteFile(studi);

      Generator.generate(model);
      
      String uniFileName = model.getPackageSrcFolder() + "/University.java";
      Assert.assertTrue("University.java exists", Files.exists(Paths.get(uniFileName)));

      // Generator4CodeGenTests.generate(model);

      int returnCode = Javac.compile(model.getPackageSrcFolder() + "/*.java");
      Assert.assertEquals("compiler return code: ", 0, returnCode);

      // run self test

   }

   private void deleteFile(Clazz clazz)
   {
      ClassModel model = clazz.getModel();

      String studentFileName = model.getPackageSrcFolder() + "/" + clazz.getName() + ".java";

      try
      {
         Files.delete(Paths.get(studentFileName));
      }
      catch (IOException e)
      {
         // ok if not exists
      }
   }
}
