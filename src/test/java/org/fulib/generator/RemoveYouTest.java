package org.fulib.generator;

import org.fulib.Generator;
import org.fulib.Tools;
import org.fulib.builder.ClassModelManager;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class RemoveYouTest
{
   @Test
   public void removeYou() throws IOException
   {
      final String targetDir = "tmp/removeYou";
      final String packageName = "org.example.removeYou";
      final String srcDir = targetDir + "/src";
      final String outDir = targetDir + "/out";

      Tools.removeDirAndFiles(targetDir);

      // build class model

      final ClassModelManager mm = new ClassModelManager().setMainJavaDir(srcDir).setPackageName(packageName);

      final Clazz empty = mm.haveClass("Empty");
      final Clazz assocs = mm.haveClass("Assocs", empty, c -> {});
      mm.associate(assocs, "r1", 1, assocs, "r2", 1);
      final Clazz empty2 = mm.haveClass("Empty2", assocs, c -> {});
      final Clazz assocs2 = mm.haveClass("Assocs2", empty2, c -> {});
      mm.associate(assocs2, "r3", 1, assocs2, "r4", 1);

      // generate

      final ClassModel model = mm.getClassModel();
      final String packageSrcDir = model.getPackageSrcFolder();
      new Generator().generate(model);

      // test file contents

      final String emptyContent = readFile(packageSrcDir, "Empty.java");
      assertThat(emptyContent, not(containsString("void removeYou()")));

      final String assocsContent = readFile(packageSrcDir, "Assocs.java");
      assertThat(assocsContent, containsString("void removeYou()"));
      assertThat(assocsContent, not(containsString("super.removeYou()")));

      final String empty2Content = readFile(packageSrcDir, "Empty2.java");
      assertThat(empty2Content, not(containsString("void removeYou()")));

      final String assocs2Content = readFile(packageSrcDir, "Assocs2.java");
      assertThat(assocs2Content, containsString("void removeYou()"));
      assertThat(assocs2Content, containsString("super.removeYou()"));
      assertThat(assocs2Content, containsString("@Override"));

      // test compile

      int returnCode = Tools.javac(outDir, packageSrcDir);
      assertThat("compiler return code: ", returnCode, is(0));
   }

   private static String readFile(String packageSrcDir, String javaFileName) throws IOException
   {
      return new String(Files.readAllBytes(Paths.get(packageSrcDir, javaFileName)), StandardCharsets.UTF_8);
   }
}
