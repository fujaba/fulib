package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.TestGenerator;
import org.fulib.Tools;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

public class AttributeTest
{
   protected String getTargetFolder()
   {
      return "tmp/attributes";
   }

   @Test
   void testAttributeGenerator() throws Exception
   {
      final String targetFolder = this.getTargetFolder();
      final String srcFolder = targetFolder + "/src";
      final String outFolder = targetFolder + "/out";
      final String packageName = "org.fulib.test.studyright";

      Tools.removeDirAndFiles(targetFolder);

      ClassModel model = this.getAttributesModel(srcFolder, packageName);

      TestGenerator.createPreexistingUniFile(packageName, model);

      String uniFileName = model.getPackageSrcFolder() + "/University.java";
      assertThat("University.java exists", Files.exists(Paths.get(uniFileName)));

      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      Fulib.generator().generate(model);

      assertThat("University.java exists", Files.exists(Paths.get(uniFileName)));

      String studFileName = model.getPackageSrcFolder() + "/Student.java";
      assertThat("Student.java exists", Files.exists(Paths.get(studFileName)));

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      assertThat("compiler return code: ", returnCode, is(0));

      try (final URLClassLoader classLoader = URLClassLoader
         .newInstance(new URL[] { new File(outFolder).toURI().toURL() }))
      {
         this.runAttributeReadWriteTests(classLoader, packageName);
      }
   }

   protected ClassModel getAttributesModel(String srcFolder, String packageName)
   {
      return this.getAttributesModel(Fulib.classModelBuilder(packageName, srcFolder));
   }

   protected final ClassModel getAttributesModel(ClassModelBuilder mb)
   {
      mb.buildClass("University").buildAttribute("name", Type.STRING);

      mb.buildClass("Student").buildAttribute("name", Type.STRING, "\"Karli\"")
        .buildAttribute("matrNo", Type.LONG, "0");

      return mb.getClassModel();
   }

   private void runAttributeReadWriteTests(ClassLoader classLoader, String packageName) throws Exception
   {
      final ArrayList<PropertyChangeEvent> eventList = new ArrayList<>();

      // run self test

      Class<?> uniClass = Class.forName(packageName + ".University", true, classLoader);

      Object studyRight = uniClass.newInstance();

      Method addPropertyChangeListener = uniClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);

      PropertyChangeListener listener = eventList::add;
      addPropertyChangeListener.invoke(studyRight, listener);

      assertThat(studyRight, hasProperty("name", nullValue()));

      Method setName = uniClass.getMethod("setName", String.class);

      Object setNameReturn = setName.invoke(studyRight, "StudyRight");
      assertThat("setName returned this", setNameReturn, is(sameInstance(studyRight)));
      assertThat("got property change", !eventList.isEmpty());

      PropertyChangeEvent evt = eventList.get(0);
      assertThat(evt.getPropertyName(), is(equalTo("name")));
      assertThat("event new value", evt.getNewValue(), is(equalTo("StudyRight")));

      // set name with same value again --> no propertyChange
      setName.invoke(studyRight, "StudyRight");
      assertThat("no property change", eventList.size() == 1);
      assertThat(studyRight, hasProperty("name", equalTo("StudyRight")));

      // change name
      setName.invoke(studyRight, "StudyFuture");
      assertThat(studyRight, hasProperty("name", equalTo("StudyFuture")));
      assertThat("got property change", eventList.size() == 2);
      evt = eventList.get(1);
      assertThat("event property", evt.getPropertyName(), is(equalTo("name")));
      assertThat("event new value", evt.getNewValue(), is(equalTo("StudyFuture")));

      // testing int attr
      eventList.clear();

      Class<?> studClass = Class.forName(packageName + ".Student", true, classLoader);

      Object karli = studClass.newInstance();
      Method setStudentName = studClass.getMethod("setName", String.class);
      setStudentName.invoke(karli, "Karli");

      Method setMatrNo = studClass.getMethod("setMatrNo", long.class);
      addPropertyChangeListener = studClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
      addPropertyChangeListener.invoke(karli, listener);

      assertThat(karli, hasProperty("matrNo", equalTo(0L)));

      Object setMatrNoReturn = setMatrNo.invoke(karli, 42);

      assertThat("set method returned this", setMatrNoReturn, is(sameInstance(karli)));
      assertThat(karli, hasProperty("matrNo", equalTo(42L)));
      assertThat("got property change", eventList.size() == 1);
      evt = eventList.get(0);
      assertThat("event property", evt.getPropertyName(), is(equalTo("matrNo")));
      assertThat("event new value", evt.getNewValue(), is(42L));

      setMatrNoReturn = setMatrNo.invoke(karli, 42);

      assertThat("set method returned this", setMatrNoReturn, is(sameInstance(karli)));
      assertThat("no property change", eventList.size() == 1);

      setMatrNo.invoke(karli, 23);
      assertThat("got property change", eventList.size() == 2);

      // test toString()
      Method toString = studClass.getMethod("toString");
      Object txt = toString.invoke(karli);
      assertThat("toString", txt, is(equalTo("Karli")));

      toString = uniClass.getMethod("toString");
      txt = toString.invoke(studyRight);
      assertThat("toString", txt, is(equalTo("Hello")));
   }
}
