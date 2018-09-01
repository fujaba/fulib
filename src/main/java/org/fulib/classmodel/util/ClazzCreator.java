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
import org.fulib.classmodel.Clazz;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.AssocRole;

public class ClazzCreator implements AggregatedEntityCreator
{
   public static final ClazzCreator it = new ClazzCreator();
   
   private final String[] properties = new String[]
   {
      Clazz.PROPERTY_NAME,
      Clazz.PROPERTY_ATTRIBUTES,
      Clazz.PROPERTY_MODEL,
      Clazz.PROPERTY_ROLES,
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
      return new Clazz();
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

      if (Clazz.PROPERTY_NAME.equalsIgnoreCase(attribute))
      {
         return ((Clazz) target).getName();
      }

      if (Clazz.PROPERTY_ATTRIBUTES.equalsIgnoreCase(attribute))
      {
         return ((Clazz) target).getAttributes();
      }

      if (Clazz.PROPERTY_MODEL.equalsIgnoreCase(attribute))
      {
         return ((Clazz) target).getModel();
      }

      if (Clazz.PROPERTY_ROLES.equalsIgnoreCase(attribute))
      {
         return ((Clazz) target).getRoles();
      }
      
      return null;
   }
   
   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (Clazz.PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         ((Clazz) target).setName((String) value);
         return true;
      }

      if(SendableEntityCreator.REMOVE_YOU.equals(type)) {
           ((Clazz)target).removeYou();
           return true;
      }
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }

      if (Clazz.PROPERTY_ATTRIBUTES.equalsIgnoreCase(attrName))
      {
         ((Clazz) target).withAttributes((Attribute) value);
         return true;
      }
      
      if ((Clazz.PROPERTY_ATTRIBUTES + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Clazz) target).withoutAttributes((Attribute) value);
         return true;
      }

      if (Clazz.PROPERTY_MODEL.equalsIgnoreCase(attrName))
      {
         ((Clazz) target).setModel((ClassModel) value);
         return true;
      }

      if (Clazz.PROPERTY_ROLES.equalsIgnoreCase(attrName))
      {
         ((Clazz) target).withRoles((AssocRole) value);
         return true;
      }
      
      if ((Clazz.PROPERTY_ROLES + SendableEntityCreator.REMOVE).equalsIgnoreCase(attrName))
      {
         ((Clazz) target).withoutRoles((AssocRole) value);
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
      ((Clazz) entity).removeYou();
   }
}
