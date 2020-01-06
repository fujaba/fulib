package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.Tools;
import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.ClassModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UniAssocTest
{
   @Test
   void testUnidirectionalAssociations() throws Exception
   {
      final String targetFolder = "tmp/uni-assocs";
      final String srcFolder = targetFolder + "/src";
      final String outFolder = targetFolder + "/out";
      final String packageName = "org.fulib.test.studyright";

      Tools.removeDirAndFiles(targetFolder);

      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);

      ClassBuilder university = mb.buildClass("University");
      ClassBuilder prof = mb.buildClass("Prof");

      university.buildAssociation(prof, "head", Type.ONE, null, 0);
      university.buildAssociation(prof, "staff", Type.MANY, null, 0);

      ClassModel model = mb.getClassModel();
      Fulib.generator().generate(model);

      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      for (AssocRole r : new ArrayList<>(university.getClazz().getRoles()))
      {
         r.setClazz(null);
      }
      for (AssocRole r : new ArrayList<>(prof.getClazz().getRoles()))
      {
         r.setClazz(null);
      }

      university.buildAssociation(prof, "head", Type.ONE, "uni", Type.ONE);
      university.buildAssociation(prof, "staff", Type.MANY, "employer", Type.ONE);

      Fulib.generator().generate(model);

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      for (AssocRole r : new ArrayList<>(university.getClazz().getRoles()))
      {
         r.setClazz(null);
      }
      for (AssocRole r : new ArrayList<>(prof.getClazz().getRoles()))
      {
         r.setClazz(null);
      }

      university.buildAssociation(prof, "head", Type.ONE, null, 0);
      university.buildAssociation(prof, "staff", Type.MANY, null, 0);

      Fulib.generator().generate(model);

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));
   }
}
