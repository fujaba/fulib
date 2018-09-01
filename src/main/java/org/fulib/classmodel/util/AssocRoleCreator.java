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
import org.fulib.classmodel.AssocRole;
import de.uniks.networkparser.list.ObjectSet;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodel.Clazz;

public class AssocRoleCreator implements AggregatedEntityCreator
{
   public static final AssocRoleCreator it = new AssocRoleCreator();
   
   private final String[] properties = new String[]
   {
      AssocRole.PROPERTY_CARDINALITY,
      AssocRole.PROPERTY_NAME,
      AssocRole.PROPERTY_CLAZZ,
      AssocRole.PROPERTY_OTHER,
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
      return new AssocRole();
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

      if (AssocRole.PROPERTY_CARDINALITY.equalsIgnoreCase(attribute))
      {
         return ((AssocRole) target).getCardinality();
      }

      if (AssocRole.PROPERTY_NAME.equalsIgnoreCase(attribute))
      {
         return ((AssocRole) target).getName();
      }

      if (AssocRole.PROPERTY_CLAZZ.equalsIgnoreCase(attribute))
      {
         return ((AssocRole) target).getClazz();
      }

      if (AssocRole.PROPERTY_OTHER.equalsIgnoreCase(attribute))
      {
         return ((AssocRole) target).getOther();
      }
      
      return null;
   }
   
   @Override
   public boolean setValue(Object target, String attrName, Object value, String type)
   {
      if (AssocRole.PROPERTY_NAME.equalsIgnoreCase(attrName))
      {
         ((AssocRole) target).setName((String) value);
         return true;
      }

      if (AssocRole.PROPERTY_CARDINALITY.equalsIgnoreCase(attrName))
      {
         ((AssocRole) target).setCardinality(Integer.parseInt(value.toString()));
         return true;
      }

      if(SendableEntityCreator.REMOVE_YOU.equals(type)) {
           ((AssocRole)target).removeYou();
           return true;
      }
      if (SendableEntityCreator.REMOVE.equals(type) && value != null)
      {
         attrName = attrName + type;
      }

      if (AssocRole.PROPERTY_CLAZZ.equalsIgnoreCase(attrName))
      {
         ((AssocRole) target).setClazz((Clazz) value);
         return true;
      }

      if (AssocRole.PROPERTY_OTHER.equalsIgnoreCase(attrName))
      {
         ((AssocRole) target).setOther((AssocRole) value);
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
      ((AssocRole) entity).removeYou();
   }
}
