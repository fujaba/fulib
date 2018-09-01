package org.fulib;

import org.fulib.classmodel.ClassModel;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

public class Javac
{
   public static int compile(ClassModel model)
   {
      return new Javac().doCompile(model);
   }

   private int doCompile(ClassModel model)
   {
      ProcessBuilder javac = new ProcessBuilder()
            .command(
                  "javac",
                  "-version")
            .redirectErrorStream(true);

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
