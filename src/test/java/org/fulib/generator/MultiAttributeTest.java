package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.Tools;
import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MultiAttributeTest
{
   @Test
   public void testAttrList() throws Exception
   {
      final String testFolder = "tmp/multi-attributes";
      final String sourceFolder = testFolder + "/src";
      final String outFolder = testFolder + "/out";
      final String packageName = "org.testAttrList";

      Tools.removeDirAndFiles(testFolder);

      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, sourceFolder);

      ClassBuilder root = mb.buildClass("Root");
      root.buildAttribute("resultList", Type.INT + Type.__LIST);
      mb.buildClass("Kid").setSuperClass(root);

      ClassModel model = mb.getClassModel();
      Fulib.generator().generate(model);

      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      // Load and instantiate compiled class.
      try (final URLClassLoader classLoader = URLClassLoader
         .newInstance(new URL[] { new File(outFolder).toURI().toURL() }))
      {
         final Class<?> rootClass = Class.forName(packageName + ".Root", true, classLoader);
         final Method getMethod = rootClass.getMethod("getResultList");
         final Method withItemMethod = rootClass.getMethod("withResultList", Integer.class);
         final Method withoutItemMethod = rootClass.getMethod("withoutResultList", Integer.class);

         final Object theRoot = rootClass.newInstance();

         final Object answer = withItemMethod.invoke(theRoot, 23);
         assertThat(answer, equalTo(theRoot));

         final List<?> theList = (List<?>) getMethod.invoke(theRoot);
         assertThat(theList.size(), equalTo(1));

         withItemMethod.invoke(theRoot, 42);
         withItemMethod.invoke(theRoot, 23);
         assertThat(theList.size(), equalTo(3));

         withoutItemMethod.invoke(theRoot, 23);
         assertThat(theList.size(), equalTo(2));

         // TODO test other "with" and "without" overloads
      }

      // change to simple attribute
      Clazz modelRoot = model.getClazz("Root");
      Attribute modelResultList = modelRoot.getAttribute("resultList");
      modelResultList.setType(Type.INT);

      Fulib.generator().generate(model);

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      // change back to multi attribute
      modelResultList.setType(Type.INT + Type.__LIST);

      Fulib.generator().generate(model);

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));
   }
}
