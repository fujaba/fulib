package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.FulibTools;
import org.fulib.Tools;
import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.ClassModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class EmfTest
{
   @Test
   void testEcoreVisitor() throws Exception
   {
      final String targetFolder = "tmp/emf";
      final String srcFolder = targetFolder + "/src";
      final String outFolder = targetFolder + "/out";
      final String packageName = "org.fulib.test.emf";

      Tools.removeDirAndFiles(targetFolder);

      ClassModelManager m = new ClassModelManager();
      m.setPackageName(packageName).setMainJavaDir(srcFolder);

      m.haveEcore("laboratoryAutomation.ecore");
      m.haveEcore("jobCollection.ecore");

      ClassModel model = m.getClassModel();
      Fulib.generator().generate(model);

      FulibTools.classDiagrams().dumpSVG(model, "tmp/emf/src/org/fulib/test/emf/classDiagram.svg");
      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));
   }
}
