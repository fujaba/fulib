package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.Tools;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CustomTemplateTest
{
   @Test
   void testCustomTemplates() throws IOException
   {
      final String targetFolder = "tmp/custom-templates";
      final String srcFolder = targetFolder + "/src";
      final String outFolder = targetFolder + "/out";

      Tools.removeDirAndFiles(targetFolder);

      final ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright", srcFolder);
      mb.buildClass("University").buildAttribute("name", Type.STRING);
      mb
         .buildClass("Student")
         .buildAttribute("name", Type.STRING, "\"Karli\"")
         .buildAttribute("matrNo", Type.LONG, "0");

      final ClassModel model = mb.getClassModel();

      // generate custom
      // start_code_fragment: testCustomTemplates
      Fulib.generator().setCustomTemplatesFile("templates/custom.stg").generate(model);
      // end_code_fragment:

      final Path path = Paths.get(model.getPackageSrcFolder(), "Student.java");
      final String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
      assertThat(content, containsString("/* custom attribute comment */"));

      final int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      // change comment text
      final String userChangedContent = content.replace("/* custom attribute comment */",
                                                        "/* attribute comment changed by user */");
      Files.write(path, userChangedContent.getBytes(StandardCharsets.UTF_8));

      // generate custom again
      Fulib.generator().setCustomTemplatesFile("templates/custom.stg").generate(model);

      final String loadedContent = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
      assertThat("it keeps user changes intact", loadedContent,
                 containsString("/* attribute comment changed by user */"));
   }
}
