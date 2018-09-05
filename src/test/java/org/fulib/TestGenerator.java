package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.Assert;
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


public class TestGenerator
{
   @Test
   public void testGenerator() throws Exception
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

      ClassBuilder universitiy = mb.buildClass( "University").buildAttribute("name","String");

      ClassBuilder studi = mb.buildClass( "Student")
            .buildAttribute("name","String","\"Karli\"")
            .buildAttribute("matrNo","long","0");

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

      Object name = getName.invoke(studyRight);
      Assert.assertNull("no name yet", name);

      Method setName = uniClass.getMethod("setName", String.class);

      Object setNameReturn = setName.invoke(studyRight, "StudyRight");
      Assert.assertEquals("setName returned this", studyRight, setNameReturn);
      name = getName.invoke(studyRight);
      Assert.assertEquals("name is now", "StudyRight", name);

      Assert.assertTrue("got property change", eventList.size() > 0);

      PropertyChangeEvent evt = eventList.get(0);
      Assert.assertEquals("event property", "name", evt.getPropertyName());
      Assert.assertEquals("event new value", "StudyRight", evt.getNewValue());

      // set name with same value again --> no propertyChange
      setName.invoke(studyRight, "StudyRight");
      Assert.assertTrue("no property change", eventList.size() == 1);
      name = getName.invoke(studyRight);
      Assert.assertEquals("name is now", "StudyRight", name);

      // change name
      setName.invoke(studyRight, "StudyFuture");
      name = getName.invoke(studyRight);
      Assert.assertEquals("name is now", "StudyFuture", name);
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

      Object matrNo = getMatrNo.invoke(karli);
      Assert.assertEquals("matrno", 0l, matrNo);

      Object setMatrNoReturn = setMatrNo.invoke(karli, 42);

      Assert.assertEquals("set method returned this", karli, setMatrNoReturn);

      matrNo = getMatrNo.invoke(karli);

      Assert.assertEquals("matrno", 42l, matrNo);
      Assert.assertTrue("got property change", eventList.size() == 1);
      evt = eventList.get(0);
      Assert.assertEquals("event property", "matrNo", evt.getPropertyName());
      Assert.assertEquals("event new value", 42L, evt.getNewValue());

      setMatrNoReturn = setMatrNo.invoke(karli, 42);

      Assert.assertEquals("set method returned this", karli, setMatrNoReturn);
      Assert.assertTrue("no property change", eventList.size() == 1);

      setMatrNoReturn = setMatrNo.invoke(karli, 23);
      Assert.assertTrue("got property change", eventList.size() == 2);

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
