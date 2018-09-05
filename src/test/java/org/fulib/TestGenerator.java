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
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TestGenerator
{
   @Test
   public void testGenerator() throws IOException
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

      runSelfTest(outFolder, model);

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

   private void runSelfTest(String outFolder, ClassModel model)
   {
      final StringBuilder buf = new StringBuilder();

      // run self test
      File classesDir = new File(outFolder);

      // Load and instantiate compiled class.
      URLClassLoader classLoader;
      try {
         // Loading the class
         classLoader = URLClassLoader.newInstance(new URL[] { classesDir.toURI().toURL() });
         Class<?> cls;

         cls = Class.forName(model.getPackageName() + ".University", true, classLoader);

         Object studyRight = cls.newInstance();

         Method addPropertyChangeListener = cls.getMethod("addPropertyChangeListener", PropertyChangeListener.class);

         addPropertyChangeListener.invoke(studyRight, new PropertyChangeListener()
         {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
               buf.append(evt.toString());
            }
         });

         Method setName = cls.getMethod("setName", String.class);

         setName.invoke(studyRight, "StudyRight");

         Assert.assertTrue("got property change", buf.length() > 0);

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
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
