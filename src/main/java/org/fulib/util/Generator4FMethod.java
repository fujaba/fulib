package org.fulib.util;

import org.fulib.Generator;
import org.fulib.Parser;
import org.fulib.classmodel.FMethod;
import org.fulib.classmodel.FileFragmentMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Generator4FMethod
{

   public void generate(FMethod method)
   {
      String packageDir = method.getPackageName().replaceAll("\\.", "/");
      String srcDir = method.getJavaSrcDir() + "/" + packageDir;
      String classFileName = srcDir + "/" + method.getClassName() + ".java";
      FileFragmentMap fragmentMap = Parser.parse(classFileName);

      String signature = method.getSignature();
      String methodBody = method.getMethodBody();
      if (methodBody == null) {
         methodBody = "      // hello world\n";
      }
      String newText = "   " + method.getDeclaration() +
            "{ \n" +
            methodBody +
            "   }";

      fragmentMap.add(signature, newText, 2);

      fragmentMap.add(Parser.CLASS_END, "", 2, true);
      fragmentMap.add(Parser.CLASS_END, "}", 1, false);

      if (fragmentMap.classBodyIsEmpty(fragmentMap)) {
         Path path = Paths.get(classFileName);
         try {
            Files.deleteIfExists(path);
            Logger.getLogger(Generator.class.getName())
                  .info("\n   deleting empty file " + classFileName);
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         fragmentMap.writeFile();
      }

      System.out.println();
   }
}
