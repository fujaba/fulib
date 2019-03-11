package org.fulib;

import org.fulib.builder.ClassModelManager;
import org.junit.jupiter.api.Test;

public class TestClassModelDistribution
{
   @Test
   public void testClassModelDistribution()
   {
      ClassModelManager mm = new ClassModelManager("uniks.studyright", "tmp");

      mm.havePackageName("uniks.studyright")
         .haveMainJavaDir("tmp");
   }

}
