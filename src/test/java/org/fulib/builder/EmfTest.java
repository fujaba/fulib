package org.fulib.builder;

import org.junit.jupiter.api.Test;

public class EmfTest
{
   @Test
   void laboratyAutomation()
   {
      final String packageName = "org.fulib.test.laboratoryAutomation";

      ClassModelManager m = new ClassModelManager();
      m.setPackageName(packageName);

      m.haveEcore(getClass().getResource("laboratoryAutomation.ecore").toString());
   }

   @Test
   void jobCollection()
   {
      final String packageName = "org.fulib.test.jobCollection";

      ClassModelManager m = new ClassModelManager();
      m.setPackageName(packageName);

      m.haveEcore(getClass().getResource("jobCollection.ecore").toString());
   }
}
