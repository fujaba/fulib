package org.fulib.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReflectorMap
{
   Map<String, Reflector> reflectorMap = new LinkedHashMap<>();

   private ArrayList<String> packageNames;

   public ReflectorMap(String packageName)
   {
      ArrayList<String> packageNames = new ArrayList<>();

      packageNames.add(packageName);

      this.packageNames = packageNames;
   }

   public ReflectorMap(ArrayList<String> packageNames)
   {
      this.packageNames = packageNames;
   }


   public Reflector getReflector(Object newObject)
   {
      String simpleName = newObject.getClass().getSimpleName();

      return getReflector(simpleName);
   }


   public Reflector getReflector(String clazzName)
   {
      // already known?
      Reflector reflector = reflectorMap.get(clazzName);

      if (reflector != null)
      {
         return reflector;
      }

      for (String packageName : packageNames)
      {
         String fullClassName = packageName + "." + clazzName;

         try
         {
            Class<?> theClass = Class.forName(fullClassName);

            if (theClass != null)
            {
               reflector = new Reflector().setClassName(fullClassName);
               reflectorMap.put(clazzName, reflector);
               return reflector;
            }
         }
         catch (Exception e)
         {
            reflector = null;
         }
      }

      return reflector;
   }

}
