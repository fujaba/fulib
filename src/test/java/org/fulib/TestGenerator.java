package org.fulib;

import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.Test;

public class TestGenerator
{
   @Test
   public void testGenerator()
   {
      ClassModel model = new ClassModel()
            .withPackageName("org.fulib.test.studyright")
            .withCodeDir("src/test/java");

      Clazz uni = model.createClasses().withName("University");

      Clazz studi = model.createClasses().withName("Student");

      Generator.generate(model);
   }
}
