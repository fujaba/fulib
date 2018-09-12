package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.Assert;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.*;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupDir;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class TestGenerator
{

   @Test
   public void testAttributeGenerator() throws Exception
   {
      String targetFolder = "tmp";
      String packageName = "org.fulib.test.studyright";

      Tools.removeDirAndFiles(targetFolder);

      ClassModel model = getClassModelUniStudWithAttributes(targetFolder, packageName);

      createPreexistingUniFile(packageName, model);

      String uniFileName = model.getPackageSrcFolder() + "/University.java";
      Assert.assertTrue("University.java exists", Files.exists(Paths.get(uniFileName)));

      String outFolder = model.getMainJavaDir() + "/../out";
      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      Assert.assertEquals("compiler return code: ", 0, returnCode);

      Generator.generate(model);

      Assert.assertTrue("University.java exists", Files.exists(Paths.get(uniFileName)));

      String studFileName = model.getPackageSrcFolder() + "/Student.java";
      Assert.assertTrue("Student.java exists", Files.exists(Paths.get(uniFileName)));

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      Assert.assertEquals("compiler return code: ", 0, returnCode);

      runAttributeReadWriteTests(outFolder, model);
   }


   @Test
   public void testAssociationGenerator() throws Exception
   {
      String targetFolder = "tmp";
      String packageName = "org.fulib.test.studyright";

      Tools.removeDirAndFiles(targetFolder);

      ClassModel model = getClassModelWithAssociations(targetFolder, packageName);

      createPreexistingUniFile(packageName, model);

      String uniFileName = model.getPackageSrcFolder() + "/University.java";
      Assert.assertTrue("University.java exists", Files.exists(Paths.get(uniFileName)));

      String outFolder = model.getMainJavaDir() + "/../out";
      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      Assert.assertEquals("compiler return code: ", 0, returnCode);

      Generator.generate(model);

      Assert.assertTrue("University.java exists", Files.exists(Paths.get(uniFileName)));

      String studFileName = model.getPackageSrcFolder() + "/Student.java";
      Assert.assertTrue("Student.java exists", Files.exists(Paths.get(uniFileName)));

      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      Assert.assertEquals("compiler return code: ", 0, returnCode);

      runAssociationReadWriteTests(outFolder, model);

   }


   @Test
   public void testExtendsGenerator() throws Exception
   {
      String targetFolder = "tmp";
      String packageName = "org.fulib.test.studyright";

      Tools.removeDirAndFiles(targetFolder);

      ClassModel model = getClassModelWithExtends(targetFolder, packageName);

      createPreexistingUniFile(packageName, model);


      Generator.generate(model);

      String uniFileName = model.getPackageSrcFolder() + "/University.java";
      Assert.assertTrue("University.java exists", Files.exists(Paths.get(uniFileName)));

      String studFileName = model.getPackageSrcFolder() + "/Student.java";
      Assert.assertTrue("Student.java exists", Files.exists(Paths.get(uniFileName)));

      String outFolder = model.getMainJavaDir() + "/../out";
      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      Assert.assertEquals("compiler return code: ", 0, returnCode);

      runExtendsReadWriteTests(outFolder, model);

   }

   @Test
   public void testValidIdentifiers()
   {
      try
      {
         ClassModelBuilder.get("org.extends.tools");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder.get("org.fulib.");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder.get(".org.fulib");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder.get("org fulib");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder.get("org$fulib");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      ClassModelBuilder fF3 = ClassModelBuilder.get("__fF3");

      try
      {
         ClassModelBuilder mb = ClassModelBuilder.get("org.fulib");
         mb.buildClass(null);
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = ClassModelBuilder.get("org.fulib");
         mb.buildClass("");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = ClassModelBuilder.get("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         c1.buildAttribute("42", mb.STRING);
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = ClassModelBuilder.get("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         c1.buildAttribute("a42", mb.STRING);
         c1.buildAttribute("a42", mb.STRING);
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = ClassModelBuilder.get("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         ClassBuilder c2 = mb.buildClass("C1");
         fail();
      }
      catch (IllegalArgumentException e) {  }


      try
      {
         ClassModelBuilder mb = ClassModelBuilder.get("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         ClassBuilder c2 = mb.buildClass("C2");
         c1.buildAttribute("a42", mb.STRING);
         c1.buildAssociation(c2, "a42", mb.MANY, "b", mb.MANY);
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = ClassModelBuilder.get("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         c1.buildAssociation(c1, "x", mb.MANY, "x", mb.ONE);
         fail();
      }
      catch (IllegalArgumentException e) {  }


      ClassModelBuilder mb = ClassModelBuilder.get("org.fulib");
      ClassBuilder c1 = mb.buildClass("C1");
      c1.buildAssociation(c1, "x", mb.MANY, "x", mb.MANY);
   }

   @Test
   public void testModelEvolution() throws IOException
   {
      Tools.removeDirAndFiles("tmp");


      // first simple model
      ClassModelBuilder mb = ClassModelBuilder.get("org.evolve", "tmp/src");
      ClassBuilder uni = mb.buildClass("University")
            .buildAttribute("uniName", mb.STRING);
      ClassBuilder stud = mb.buildClass("Student")
            .buildAttribute("matNo", mb.STRING)
            .buildAttribute("startYear", mb.INT);
      uni.buildAssociation(stud, "students", mb.MANY, "uni", mb.ONE);
      ClassBuilder room = mb.buildClass("Room")
            .buildAttribute("roomNo", mb.STRING);

      ClassModel firstModel = mb.getClassModel();

      createPreexistingUniFile("org.evolve", firstModel);

      Generator.generate(firstModel);

      int compileResult = Tools.javac("tmp/out", firstModel.getPackageSrcFolder());
      assertThat(compileResult, equalTo(0));
      assertThat(Files.exists(Paths.get(firstModel.getPackageSrcFolder() + "/University.java")), is(true));

      // rename an attribute
      uni.getClazz().setName("Institute");
      room.getClazz().setName("LectureHall");
      stud.getClazz().getAttribute("matNo").setName("studentId");
      stud.getClazz().getAttribute("startYear").setType(mb.STRING);

      // prepare logger
      Logger logger = Logger.getLogger(Generator.class.getName());
      final ArrayList<LogRecord> logRecordList = new ArrayList<>();
      Handler handler = new Handler()
      {
         @Override
         public void publish(LogRecord record)
         {
            logRecordList.add(record);
         }

         @Override
         public void flush()
         {
         }

         @Override
         public void close() throws SecurityException
         {
         }
      };
      logger.setUseParentHandlers(false);
      logger.addHandler(handler);
      logger.setLevel(Level.INFO);

      Generator.generate(firstModel);
      assertThat(logRecordList.size(), not(equalTo(0)));

      compileResult = Tools.javac("tmp/out", firstModel.getPackageSrcFolder());
      assertThat(compileResult, equalTo(0));

      // rename a class
      uni.getClazz().setName("Institute");

      // rename an association

   }



   private void createPreexistingUniFile(String packageName, ClassModel model) throws IOException
   {
      // create pre existing University class with extra elements
      STGroupDir group = new STGroupDir("templates");
      ST uniTemplate = group.getInstanceOf("university");
      uniTemplate.add("packageName", packageName);
      String uniText = uniTemplate.render();

      Files.createDirectories(Paths.get(model.getPackageSrcFolder()));
      Files.write(Paths.get(model.getPackageSrcFolder() + "/University.java"), uniText.getBytes());
   }


   private ClassModel getClassModelUniStudWithAttributes(String targetFolder, String packageName)
   {
      ClassModelBuilder mb = ClassModelBuilder.get(packageName,targetFolder + "/src");

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);

      ClassBuilder studi = mb.buildClass( "Student")
            .buildAttribute("name", mb.STRING,"\"Karli\"")
            .buildAttribute("matrNo", mb.LONG,"0");

      return mb.getClassModel();
   }


   private ClassModel getClassModelWithAssociations(String targetFolder, String packageName)
   {
      ClassModelBuilder mb = ClassModelBuilder.get(packageName,targetFolder + "/src");

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);

      ClassBuilder studi = mb.buildClass( "Student")
            .buildAttribute("name", mb.STRING,"\"Karli\"");

      universitiy.buildAssociation(studi, "students", mb.MANY, "uni", mb.ONE);

      ClassBuilder room = mb.buildClass("Room")
            .buildAttribute("no", mb.STRING);

      universitiy.buildAssociation(room, "rooms", mb.MANY, "uni", mb.ONE, LinkedHashSet.class, LinkedHashSet.class);

      studi.buildAssociation(room, "condo", mb.ONE, "owner", mb.ONE);

      studi.buildAssociation(room, "in", mb.MANY, "students", mb.MANY);

      return mb.getClassModel();
   }


   private ClassModel getClassModelWithExtends(String targetFolder, String packageName)
   {
      ClassModelBuilder mb = ClassModelBuilder.get(packageName,targetFolder + "/src");

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);

      ClassBuilder studi = mb.buildClass( "Student")
            .buildAttribute("name", mb.STRING,"\"Karli\"");

      universitiy.buildAssociation(studi, "students", mb.MANY, "uni", mb.ONE);

      ClassBuilder room = mb.buildClass("Room")
            .buildAttribute("no", mb.STRING);

      universitiy.buildAssociation(room, "rooms", mb.MANY, "uni", mb.ONE, LinkedHashSet.class, LinkedHashSet.class);

      studi.buildAssociation(room, "condo", mb.ONE, "owner", mb.ONE);

      studi.buildAssociation(room, "in", mb.MANY, "students", mb.MANY);

      ClassBuilder ta = mb.buildClass("TeachingAssistent")
            .setSuperClass(studi)
            .buildAttribute("level", mb.STRING);

      return mb.getClassModel();
   }


   private void runAttributeReadWriteTests(String outFolder, ClassModel model) throws Exception
   {
      final ArrayList<PropertyChangeEvent> eventList = new ArrayList<>();

      // run self test
      File classesDir = new File(outFolder);

      // Load and instantiate compiled class.
      URLClassLoader classLoader;
      // Loading the class
      classLoader = URLClassLoader.newInstance(new URL[] { classesDir.toURI().toURL() });

      Class<?> uniClass = Class.forName(model.getPackageName() + ".University", true, classLoader);

      Object studyRight = uniClass.newInstance();

      Method addPropertyChangeListener = uniClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);

      PropertyChangeListener listener = new PropertyChangeListener()
      {
         @Override
         public void propertyChange(PropertyChangeEvent evt)
         {
            eventList.add(evt);
         }
      };

      addPropertyChangeListener.invoke(studyRight, listener);

      Method getName = uniClass.getMethod("getName");

      Object name;
      Assert.assertThat(studyRight, hasProperty("name", nullValue()));

      Method setName = uniClass.getMethod("setName", String.class);

      Object setNameReturn = setName.invoke(studyRight, "StudyRight");
      Assert.assertEquals("setName returned this", studyRight, setNameReturn);
      Assert.assertTrue("got property change", eventList.size() > 0);

      PropertyChangeEvent evt = eventList.get(0);
      Assert.assertThat(evt.getPropertyName(), equalTo("name"));
      Assert.assertEquals("event new value", "StudyRight", evt.getNewValue());

      // set name with same value again --> no propertyChange
      setName.invoke(studyRight, "StudyRight");
      Assert.assertTrue("no property change", eventList.size() == 1);
      Assert.assertThat(studyRight, hasProperty("name", equalTo("StudyRight")));

      // change name
      setName.invoke(studyRight, "StudyFuture");
      Assert.assertThat(studyRight, hasProperty("name", equalTo("StudyFuture")));
      Assert.assertTrue("got property change", eventList.size() == 2);
      evt = eventList.get(1);
      Assert.assertEquals("event property", "name", evt.getPropertyName());
      Assert.assertEquals("event new value", "StudyFuture", evt.getNewValue());

      // testing int attr
      eventList.clear();

      Class<?> studClass = Class.forName(model.getPackageName() + ".Student", true, classLoader);

      Object karli = studClass.newInstance();

      Method setMatrNo = studClass.getMethod("setMatrNo", long.class);
      Method getMatrNo = studClass.getMethod("getMatrNo");
      addPropertyChangeListener = studClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
      addPropertyChangeListener.invoke(karli, listener);

      Object matrNo;
      Assert.assertThat(karli, hasProperty("matrNo", equalTo(0L)));

      Object setMatrNoReturn = setMatrNo.invoke(karli, 42);

      Assert.assertEquals("set method returned this", karli, setMatrNoReturn);
      Assert.assertThat(karli, hasProperty("matrNo", equalTo(42L)));
      Assert.assertTrue("got property change", eventList.size() == 1);
      evt = eventList.get(0);
      Assert.assertEquals("event property", "matrNo", evt.getPropertyName());
      Assert.assertEquals("event new value", 42L, evt.getNewValue());

      setMatrNoReturn = setMatrNo.invoke(karli, 42);

      Assert.assertEquals("set method returned this", karli, setMatrNoReturn);
      Assert.assertTrue("no property change", eventList.size() == 1);

      setMatrNoReturn = setMatrNo.invoke(karli, 23);
      Assert.assertTrue("got property change", eventList.size() == 2);

      // test toString()
      Method toString = studClass.getMethod("toString");
      Object txt = toString.invoke(karli);
      Assert.assertEquals("toString", "Karli", txt);

      toString = uniClass.getMethod("toString");
      txt = toString.invoke(studyRight);
      Assert.assertEquals("toString", "Hello", txt);
   }


   public void runAssociationReadWriteTests(String outFolder, ClassModel model) throws Exception
   {
      final ArrayList<PropertyChangeEvent> eventList = new ArrayList<>();
      PropertyChangeListener listener = new PropertyChangeListener()
      {
         @Override
         public void propertyChange(PropertyChangeEvent evt)
         {
            eventList.add(evt);
         }
      };

      // run self test
      File classesDir = new File(outFolder);

      // Load and instantiate compiled class.
      URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { classesDir.toURI().toURL() });

      Class<?> uniClass = Class.forName(model.getPackageName() + ".University", true, classLoader);
      Class<?> studClass = Class.forName(model.getPackageName() + ".Student", true, classLoader);
      Class<?> roomClass = Class.forName(model.getPackageName() + ".Room", true, classLoader);

      Object studyRight = uniClass.newInstance();
      Object studyFuture = uniClass.newInstance();

      Method setName = uniClass.getMethod("setName", String.class);
      Method addPropertyChangeListener = uniClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
      setName.invoke(studyRight, "Study Right");
      setName.invoke(studyFuture, "Study Future");
      addPropertyChangeListener.invoke(studyRight, listener);
      addPropertyChangeListener.invoke(studyFuture, listener);

      Object karli = studClass.newInstance();
      Object lee = studClass.newInstance();

      setName = studClass.getMethod("setName", String.class);
      addPropertyChangeListener = studClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
      setName.invoke(karli, "Karli");
      setName.invoke(lee, "Lee");
      addPropertyChangeListener.invoke(karli, listener);
      addPropertyChangeListener.invoke(lee, listener);

      // do not add to students directly
      Method getStudents = uniClass.getMethod("getStudents");
      Collection studentSet = (Collection) getStudents.invoke(studyRight);
      try
      {
         studentSet.add(karli);
         Assert.fail("should not be possible to add an object to a role set directly");
      }
      catch (Exception e)
      {
         assertThat(e, instanceOf(UnsupportedOperationException.class));
      }

      // ok, create a link
      assertThat(studyRight, hasProperty("students", is(empty())));
      assertThat(karli, hasProperty("uni", nullValue()));

      Method withStudents = uniClass.getMethod("withStudents", Object[].class);
      Object withResult = withStudents.invoke(studyRight, new Object[]{new Object[]{karli}});
      assertThat(withResult, is(equalTo(studyRight)));
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));

      Method setUni = studClass.getMethod("setUni", uniClass);
      Object setUniResult = setUni.invoke(karli, studyFuture);
      assertThat(setUniResult, is(equalTo(karli)));
      assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      assertThat(studyRight, hasProperty("students", is(empty())));
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli)));

      setUni.invoke(karli, new Object[]{null} );
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(studyFuture, hasProperty("students", is(empty())));

      withStudents.invoke(studyRight, new Object[]{new Object[]{karli, lee}});
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      assertThat(lee, hasProperty("uni", equalTo(studyRight)));

      try
      {
         withStudents.invoke(studyFuture, new Object[]{new Object[]{karli, lee, studyRight}});
         Assert.fail();
      }
      catch (Exception e)
      {
         // cool
      }
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(studyFuture, hasProperty("students", not(containsInAnyOrder(studyRight))));
      assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

      Method withoutStudents = uniClass.getMethod("withoutStudents", Object[].class);
      withoutStudents.invoke(studyFuture, new Object[]{new Object[]{karli, lee, studyRight}});
      assertThat(studyFuture, hasProperty("students", is(empty())));
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(lee, hasProperty("uni", nullValue()));

      withStudents.invoke(studyRight, new Object[]{new Object[]{karli, lee}});
      withStudents.invoke(studyFuture, new Object[]{new Object[]{lee}});
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(lee)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

      // test LinkedHashSet role
      Object wa1337 = roomClass.newInstance();
      Object wa1342 = roomClass.newInstance();

      Method withRooms = uniClass.getMethod("withRooms", Object[].class);
      Method setUni4Room = roomClass.getMethod("setUni", uniClass);

      Object withRoomsResult = withRooms.invoke(studyRight, new Object[]{new Object[]{wa1337, wa1342}});
      assertThat(withRoomsResult, equalTo(studyRight));
      assertThat(studyRight, hasProperty("rooms", containsInAnyOrder(wa1337, wa1342)));
      assertThat(wa1337, hasProperty("uni", equalTo(studyRight)));
      assertThat(wa1342, hasProperty("uni", equalTo(studyRight)));

      withRooms.invoke(studyFuture, new Object[]{new Object[]{wa1342}});
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
      Method withIn = studClass.getMethod("withIn", Object[].class);
      Method withStudents4Room = roomClass.getMethod("withStudents", Object[].class);

      Object withInResult = withIn.invoke(karli, new Object[]{new Object[]{wa1337, wa1342}});
      withIn.invoke(lee, new Object[]{new Object[]{wa1337, wa1342}});
      assertThat(withInResult, equalTo(karli));
      assertThat(karli, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
      assertThat(lee, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
      assertThat(wa1337, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(wa1342, hasProperty("students", containsInAnyOrder(karli, lee)));

      Method withoutStudents4Room = roomClass.getMethod("withoutStudents", Object[].class);
      withoutStudents4Room.invoke(wa1337, new Object[]{new Object[]{lee}});
      assertThat(wa1337, hasProperty("students", not(containsInAnyOrder(lee))));
      assertThat(lee, hasProperty("in", not(containsInAnyOrder(wa1337))));
   }


   public void runExtendsReadWriteTests(String outFolder, ClassModel model) throws Exception
   {
      // run self test
      File classesDir = new File(outFolder);

      // Load and instantiate compiled class.
      URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { classesDir.toURI().toURL() });

      Class<?> uniClass = Class.forName(model.getPackageName() + ".University", true, classLoader);
      Class<?> studClass = Class.forName(model.getPackageName() + ".Student", true, classLoader);
      Class<?> taClass = Class.forName(model.getPackageName() + ".TeachingAssistent", true, classLoader);
      Class<?> roomClass = Class.forName(model.getPackageName() + ".Room", true, classLoader);

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
      Object withResult = withStudents.invoke(studyRight, new Object[]{new Object[]{karli}});
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


   private void deleteFile(Clazz clazz)
   {
      ClassModel model = clazz.getModel();

      String studentFileName = model.getPackageSrcFolder() + "/" + clazz.getName() + ".java";

      try
      {
         Files.delete(Paths.get(studentFileName));
      }
      catch (IOException e)
      {
         // ok if not exists
      }
   }
}
