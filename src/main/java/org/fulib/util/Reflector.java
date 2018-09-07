package org.fulib.util;

import org.fulib.StrUtil;

import java.lang.reflect.Method;
import java.util.LinkedHashSet;

public class Reflector
{
   private String className = "";

   public String getClassName()
   {
      return className;
   }

   public Reflector setClassName(String className)
   {
      this.className = className;
      return this;
   }

   public void removeObject(Object object)
   {
      // call removeYou if possible
      try
      {
         Class<?> clazz = Class.forName(className);
         Method removeYou = clazz.getMethod("removeYou");
         removeYou.invoke(object);
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

   }


   private String[] properties = null;

   public String[] getProperties()
   {
      if (properties != null)
      {
         return properties;
      }

      try
      {
         Class<?> clazz = Class.forName(className);

         Method[] methods = clazz.getMethods();

         LinkedHashSet<String> fieldNames = new LinkedHashSet<String>();
         for (Method method : methods)
         {
            String methodName = method.getName();

            if (methodName.startsWith("get")
                  && ! methodName.equals("getClass"))
            {
               methodName = methodName.substring(3);

               methodName = StrUtil.downFirstChar(methodName);

               if (!"".equals(methodName.trim()))
               {
                  fieldNames.add(methodName);
               }
            }

         }

         properties = fieldNames.toArray(new String[]{});

         return properties;
      }
      catch (ClassNotFoundException e)
      {
         e.printStackTrace();
      }
      return null;
   }

   public Object newInstance()
   {
      try
      {
         Class<?> clazz = Class.forName(className);
         return clazz.newInstance();
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      return null;
   }

   public Object getValue(Object object, String attribute)
   {
      if (object == null)
      {
         return null;
      }

      try
      {
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("get" + StrUtil.cap(attribute));

         Object invoke = method.invoke(object);

         return invoke;
      }
      catch (Exception e)
      {
         try
         {
            Class<?> clazz = Class.forName(className);

            Method method = clazz.getMethod(attribute);

            Object invoke = method.invoke(object);

            return invoke;
         }
         catch (Exception e2)
         {
            // e.printStackTrace();
         }

      }

      return null;
   }

   public Object setValue(Object object, String attribute, Object value, String type)
   {
      if (object == null)
      {
         return null;
      }

      try
      {
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("set" + StrUtil.cap(attribute), value.getClass());

         Object result = method.invoke(object, value);

         return result;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      // maybe a number
      try
      {
         int intValue = Integer.parseInt((String) value);
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("set" + StrUtil.cap(attribute), int.class);

         method.invoke(object, intValue);

         return true;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }
      // maybe a double
      try
      {
         double doubleValue = Double.parseDouble((String) value);
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("set" + StrUtil.cap(attribute), double.class);

         method.invoke(object, doubleValue);

         return true;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      // maybe a float
      try
      {
         float floatValue = Float.parseFloat((String) value);
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("set" + StrUtil.cap(attribute), float.class);

         method.invoke(object, floatValue);

         return true;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }

      try
      {
         Class<?> clazz = Class.forName(className);

         Method method = clazz.getMethod("with" + StrUtil.cap(attribute), Object[].class);

         method.invoke(object, new Object[]{new Object[]{value}});

         return true;
      }
      catch (Exception e)
      {
         // e.printStackTrace();
      }
      return null;
   }

}
