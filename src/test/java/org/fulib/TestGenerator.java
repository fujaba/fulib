package org.fulib;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

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
      ClassModel model = new ClassModel()
            .withPackageName(packageName)
            .withSrcFolder(targetFolder + "/src");

      Clazz uni = model.createClasses().withName("University");
      uni.createAttributes().withName("name").withType("String");

      Clazz studi = model.createClasses().withName("Student");
      studi.createAttributes().withName("name").withType("String").withInitialization("\"Karli\"");
      studi.createAttributes().withName("matrNo").withType("long").withInitialization("0");

      deleteFile(studi);
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

      Class<?> cls = Class.forName(model.getPackageName() + ".University", true, classLoader);

      Object studyRight = cls.newInstance();

      Method addPropertyChangeListener = cls.getMethod("addPropertyChangeListener", PropertyChangeListener.class);

      addPropertyChangeListener.invoke(studyRight, new PropertyChangeListener()
      {
         @Override
         public void propertyChange(PropertyChangeEvent evt)
         {
            eventList.add(evt);
         }
      });

      Method getName = cls.getMethod("getName");

      Object name = getName.invoke(studyRight);
      Assert.assertNull("no name yet", name);

      Method setName = cls.getMethod("setName", String.class);

      setName.invoke(studyRight, "StudyRight");
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
