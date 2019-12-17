package org.fulib.generator;

import org.fulib.classmodel.ClassModel;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UniversityFileHelper
{
   public static void create(String packageName, ClassModel model) throws IOException
   {
      // create pre existing University class with extra elements
      STGroup group = new STGroupFile("templates/university.stg");
      ST uniTemplate = group.getInstanceOf("university");
      uniTemplate.add("packageName", packageName);
      String uniText = uniTemplate.render();

      Files.createDirectories(Paths.get(model.getPackageSrcFolder()));
      Files.write(Paths.get(model.getPackageSrcFolder() + "/University.java"), uniText.getBytes());
   }
}
