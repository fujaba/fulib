package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.Tools;
import org.fulib.builder.ClassModelManager;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
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

      final ClassModelManager mm = new ClassModelManager();
      mm.setMainJavaDir(sourceFolder);
      mm.setPackageName(packageName);

      final Clazz party = mm.haveClass("Party");

      mm.haveMethod(party, "@Deprecated public int theAnswer()", "return 42;");
      mm.haveMethod(party, "public int theAnswer(int question)", "return question * 2;");
      mm.haveMethod(party, "public <T extends Number> import(java.util.List)<T> generic(T... args)",
                    "return import(java.util.Arrays).asList(args);");

      final ClassModel model = mm.getClassModel();
      Fulib.generator().generate(model);

      final int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      try (final URLClassLoader classLoader = URLClassLoader.newInstance(
         new URL[] { new File(outFolder).toURI().toURL() }))
      {
         final Class<?> partyClass = Class.forName(model.getPackageName() + ".Party", true, classLoader);
         final Object theParty = partyClass.newInstance();

         final Method answerMethod = partyClass.getMethod("theAnswer");
         assertThat(answerMethod.getAnnotation(Deprecated.class), notNullValue());
         assertThat(answerMethod.invoke(theParty), equalTo(42));

         final Method answerMethod2 = partyClass.getMethod("theAnswer", int.class);
         assertThat(answerMethod2.invoke(theParty, 23), equalTo(46));

         final Method genericMethod = partyClass.getMethod("generic", Number[].class);
         assertThat(genericMethod.isVarArgs(), is(true));
         assertThat(genericMethod.getTypeParameters()[0].getBounds()[0], is(Number.class));

         assertThat(genericMethod.invoke(theParty, (Object) new Number[] { 1, 2, 3 }), equalTo(Arrays.asList(1, 2, 3)));
      }
   }
}
