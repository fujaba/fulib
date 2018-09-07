package org.fulib;

import java.io.*;
import java.util.logging.Logger;

public class Javac
{
   public static int compile(String mainFiles)
   {
      return new Javac().doCompile(mainFiles);
   }

   private int doCompile(String mainFiles)
   {
      String classPath = System.getProperty("java.class.path");

      ProcessBuilder javac = new ProcessBuilder()
            .redirectErrorStream(true)
            .command(
                  "javac",
                  "-classpath", classPath,
                  "-d", "out",
                  mainFiles);

      try
      {
         Process process = javac.start();

         BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));

         StringBuffer buf = new StringBuffer();
         String line = in.readLine();

         while (line != null)
         {
            buf.append(line).append("\n");
            line = in.readLine();
         }

         int returnCode = process.waitFor();

         if (buf.length() > 0)
         {
            Logger.getGlobal().warning("\n" + buf.toString());
         }

         return returnCode;
      }
      catch (IOException | InterruptedException e)
      {
         e.printStackTrace();
      }


      return 23;
   }

}
