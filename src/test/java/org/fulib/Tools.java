package org.fulib;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Tools
{
   public static int javac(String outFolder, String sourceFolder)
   {
      final String classPath = System.getProperty("java.class.path");
      return javac(classPath, outFolder, sourceFolder);
   }

   public static int javac(String classPath, String outFolder, String sourceFolder)
   {
      try
      {
         Files.createDirectories(Paths.get(outFolder));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      final File source = new File(sourceFolder);
      final List<String> args = new ArrayList<>();

      collectJavaFiles(sourceFolder, args, source);

      args.add("-d");
      args.add(outFolder);
      args.add("-classpath");
      args.add(outFolder + File.pathSeparator + classPath);

      return ToolProvider.getSystemJavaCompiler().run(null, System.out, System.err, args.toArray(new String[0]));
   }

   public static void collectJavaFiles(String sourceFolder, List<String> args, File source)
   {
      if (source == null)
      {
         return;
      }

      File[] listFiles = source.listFiles();
      if (listFiles == null)
      {
         return;
      }

      for (File file : listFiles)
      {
         if (file.isDirectory())
         {
            collectJavaFiles(sourceFolder + "/" + file.getName(), args, file);
         }
         else if (file.getName().endsWith(".java"))
         {
            args.add(sourceFolder + "/" + file.getName());
         }
      }
   }

   public static void removeDirAndFiles(String toBeDeletedDir) throws IOException
   {
      Path rootPath = Paths.get(toBeDeletedDir);

      if (!Files.exists(rootPath))
      {
         return;
      }

      //noinspection ResultOfMethodCallIgnored
      Files.walk(rootPath).map(Path::toFile).sorted(Comparator.reverseOrder()).forEach(File::delete);
   }
}
