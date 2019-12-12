package org.fulib.builder;

import org.fulib.Generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DecoratorMain
{
   public static final String FALLBACK_DECORATOR_CLASS = "GenModel";

   public static void main(String[] args)
   {
      if (args.length == 0)
      {
         System.err.println("usage: decorator <srcFolder> <package>...\n"
                            + "<srcFolder> - the source directory to generate classes into, e.g. 'src/main/java'\n"
                            + "<package>   - the fully qualified package name, e.g. 'org.example'");
         System.exit(1);
         return;
      }

      final String sourceFolder = args[0];

      for (int i = 1; i < args.length; i++)
      {
         final String packageName = args[i];
         run(sourceFolder, packageName);
      }
   }

   private static void run(String sourceFolder, String packageName)
   {
      final List<Class<? extends ClassModelDecorator>> decoratorClasses = getDecoratorClasses(packageName);
      if (decoratorClasses.isEmpty())
      {
         return;
      }

      final ClassModelManager manager = new ClassModelManager();
      manager.useSourceFolder(sourceFolder);
      manager.usePackageName(packageName);

      // if any decorator errors, we do not generate the model
      boolean shouldGenerate = true;

      for (final Class<? extends ClassModelDecorator> decoratorClass : decoratorClasses)
      {
         final ClassModelDecorator decorator;
         try
         {
            decorator = decoratorClass.getConstructor().newInstance();
         }
         catch (ReflectiveOperationException e)
         {
            System.err.println("failed to instantiate decorator '" + decoratorClass.getName() + "':");
            e.printStackTrace();
            shouldGenerate = false;
            continue;
         }

         try
         {
            decorator.decorate(manager);
         }
         catch (Exception e)
         {
            System.err.println(
               "failed to run decorator'" + decorator.getClass().getName() + "' failed in package '" + packageName
               + "':");
            e.printStackTrace();
            shouldGenerate = false;
         }
      }

      if (shouldGenerate)
      {
         new Generator().generate(manager.getClassModel());
      }
   }

   public static List<Class<? extends ClassModelDecorator>> getDecoratorClasses(String packageName)
   {
      try
      {
         final Class<?> packageInfoClass = Class.forName(packageName + ".package-info");
         final ClassModelDecorators annotation = packageInfoClass.getAnnotation(ClassModelDecorators.class);
         if (annotation != null)
         {
            return Arrays.asList(annotation.value());
         }
      }
      catch (ClassNotFoundException ignored)
      {
         // fallthrough
      }

      try
      {
         final Class<?> genModelClass = Class.forName(packageName + "." + FALLBACK_DECORATOR_CLASS);
         if (ClassModelDecorator.class.isAssignableFrom(genModelClass))
         {
            return Collections.singletonList((Class<? extends ClassModelDecorator>) genModelClass);
         }
      }
      catch (ClassNotFoundException e)
      {
         // fallthrough
      }

      return Collections.emptyList();
   }
}
