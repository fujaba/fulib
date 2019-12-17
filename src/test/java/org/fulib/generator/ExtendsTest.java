package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.Parser;
import org.fulib.Tools;
import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.CodeFragment;
import org.fulib.classmodel.FileFragmentMap;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

public class ExtendsTest
{
   @Test
   void testExtendsGenerator() throws Exception
   {
      final String targetFolder = "tmp/extends";
      final String srcFolder = targetFolder + "/out";
      final String outFolder = targetFolder + "/out";
      final String packageName = "org.fulib.test.studyright";

      Tools.removeDirAndFiles(targetFolder);

      ClassModel model = this.getClassModel(srcFolder, packageName);

      UniversityFileHelper.create(packageName, model);

      Fulib.generator().generate(model);

      // add implements clause to TeachingAssistant
      FileFragmentMap fragmentMap = Parser.parse(model.getPackageSrcFolder() + "/TeachingAssistent.java");
      CodeFragment fragment = fragmentMap.getFragment(Parser.CLASS);
      fragment
         .setText("@Deprecated \npublic class TeachingAssistent extends Student implements java.io.Serializable \n{");
      fragmentMap.writeFile();
      fragmentMap.add(Parser.CLASS, "public class TeachingAssistent extends Student \n{", 1);
      assertThat(fragment.getText(), containsString("@Deprecated"));
      assertThat(fragment.getText(), containsString("implements java.io.Serializable"));
      assertThat(fragment.getText(), containsString("{"));

      Fulib.generator().generate(model);

      String uniFileName = model.getPackageSrcFolder() + "/University.java";
      assertThat("University.java exists", Files.exists(Paths.get(uniFileName)));

      String studFileName = model.getPackageSrcFolder() + "/Student.java";
      assertThat("Student.java exists", Files.exists(Paths.get(studFileName)));

      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      try (final URLClassLoader classLoader = URLClassLoader
         .newInstance(new URL[] { new File(outFolder).toURI().toURL() }))
      {
         this.runDataTests(classLoader, packageName);
      }
   }

   private ClassModel getClassModel(String srcFolder, String packageName)
   {
      // start_code_fragment: ClassModelBuilder
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, srcFolder);

      ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
      // end_code_fragment:

      // start_code_fragment: ClassBuilder.buildAttribute_init
      ClassBuilder student = mb.buildClass("Student").buildAttribute("name", Type.STRING, "\"Karli\"");
      // end_code_fragment:

      // start_code_fragment: ClassBuilder.buildAssociation
      universitiy.buildAssociation(student, "students", Type.MANY, "uni", Type.ONE);
      // end_code_fragment:

      ClassBuilder room = mb.buildClass("Room").buildAttribute("no", Type.STRING);

      universitiy.buildAssociation(room, "rooms", Type.MANY, "uni", Type.ONE)
                 .setSourceRoleCollection(LinkedHashSet.class);

      student.buildAssociation(room, "condo", Type.ONE, "owner", Type.ONE);

      student.buildAssociation(room, "in", Type.MANY, "students", Type.MANY);

      ClassBuilder ta = mb.buildClass("TeachingAssistent").setSuperClass(student).buildAttribute("level", Type.STRING);

      return mb.getClassModel();
   }

   private void runDataTests(ClassLoader classLoader, String packageName) throws Exception
   {
      Class<?> uniClass = Class.forName(packageName + ".University", true, classLoader);
      Class<?> taClass = Class.forName(packageName + ".TeachingAssistent", true, classLoader);

      Object studyRight = uniClass.newInstance();
      Object studyFuture = uniClass.newInstance();

      Method setName = uniClass.getMethod("setName", String.class);
      setName.invoke(studyRight, "Study Right");
      setName.invoke(studyRight, "Study Future");

      Object karli = taClass.newInstance();

      setName = taClass.getMethod("setName", String.class);
      setName.invoke(karli, "Karli");

      // ok, create a link
      assertThat(karli, hasProperty("uni", nullValue()));

      Method withStudents = uniClass.getMethod("withStudents", Object[].class);
      Object withResult = withStudents.invoke(studyRight, new Object[] { new Object[] { karli } });
      assertThat(withResult, is(equalTo(studyRight)));
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));

      Method setUni = taClass.getMethod("setUni", uniClass);
      Object setUniResult = setUni.invoke(karli, studyFuture);
      assertThat(setUniResult, is(equalTo(karli)));
      assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      assertThat(studyRight, hasProperty("students", is(empty())));
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli)));

      Method setLevel = taClass.getMethod("setLevel", String.class);
      setLevel.invoke(karli, "master");
      assertThat(karli, hasProperty("level", equalTo("master")));
   }
}
