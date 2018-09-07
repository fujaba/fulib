package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.Assert;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.*;
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
      Assert.assertThat(studyRight, hasProperty("students", is(empty())));

      Assert.assertThat(karli, hasProperty("uni", nullValue()));

      Method withStudents = uniClass.getMethod("withStudents", Object[].class);
      Object withResult = withStudents.invoke(studyRight, new Object[]{new Object[]{karli}});
      Assert.assertThat(withResult, is(equalTo(studyRight)));
      Assert.assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      Assert.assertThat(karli, hasProperty("uni", equalTo(studyRight)));

      Method setUni = studClass.getMethod("setUni", uniClass);
      Object setUniResult = setUni.invoke(karli, studyFuture);
      Assert.assertThat(setUniResult, is(equalTo(karli)));
      Assert.assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      Assert.assertThat(studyRight, hasProperty("students", is(empty())));
      Assert.assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli)));

      setUni.invoke(karli, new Object[]{null} );
      Assert.assertThat(karli, hasProperty("uni", nullValue()));
      Assert.assertThat(studyFuture, hasProperty("students", is(empty())));

      withStudents.invoke(studyRight, new Object[]{new Object[]{karli, lee}});
      Assert.assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli, lee)));
      Assert.assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      Assert.assertThat(lee, hasProperty("uni", equalTo(studyRight)));

      withStudents.invoke(studyFuture, new Object[]{new Object[]{karli, lee, studyRight}});
      Assert.assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli, lee)));
      Assert.assertThat(studyFuture, hasProperty("students", not(containsInAnyOrder(studyRight))));
      Assert.assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      Assert.assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

      Method withoutStudents = uniClass.getMethod("withoutStudents", Object[].class);
      withoutStudents.invoke(studyFuture, new Object[]{new Object[]{karli, lee, studyRight}});
      Assert.assertThat(studyFuture, hasProperty("students", is(empty())));
      Assert.assertThat(karli, hasProperty("uni", nullValue()));
      Assert.assertThat(lee, hasProperty("uni", nullValue()));

      withStudents.invoke(studyRight, new Object[]{new Object[]{karli, lee}});
      withStudents.invoke(studyFuture, new Object[]{new Object[]{lee}});
      Assert.assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      Assert.assertThat(studyFuture, hasProperty("students", containsInAnyOrder(lee)));
      Assert.assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      Assert.assertThat(lee, hasProperty("uni", equalTo(studyFuture)));
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
