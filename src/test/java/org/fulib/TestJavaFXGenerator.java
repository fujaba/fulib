package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.CodeFragment;
import org.fulib.classmodel.FileFragmentMap;
import org.junit.Assert;
import org.junit.Test;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;


public class TestJavaFXGenerator
{

   private Class<?> uniClass;
   private URLClassLoader classLoader;
   private Object studyRight;
   private Class<?> assignClass;
   private Class<?> studClass;

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

      Fulib.generator().generate(model);

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

      Fulib.generator().generate(model);

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

      Fulib.generator().generate(model);

      // add implements clause to TeachingAssistant
      FileFragmentMap fragmentMap = Parser.parse(model.getPackageSrcFolder() + "/TeachingAssistent.java");
      CodeFragment fragment = fragmentMap.getFragment(Parser.CLASS);
      fragment.setText("@Deprecated \npublic class TeachingAssistent extends Student implements java.io.Serializable \n{");
      fragmentMap.writeFile();
      fragmentMap.add(Parser.CLASS, "public class TeachingAssistent extends Student \n{", 1);
      assertThat(fragment.getText(), containsString("@Deprecated"));
      assertThat(fragment.getText(), containsString("implements java.io.Serializable"));
      assertThat(fragment.getText(), containsString("{"));

      Fulib.generator().generate(model);

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
   public void testTables() throws Exception
   {
      String targetFolder = "tmp";
      String packageName = "org.fulib.tables.studyright";

      Tools.removeDirAndFiles(targetFolder);

      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright", "tmp/src");

      ClassBuilder uni = mb.buildClass("University")
            .buildAttribute("name", mb.STRING);

      ClassBuilder student = mb.buildClass("Student")
            .buildAttribute("name", mb.STRING)
            .buildAttribute("studentId", mb.STRING)
            .buildAttribute("credits", mb.INT);

      student.buildAssociation(student, "friends", mb.MANY, "friends", mb.MANY);
      uni.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);

      ClassBuilder ta = mb.buildClass("Tutor").setSuperClass(student);

      ClassBuilder room = mb.buildClass("Room")
            .buildAttribute("roomNo", mb.STRING)
            .buildAttribute("topic", mb.STRING);

      uni.buildAssociation(room, "rooms", mb.MANY, "uni", mb.ONE);
      student.buildAssociation(room, "in", mb.ONE, "students", mb.MANY);

      ClassBuilder assignment = mb.buildClass("Assignment")
            .buildAttribute("topic", mb.STRING)
            .buildAttribute("points", mb.INT);

      room.buildAssociation(assignment, "assignments", mb.MANY, "room", mb.ONE);
      student.buildAssociation(assignment, "done", mb.MANY, "students", mb.MANY);

      ClassModel model = mb.getClassModel();

      Fulib.generator().generate(model);

      Fulib.tablesGenerator().generate(model);

      // generate again to test recognition of existing fragments
      Fulib.generator().generate(model);

      Fulib.tablesGenerator().generate(model);

      String uniFileName = model.getPackageSrcFolder() + "/tables/UniversityTable.java";
      Assert.assertTrue("UniversityTable.java exists", Files.exists(Paths.get(uniFileName)));

      String outFolder = model.getMainJavaDir() + "/../out";
      int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
      Assert.assertEquals("compiler return code: ", 0, returnCode);
      returnCode = Tools.javac(outFolder, model.getPackageSrcFolder()+"/tables");
      Assert.assertEquals("compiler return code: ", 0, returnCode);

