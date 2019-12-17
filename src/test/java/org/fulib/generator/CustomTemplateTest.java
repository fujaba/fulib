package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.Tools;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class CustomTemplateTest
{
   @Test
   void testCustomTemplates() throws IOException
   {
      final String targetFolder = "tmp/custom-templates";
      final String srcFolder = targetFolder + "/src";

      Tools.removeDirAndFiles(targetFolder);

      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright", srcFolder);
      mb.buildClass("University").buildAttribute("name", Type.STRING);
      mb.buildClass("Student").buildAttribute("name", Type.STRING, "\"Karli\"")
        .buildAttribute("matrNo", Type.LONG, "0");

      ClassModel model = mb.getClassModel();

      // generate normal
      Fulib.generator().generate(model);

      byte[] bytes = Files.readAllBytes(Paths.get(model.getPackageSrcFolder() + "/Student.java"));
      String content = new String(bytes);
      assertThat(content, not(containsString("/* custom attribute comment */")));

      // generate custom
      // start_code_fragment: testCustomTemplates
      Fulib.generator().setCustomTemplatesFile("templates/custom.stg").generate(model);
      // end_code_fragment:

      bytes = Files.readAllBytes(Paths.get(model.getPackageSrcFolder() + "/Student.java"));
      content = new String(bytes);
      assertThat(content, containsString("/* custom attribute comment */"));
   }
}
