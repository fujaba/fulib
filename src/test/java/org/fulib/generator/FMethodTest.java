package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.Tools;
import org.fulib.builder.ClassModelManager;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FMethod;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FMethodTest
{
   @Test
   public void testFMethods() throws Exception
   {
      final String targetFolder = "tmp/fmethods";
      final String sourceFolder = targetFolder + "/src";
      final String outFolder = targetFolder + "/out";
      final String packageName = "party.partyApp";

      Tools.removeDirAndFiles(targetFolder);

      ClassModelManager mm = new ClassModelManager();
      mm.setMainJavaDir(sourceFolder);
      mm.setPackageName(packageName);

      Clazz party = mm.haveClass("Party");
      // mm.haveAttribute(party, "name", "String");

      FMethod method = mm.haveMethod(party, "public void hello()", "      System.out.println(\"World!\");\n");

      ClassModel model = mm.getClassModel();
      Fulib.generator().generate(model);

      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      // does removal work?
      party.withoutMethods(method);

      Fulib.generator().generate(model);

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      method = mm.haveMethod(party, "public int theAnswer()", "      return 42;\n");

      Fulib.generator().generate(model);

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      // add a parameter
      method.getParams().put("question", "int");
      method.setMethodBody("      return question * 2;\n");

      Fulib.generator().generate(model);

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      party.withImports("import org.junit.jupiter.api.Test;");
      party.withImports("import static org.hamcrest.CoreMatchers.*;");
      party.withImports("import static org.hamcrest.MatcherAssert.assertThat;");

      mm.haveMethod(party, "" + "@Test\n" + "public void testQuestion()",
                    "" + "      assertThat(theAnswer(21), equalTo(42));\n");

      Fulib.generator().generate(model);

      mm.haveMethod(party, "public int theAnswer()", "      return 42;\n");

      // TODO why 3 times?
      Fulib.generator().generate(model);
      Fulib.generator().generate(model);
      Fulib.generator().generate(model);

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      try (final URLClassLoader classLoader = URLClassLoader
         .newInstance(new URL[] { new File(outFolder).toURI().toURL() }))
      {
         final Class<?> partyClass = Class.forName(model.getPackageName() + ".Party", true, classLoader);
         final Method answerMethod = partyClass.getMethod("theAnswer", int.class);
         final Method secondMethod = partyClass.getMethod("testQuestion");

         final Object theParty = partyClass.newInstance();

         final Object answer = answerMethod.invoke(theParty, 23);
         assertThat(answer, equalTo(46));

         secondMethod.invoke(theParty);
      }
   }
}
