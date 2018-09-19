package org.fulib;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Tools
{
   public static int javac(String outFolder, String sourceFolder)
   {
      ArrayList<String> args = new ArrayList<>();

      try
      {
         Files.createDirectories(Paths.get(outFolder));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

      File source = new File(sourceFolder);

      for (File file : source.listFiles())
      {
         if (file.getName().endsWith(".java"))
         {
            args.add(sourceFolder + "/" + file.getName());
         }
      }

      args.add("-d");
      args.add(outFolder);
      args.add("-classpath");
      args.add(outFolder);

      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

      int result = compiler.run(null, System.out, System.err, args.toArray(new String[0]));

      return result;
   }

   public static void removeDirAndFiles(String toBeDeletedDir) throws IOException
   {
      Path rootPath = Paths.get(toBeDeletedDir);

      if ( ! Files.exists(rootPath))
         return;

      final List<Path> pathsToDelete = Files.walk(rootPath).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
      for(Path path : pathsToDelete) {
         Files.deleteIfExists(path);
      }
   }

}