      runTableTests(outFolder, model);
   }


   @Test
   public void testValidIdentifiers()
   {
      try
      {
         Fulib.classModelBuilder("org.extends.tools");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         Fulib.classModelBuilder("org.fulib.");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         Fulib.classModelBuilder(".org.fulib");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         Fulib.classModelBuilder("org fulib");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         Fulib.classModelBuilder("org$fulib");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      ClassModelBuilder fF3 = Fulib.classModelBuilder("__fF3");

      try
      {
         ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
         mb.buildClass(null);
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
         mb.buildClass("");
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         c1.buildAttribute("42", mb.STRING);
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         c1.buildAttribute("a42", mb.STRING);
         c1.buildAttribute("a42", mb.STRING);
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         ClassBuilder c2 = mb.buildClass("C1");
         fail();
      }
      catch (IllegalArgumentException e) {  }


      try
      {
         ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         ClassBuilder c2 = mb.buildClass("C2");
         c1.buildAttribute("a42", mb.STRING);
         c1.buildAssociation(c2, "a42", mb.MANY, "b", mb.MANY);
         fail();
      }
      catch (IllegalArgumentException e) {  }

      try
      {
         ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
         ClassBuilder c1 = mb.buildClass("C1");
         c1.buildAssociation(c1, "x", mb.MANY, "x", mb.ONE);
         fail();
      }
      catch (IllegalArgumentException e) {  }


      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
      ClassBuilder c1 = mb.buildClass("C1");
      c1.buildAssociation(c1, "x", mb.MANY, "x", mb.MANY);
   }


   @Test
   public void testModelEvolution() throws IOException
   {
      Tools.removeDirAndFiles("tmp");


      // first simple model
      ClassModelBuilder mb = Fulib.classModelBuilder("org.evolve", "tmp/src");
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

      Fulib.generator().generate(firstModel);

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

      Fulib.generator().generate(firstModel);
      assertThat(logRecordList.size(), not(equalTo(0)));

      compileResult = Tools.javac("tmp/out", firstModel.getPackageSrcFolder());
      assertThat(compileResult, equalTo(0));

      // rename a class
      uni.getClazz().setName("Institute");

      // rename an association

   }


   @Test
   public void testCustomTemplates() throws IOException
   {
      Tools.removeDirAndFiles("tmp");

      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright", "tmp/src");
      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
      ClassBuilder studi = mb.buildClass( "Student")
            .buildAttribute("name", mb.STRING,"\"Karli\"")
            .buildAttribute("matrNo", mb.LONG,"0");

      ClassModel model = mb.getClassModel();

      // generate normal
      Fulib.generator()
            .generate(model);

      byte[] bytes = Files.readAllBytes(Paths.get(model.getPackageSrcFolder() + "/Student.java"));
      String content = new String(bytes);
      assertThat(content, not(containsString("/* custom attribute comment */")));

      // generate custom
      // start_code_fragment: testCustomTemplates
      Fulib.generator()
            .setCustomTemplatesFile("templates/custom.stg")
            .generate(model);
      // end_code_fragment:

      bytes = Files.readAllBytes(Paths.get(model.getPackageSrcFolder() + "/Student.java"));
      content = new String(bytes);
      assertThat(content, containsString("/* custom attribute comment */"));

   }


   private void createPreexistingUniFile(String packageName, ClassModel model) throws IOException
   {
      // create pre existing University class with extra elements
      STGroup group = new STGroupFile("templates/university.stg");
      ST uniTemplate = group.getInstanceOf("university");
      uniTemplate.add("packageName", packageName);
      String uniText = uniTemplate.render();

      Files.createDirectories(Paths.get(model.getPackageSrcFolder()));
      Files.write(Paths.get(model.getPackageSrcFolder() + "/University.java"), uniText.getBytes());
   }


   private ClassModel getClassModelUniStudWithAttributes(String targetFolder, String packageName)
   {
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, targetFolder + "/src")
            .setJavaFXPropertyStyle();

      ClassBuilder universitiy = mb.buildClass( "University")
            .buildAttribute("name", mb.STRING);

      ClassBuilder studi = mb.buildClass( "Student")
            .buildAttribute("name", mb.STRING,"\"Karli\"")
            .buildAttribute("matrNo", mb.LONG,"0");

      return mb.getClassModel();
   }


   private ClassModel getClassModelWithAssociations(String targetFolder, String packageName)
   {
      // start_code_fragment: ClassModelBuilder.twoParams
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName, "src/main/java")
            .setJavaFXPropertyStyle();

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
      // end_code_fragment:

      mb.getClassModel().setMainJavaDir(targetFolder + "/src");

      ClassBuilder studi = mb.buildClass( "Student")
            .buildAttribute("name", mb.STRING,"\"Karli\"");

      universitiy.buildAssociation(studi, "students", mb.MANY, "uni", mb.ONE);

      ClassBuilder room = mb.buildClass("Room")
            .buildAttribute("no", mb.STRING);

      universitiy.buildAssociation(room, "rooms", mb.MANY, "uni", mb.ONE)
            .setSourceRoleCollection(LinkedHashSet.class)
            .setAggregation();

      studi.buildAssociation(room, "condo", mb.ONE, "owner", mb.ONE);

      studi.buildAssociation(room, "in", mb.MANY, "students", mb.MANY);

      ClassBuilder assignment = mb.buildClass("Assignment").buildAttribute("topic", mb.STRING);
      studi.buildAssociation(assignment, "done", mb.MANY, "students", mb.MANY);

      return mb.getClassModel();
   }


   private ClassModel getClassModelWithExtends(String targetFolder, String packageName)
   {
      // start_code_fragment: ClassModelBuilder
      ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name", mb.STRING);
      // end_code_fragment:

      mb.getClassModel().setMainJavaDir(targetFolder + "/src");

      // start_code_fragment: ClassBuilder.buildAttribute_init
      ClassBuilder student = mb.buildClass( "Student")
            .buildAttribute("name", mb.STRING,"\"Karli\"");
      // end_code_fragment:

      // start_code_fragment: ClassBuilder.buildAssociation
      universitiy.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);
      // end_code_fragment:

      ClassBuilder room = mb.buildClass("Room")
            .buildAttribute("no", mb.STRING);

      universitiy.buildAssociation(room, "rooms", mb.MANY, "uni", mb.ONE)
            .setSourceRoleCollection(LinkedHashSet.class);

      student.buildAssociation(room, "condo", mb.ONE, "owner", mb.ONE);

      student.buildAssociation(room, "in", mb.MANY, "students", mb.MANY);

      ClassBuilder ta = mb.buildClass("TeachingAssistent")
            .setSuperClass(student)
            .buildAttribute("level", mb.STRING);

      ClassModel model = mb.getClassModel();

      return model;
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

      PropertyChangeListener listener = evt -> eventList.add(evt);

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
      Method setStudentName = studClass.getMethod("setName", String.class);
      setStudentName.invoke(karli, "Karli");

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

      // ok, create a link
      assertThat(studyRight, hasProperty("students", is(empty())));
      assertThat(karli, hasProperty("uni", nullValue()));

      Method withStudents = uniClass.getMethod("withStudents", studClass);
      Object withResult = withStudents.invoke(studyRight, karli);
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

      withStudents.invoke(studyRight, karli);
      withStudents.invoke(studyRight, lee);
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      assertThat(lee, hasProperty("uni", equalTo(studyRight)));

      withStudents.invoke(studyFuture, karli);
      withStudents.invoke(studyFuture, lee);
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(studyFuture, hasProperty("students", not(containsInAnyOrder(studyRight))));
      assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

      Method withoutStudents = uniClass.getMethod("withoutStudents", studClass);
      withoutStudents.invoke(studyFuture, karli);
      withoutStudents.invoke(studyFuture, lee);
      withoutStudents.invoke(studyFuture, lee);
      assertThat(studyFuture, hasProperty("students", is(empty())));
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(lee, hasProperty("uni", nullValue()));

      withStudents.invoke(studyRight, karli);
      withStudents.invoke(studyRight, lee);
      withStudents.invoke(studyFuture, lee);
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(lee)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

      // test LinkedHashSet role
      Object wa1337 = roomClass.newInstance();
      Object wa1342 = roomClass.newInstance();

      Method withRooms = uniClass.getMethod("withRooms", roomClass);
      Method setUni4Room = roomClass.getMethod("setUni", uniClass);

      Object withRoomsResult = withRooms.invoke(studyRight, wa1337);
      withRooms.invoke(studyRight, wa1342);
      assertThat(withRoomsResult, equalTo(studyRight));
      assertThat(studyRight, hasProperty("rooms", containsInAnyOrder(wa1337, wa1342)));
      assertThat(wa1337, hasProperty("uni", equalTo(studyRight)));
      assertThat(wa1342, hasProperty("uni", equalTo(studyRight)));

      withRooms.invoke(studyFuture, wa1342);
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
      Method withIn = studClass.getMethod("withIn", roomClass);
      Method withStudents4Room = roomClass.getMethod("withStudents", studClass);

      Object withInResult = withIn.invoke(karli, wa1337);
      withIn.invoke(karli, wa1342);
      withIn.invoke(lee, wa1337);
      withIn.invoke(lee, wa1342);
      assertThat(withInResult, equalTo(karli));
      assertThat(karli, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
      assertThat(lee, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
      assertThat(wa1337, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(wa1342, hasProperty("students", containsInAnyOrder(karli, lee)));

      Method withoutStudents4Room = roomClass.getMethod("withoutStudents", studClass);
      withoutStudents4Room.invoke(wa1337, lee);
      assertThat(wa1337, hasProperty("students", not(containsInAnyOrder(lee))));
      assertThat(lee, hasProperty("in", not(containsInAnyOrder(wa1337))));

      Method removeYou = uniClass.getMethod("removeYou");
      removeYou.invoke(studyRight);
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(wa1337, hasProperty("uni", nullValue()));
      assertThat(wa1337, hasProperty("students", not(containsInAnyOrder(karli))));
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


   public void runTableTests(String outFolder, ClassModel model) throws Exception
   {
      getTableExampleObjects(outFolder, model);

      // simple table
      Class<?> uniTableClass = Class.forName(model.getPackageName() + ".tables.UniversityTable", true, classLoader);
      Class<?> roomsTableClass = Class.forName(model.getPackageName() + ".tables.RoomTable", true, classLoader);
      Class<?> studentsTableClass = Class.forName(model.getPackageName() + ".tables.StudentTable", true, classLoader);
      Class<?> assignmentsTableClass = Class.forName(model.getPackageName() + ".tables.AssignmentTable", true, classLoader);
      Class<?> intTableClass = Class.forName(model.getPackageName() + ".tables.intTable", true, classLoader);

      Constructor<?> declaredConstructors = uniTableClass.getDeclaredConstructors()[0];
      Method uniExpandRooms = uniTableClass.getMethod("expandRooms", String[].class);
      Method uniExpandStudents = uniTableClass.getMethod("expandStudents", String[].class);
      Method roomsExpandAssignments = roomsTableClass.getMethod("expandAssignments", String[].class);
      Method roomsExpandStudents = roomsTableClass.getMethod("expandStudents", String[].class);
      Method studentTablehasDone = studentsTableClass.getMethod("hasDone", assignmentsTableClass);
      Method studentTableSelectColumns = studentsTableClass.getMethod("selectColumns", String[].class);
      Method studentTableDropColumns = studentsTableClass.getMethod("dropColumns", String[].class);
      Method studentTableAddColumn = studentsTableClass.getMethod("addColumn", String.class, Function.class);
      Method assignmentsToSet = assignmentsTableClass.getMethod("toSet");
      Method assignmentsExpandPoints = assignmentsTableClass.getMethod("expandPoints", String[].class);
      Method assignmentsFilter = assignmentsTableClass.getMethod("filter", Predicate.class);
      Method assignmentsFilterRow = assignmentsTableClass.getMethod("filterRow", Predicate.class);
      Method intTableToList = intTableClass.getMethod("toList");
      Method intTableSum = intTableClass.getMethod("sum");
      final Method assignmentGetPoints = assignClass.getMethod("getPoints");
      final Method studentGetDone = studClass.getMethod("getDone");

      Object uniArray = Array.newInstance(uniClass, 1);
      Array.set(uniArray, 0, studyRight);

      Object uniTable = declaredConstructors.newInstance(uniArray);
      assertThat(uniTable, notNullValue());

      Object roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
      assertThat(roomsTable.toString(), containsString("wa1337") );
      assertThat(roomsTable.toString(), containsString("wa1338") );
      assertThat(roomsTable.toString(), containsString("wa1339") );

      Object assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});
      assertThat(assignmentsTable.toString(), containsString("integrals") );

      Set assignmentsSet = (Set) assignmentsToSet.invoke(assignmentsTable);
      assertThat(assignmentsSet.size(), equalTo(4));

      Object pointsTable = assignmentsExpandPoints.invoke(assignmentsTable, new Object[]{new String[]{"Points"}});
      List pointsList = (List) intTableToList.invoke(pointsTable);
      assertThat(pointsList.size(), equalTo(4));

      Object sum = intTableSum.invoke(pointsTable);
      assertThat(sum, equalTo(89));

      Object studentsTable = roomsExpandStudents.invoke(roomsTable, new Object[]{new String[]{"Students"}});
      assertThat(studentsTable.toString(), containsString("Alice") );

      Predicate<Object> predicate = new Predicate<Object>(){
         @Override
         public boolean test(Object o)
         {
            try
            {
               int points = (Integer) assignmentGetPoints.invoke(o);
               return points <= 20;
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
            return false;
         }
      };
      assignmentsFilter.invoke(assignmentsTable, predicate);
      assertThat(assignmentsTable.toString(), not(containsString("integrals")));
      assertThat(assignmentsTable.toString(), containsString("sculptures"));

      // filter row
      uniTable = declaredConstructors.newInstance(uniArray);
      studentsTable = uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});
      roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
      assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});


      Predicate<Object> rowPredicate = new Predicate<Object>(){
         @Override
         public boolean test(Object o)
         {
            try
            {
               LinkedHashMap<String,Object> row = (LinkedHashMap<String, Object>) o;
               Object stud = row.get("Students");
               Object assign = row.get("Assignments");
               Collection doneSet = (Collection) studentGetDone.invoke(stud);
               return ! doneSet.contains(assign);
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
            return false;
         }
      };

      assignmentsFilterRow.invoke(assignmentsTable, rowPredicate);
      assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tintegrals")));
      assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math \tmatrices"));


      // has done
      uniTable = declaredConstructors.newInstance(uniArray);
      studentsTable = uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});
      roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
      assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});

      studentTablehasDone.invoke(studentsTable, assignmentsTable);
      assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math \tintegrals"));
      assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tmatrices")));

      // select columns
      uniTable = declaredConstructors.newInstance(uniArray);
      studentsTable = uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});
      roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
      assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});

      studentTableSelectColumns.invoke(studentsTable, new Object[]{new String[]{"Students", "Rooms"}});
      assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math"));
      assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tintegrals")));

      // drop columns
      uniTable = declaredConstructors.newInstance(uniArray);
      studentsTable = uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});
      roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
      assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});

      studentTableDropColumns.invoke(studentsTable, new Object[]{new String[]{"Assignments"}});
      assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math"));
      assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tintegrals")));

      // add column
      uniTable = declaredConstructors.newInstance(uniArray);
      studentsTable = uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});
      Function<LinkedHashMap<String,Object>,Object> function = new Function<LinkedHashMap<String, Object>, Object>()
      {
         @Override
         public Object apply(LinkedHashMap<String, Object> row)
         {
            Object student = row.get("Students");
            return 42;
         }
      };
      studentTableAddColumn.invoke(studentsTable, "Credits", function);
      assertThat(studentsTable.toString(), containsString("Credits"));
      assertThat(studentsTable.toString(), containsString("42"));
   }


   private void getTableExampleObjects(String outFolder, ClassModel model) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException
   {
      // create example objects
      File classesDir = new File(outFolder);

      // Load and instantiate compiled class.
      classLoader = URLClassLoader.newInstance(new URL[] { classesDir.toURI().toURL() });

      uniClass = Class.forName(model.getPackageName() + ".University", true, classLoader);
      studClass = Class.forName(model.getPackageName() + ".Student", true, classLoader);
      Class<?> roomClass = Class.forName(model.getPackageName() + ".Room", true, classLoader);
      assignClass = Class.forName(model.getPackageName() + ".Assignment", true, classLoader);

      Method uniSetName = uniClass.getMethod("setName", String.class);
      Method roomSetRoomNo = roomClass.getMethod("setRoomNo", String.class);
      Method roomSetTopic = roomClass.getMethod("setTopic", String.class);
      Method roomSetUni = roomClass.getMethod("setUni", uniClass);
      Method assignmentSetTopic = assignClass.getMethod("setTopic", String.class);
      Method assignmentSetPoints = assignClass.getMethod("setPoints", int.class);
      Method assignmentSetRoom = assignClass.getMethod("setRoom", roomClass);
      Method studStudentId = studClass.getMethod("setStudentId", String.class);
      Method studSetName = studClass.getMethod("setName", String.class);
      Method studSetUni = studClass.getMethod("setUni", uniClass);
      Method studSetIn = studClass.getMethod("setIn", roomClass);
      Method studWithDone = studClass.getMethod("withDone", Object[].class);


      studyRight = uniClass.newInstance();
      uniSetName.invoke(studyRight, "Study Right");

      Object mathRoom = roomClass.newInstance();
      roomSetRoomNo.invoke(mathRoom, "wa1337");
      roomSetTopic.invoke(mathRoom, "Math");
      roomSetUni.invoke(mathRoom, studyRight);

      Object artsRoom = roomClass.newInstance();
      roomSetRoomNo.invoke( artsRoom, "wa1338");
      roomSetTopic.invoke( artsRoom,"Arts");
      roomSetUni.invoke(artsRoom, studyRight);

      Object sportsRoom = roomClass.newInstance();
      roomSetRoomNo.invoke(sportsRoom, "wa1339");
      roomSetTopic.invoke(sportsRoom, "Football");
      roomSetUni.invoke(sportsRoom, studyRight);

      Object integrals = assignClass.newInstance();
      assignmentSetTopic.invoke(integrals, "integrals");
      assignmentSetPoints.invoke(integrals, 42);
      assignmentSetRoom.invoke(integrals, mathRoom);

      Object matrix = assignClass.newInstance();
      assignmentSetTopic.invoke(matrix, "matrices");
      assignmentSetPoints.invoke(matrix, 23);
      assignmentSetRoom.invoke(matrix, mathRoom);

      Object drawings = assignClass.newInstance();
      assignmentSetTopic.invoke(drawings, "drawings");
      assignmentSetPoints.invoke(drawings, 12);
      assignmentSetRoom.invoke(drawings, artsRoom);

      Object sculptures = assignClass.newInstance();
      assignmentSetTopic.invoke(sculptures, "sculptures");
      assignmentSetPoints.invoke(sculptures, 12);
      assignmentSetRoom.invoke(sculptures, artsRoom);

      Object alice = studClass.newInstance();
      studStudentId.invoke(alice, "m4242");
      studSetName.invoke(alice, "Alice");
      studSetUni.invoke(alice, studyRight);
      studSetIn.invoke(alice, artsRoom);
      studWithDone.invoke(alice, new Object[]{new Object[]{integrals}});

      Object bob   = studClass.newInstance();
      studStudentId.invoke(bob, "m2323");
      studSetName.invoke(bob, "Bobby"  );
      studSetUni.invoke(bob, studyRight);
      studSetIn.invoke(bob, artsRoom);
      ;

      Object carli = studClass.newInstance();
      studStudentId.invoke(carli, "m2323");
      studSetName.invoke(carli, "Carli");
      studSetUni.invoke(carli, studyRight);
      studSetIn.invoke(carli, mathRoom);
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
