package org.fulib.generator;

import org.fulib.Fulib;
import org.fulib.Tools;
import org.fulib.builder.ClassBuilder;
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
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssociationTest
{
   protected String getTargetFolder()
   {
      return "tmp/associations";
   }

   @Test
   void testAssociationGenerator() throws Exception
   {
      final String targetFolder = this.getTargetFolder();
      final String srcFolder = targetFolder + "/src";
      final String outFolder = targetFolder + "/out";
      final String packageName = "org.fulib.test.studyright";

      Tools.removeDirAndFiles(targetFolder);

      ClassModel model = this.getClassModel(srcFolder, packageName);

      UniversityFileHelper.create(packageName, model);

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
         this.runDataTests(classLoader, packageName);
      }
   }

   protected ClassModel getClassModel(String srcFolder, String packageName)
   {
      return this.getClassModel(Fulib.classModelBuilder(packageName, srcFolder)).setDefaultPropertyStyle(Type.BEAN);
   }

   protected final ClassModel getClassModel(ClassModelBuilder mb)
   {
      ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
      // end_code_fragment:

      ClassBuilder studi = mb.buildClass("Student").buildAttribute("name", Type.STRING, "\"Karli\"");

      universitiy.buildAssociation(studi, "students", Type.MANY, "uni", Type.ONE);

      ClassBuilder room = mb.buildClass("Room").buildAttribute("no", Type.STRING);

      universitiy.buildAssociation(room, "rooms", Type.MANY, "uni", Type.ONE)
                 .setSourceRoleCollection(LinkedHashSet.class).setAggregation();

      studi.buildAssociation(room, "condo", Type.ONE, "owner", Type.ONE);

      studi.buildAssociation(room, "in", Type.MANY, "students", Type.MANY);

      ClassBuilder assignment = mb.buildClass("Assignment").buildAttribute("topic", Type.STRING);
      studi.buildAssociation(assignment, "done", Type.MANY, "students", Type.MANY);

      return mb.getClassModel();
   }

   protected void runDataTests(ClassLoader classLoader, String packageName) throws Exception
   {
      Class<?> uniClass = Class.forName(packageName + ".University", true, classLoader);
      Class<?> studClass = Class.forName(packageName + ".Student", true, classLoader);
      Class<?> roomClass = Class.forName(packageName + ".Room", true, classLoader);

      Object studyRight = uniClass.newInstance();
      Object studyFuture = uniClass.newInstance();

      Method setName = uniClass.getMethod("setName", String.class);
      setName.invoke(studyRight, "Study Right");
      setName.invoke(studyFuture, "Study Future");

      Object karli = studClass.newInstance();
      Object lee = studClass.newInstance();

      setName = studClass.getMethod("setName", String.class);
      setName.invoke(karli, "Karli");
      setName.invoke(lee, "Lee");

      // do not add to students directly
      Method getStudents = uniClass.getMethod("getStudents");
      Collection studentSet = (Collection) getStudents.invoke(studyRight);
      assertThrows(UnsupportedOperationException.class, () -> studentSet.add(karli),
                   "should not be possible to add an object to a role set directly");

      // ok, create a link
      assertThat(studyRight, hasProperty("students", is(empty())));
      assertThat(karli, hasProperty("uni", nullValue()));

      Method withStudents = uniClass.getMethod("withStudents", Collection.class);
      Object withResult = withStudents.invoke(studyRight, Collections.singletonList(karli));
      assertThat(withResult, is(equalTo(studyRight)));
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));

      Method setUni = studClass.getMethod("setUni", uniClass);
      Object setUniResult = setUni.invoke(karli, studyFuture);
      assertThat(setUniResult, is(equalTo(karli)));
      assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      assertThat(studyRight, hasProperty("students", is(empty())));
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli)));

      setUni.invoke(karli, new Object[] { null });
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(studyFuture, hasProperty("students", is(empty())));

      withStudents.invoke(studyRight, Arrays.asList(karli, lee));
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      assertThat(lee, hasProperty("uni", equalTo(studyRight)));

      assertThrows(Exception.class, () -> withStudents.invoke(studyFuture, Arrays.asList(karli, lee, studyRight)));

      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(studyFuture, hasProperty("students", not(containsInAnyOrder(studyRight))));
      assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

      Method withoutStudents = uniClass.getMethod("withoutStudents", Collection.class);
      withoutStudents.invoke(studyFuture, Arrays.asList(karli, lee));
      assertThat(studyFuture, hasProperty("students", is(empty())));
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(lee, hasProperty("uni", nullValue()));

      withStudents.invoke(studyRight, Arrays.asList(karli, lee));
      withStudents.invoke(studyFuture, Collections.singletonList(lee));
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(lee)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

      // test LinkedHashSet role
      Object wa1337 = roomClass.newInstance();
      Object wa1342 = roomClass.newInstance();

      Method withRooms = uniClass.getMethod("withRooms", Collection.class);
      Method setUni4Room = roomClass.getMethod("setUni", uniClass);

      Object withRoomsResult = withRooms.invoke(studyRight, Arrays.asList(wa1337, wa1342));
      assertThat(withRoomsResult, equalTo(studyRight));
      assertThat(studyRight, hasProperty("rooms", containsInAnyOrder(wa1337, wa1342)));
      assertThat(wa1337, hasProperty("uni", equalTo(studyRight)));
      assertThat(wa1342, hasProperty("uni", equalTo(studyRight)));

      withRooms.invoke(studyFuture, Collections.singletonList(wa1342));
      assertThat(studyRight, hasProperty("rooms", not(containsInAnyOrder(wa1342))));
      assertThat(studyFuture, hasProperty("rooms", containsInAnyOrder(wa1342)));
      assertThat(wa1342, hasProperty("uni", equalTo(studyFuture)));

      // test 1 to 1
      Method setCondo = studClass.getMethod("setCondo", roomClass);
      Object setCondoResult = setCondo.invoke(karli, wa1337);
      assertThat(setCondoResult, equalTo(karli));
      assertThat(karli, hasProperty("condo", equalTo(wa1337)));
      assertThat(wa1337, hasProperty("owner", equalTo(karli)));

      setCondo.invoke(lee, wa1337);
      assertThat(karli, hasProperty("condo", nullValue()));
      assertThat(lee, hasProperty("condo", equalTo(wa1337)));
      assertThat(wa1337, hasProperty("owner", equalTo(lee)));

      // test n to m
      Method withIn = studClass.getMethod("withIn", Collection.class);

      Object withInResult = withIn.invoke(karli, Arrays.asList(wa1337, wa1342));
      withIn.invoke(lee, Arrays.asList(wa1337, wa1342));
      assertThat(withInResult, equalTo(karli));
      assertThat(karli, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
      assertThat(lee, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
      assertThat(wa1337, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(wa1342, hasProperty("students", containsInAnyOrder(karli, lee)));

      Method withoutStudents4Room = roomClass.getMethod("withoutStudents", Collection.class);
      withoutStudents4Room.invoke(wa1337, Collections.singletonList(lee));
      assertThat(wa1337, hasProperty("students", not(containsInAnyOrder(lee))));
      assertThat(lee, hasProperty("in", not(containsInAnyOrder(wa1337))));

      Method removeYou = uniClass.getMethod("removeYou");
      removeYou.invoke(studyRight);
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(wa1337, hasProperty("uni", nullValue()));
      assertThat(wa1337, hasProperty("students", not(containsInAnyOrder(karli))));
   }
}
