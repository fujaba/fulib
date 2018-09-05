package org.fulib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.Assert;
import org.junit.Test;

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
      studi.createAttributes().withName("matrNo").withType("long").withInitialization("0");

      deleteFile(studi);

      Generator.generate(model);
      
      String uniFileName = model.getPackageSrcFolder() + "/University.java";
      Assert.assertTrue("University.java exists", Files.exists(Paths.get(uniFileName)));

      // Generator4CodeGenTests.generate(model);

      int returnCode = Javac.compile(model.getPackageSrcFolder() + "/*.java");
      Assert.assertEquals("compiler return code: ", 0, returnCode);

      // run self test

   }
   
   
   
   @Test
   public void testGeneratorWithBuilder()
   {

      ClassModelBuilder mb = ClassModelBuilder.get("org.fulib.test.studyright","src/test/java");
      
      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name","String");
      
      ClassBuilder studi = mb.buildClass( "Student")
         .buildAttribute("name","String","\"Karli\"")
         .buildAttribute("matrNo","long","0");


      deleteFile(studi.getClazz());

      Generator.generate(mb.getClassModel());
      
      String uniFileName = mb.getClassModel().getPackageSrcFolder() + "/University.java";
      Assert.assertTrue("University.java exists", Files.exists(Paths.get(uniFileName)));

      // Generator4CodeGenTests.generate(model);

      int returnCode = Javac.compile(mb.getClassModel().getPackageSrcFolder() + "/*.java");
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
