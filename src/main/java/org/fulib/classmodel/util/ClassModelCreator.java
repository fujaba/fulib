/*
   Copyright (c) 2018 zuend
   
   Permission is hereby granted, free of charge, to any person obtaining a copy of this software 
   and associated documentation files (the "Software"), to deal in the Software without restriction, 
   including without limitation the rights to use, copy, modify, merge, publish, distribute, 
   sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is 
   furnished to do so, subject to the following conditions: 
   
   The above copyright notice and this permission notice shall be included in all copies or 
   substantial portions of the Software. 
   
   The Software shall be used for Good, not Evil. 
   
   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING 
   BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
   DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. 
 */
   
package org.fulib.classmodel.util;

import de.uniks.networkparser.interfaces.AggregatedEntityCreator;
import org.fulib.classmodel.ClassModel;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodel.Clazz;

public class ClassModelCreator implements AggregatedEntityCreator
{
   public static final ClassModelCreator it = new ClassModelCreator();
   
   private final String[] properties = new String[]
   {
      ClassModel.PROPERTY_PACKAGENAME,
      ClassModel.PROPERTY_CLASSES,
      ClassModel.PROPERTY_CODEDIR,
   };
   
   private final String[] upProperties = new String[]
   {
   };
   
   private final String[] downProperties = new String[]
   {
   };
   
   @Override
   public String[] getProperties()
   {
      return properties;
   }
   
   @Override
   public String[] getUpProperties()
   {
      return upProperties;
   }
   
   @Override
   public String[] getDownProperties()
   {
      return downProperties;
   }
   
   @Override
   public Object getSendableInstance(boolean reference)
   {
      return new ClassModel();
   }
   
   
   @Override
   public Object getValue(Object target, String attrName)
   {
      int pos = attrName.indexOf('.');
      String attribute = attrName;
      
      if (pos > 0)
      {
         attribute = attrName.substring(0, pos);
      }

      if (ClassModel.PROPERTY_PACKAGENAME.equalsIgnoreCase(attribute))
      {
         return ((ClassModel) target).getPackageName();
      }

      if (ClassModel.PROPERTY_CLASSES.equalsIgnoreCase(attribute))
      {
         return ((ClassModel) target).getClasses();
      }

      if (ClassModel.PROPERTY_CODEDIR.equalsIgnoreCase(attribute))
      {
         return ((ClassModel) target).getCodeDir();
      }
      
      return null;
   }
   
   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (ClassModel.PROPERTY_CODEDIR.equalsIgnoreCase(attrName))
      {
         ((ClassModel) target).setCodeDir((String) value);
         return true;
      }

      if (ClassModel.PROPERTY_PACKAGENAME.equalsIgnoreCase(attrName))
      {
         ((ClassModel) target).setPackageName((String) value);
         return true;
      }

      if(SendableEntityCreator.REMOVE_YOU.equals(type)) {
           ((ClassModel)target).removeYou();
           return true;
      }
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }

      if (ClassModel.PROPERTY_CLASSES.equalsIgnoreCase(attrName))
      {
         ((ClassModel) target).withClasses((Clazz) value);
         return true;
      }
      
      if ((ClassModel.PROPERTY_CLASSES + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((ClassModel) target).withoutClasses((Clazz) value);
         return true;
      }
      
      return false;
   }
   public static IdMap createIdMap(String sessionID)
   {
      return org.fulib.classmodel.util.CreatorCreator.createIdMap(sessionID);
   }
   
   //==========================================================================
      public void removeObject(Object entity)
   {
      ((ClassModel) entity).removeYou();
   }
}
