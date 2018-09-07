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
   
package org.fulib.classmodelbysdmlib.util;

import de.uniks.networkparser.list.SimpleSet;
import org.fulib.classmodelbysdmlib.Clazz;

import java.util.Collection;
import de.uniks.networkparser.list.ObjectSet;
import java.util.Collections;

import org.fulib.classmodelbysdmlib.Attribute;
import org.fulib.classmodelbysdmlib.ClassModel;
import org.fulib.classmodelbysdmlib.AssocRole;

public class ClazzSet extends SimpleSet<Clazz>
{
	public Class<?> getTypClass() {
		return Clazz.class;
	}

   public ClazzSet()
   {
      // empty
   }

   public ClazzSet(Clazz... objects)
   {
      for (Clazz obj : objects)
      {
         this.add(obj);
      }
   }

   public ClazzSet(Collection<Clazz> objects)
   {
      this.addAll(objects);
   }

   public static final ClazzSet EMPTY_SET = new ClazzSet().withFlag(ClazzSet.READONLY);




   public String getEntryType()
   {
      return "org.fulib.classmodelbysdmlib.Clazz";
   }


   @Override
   public ClazzSet getNewList(boolean keyValue)
   {
      return new ClazzSet();
   }


   @SuppressWarnings("unchecked")
   public ClazzSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Clazz>)value);
      }
      else if (value != null)
      {
         this.add((Clazz) value);
      }
      
      return this;
   }
   
   public ClazzSet without(Clazz value)
   {
      this.remove(value);
      return this;
   }


   /**
    * Loop through the current set of Clazz objects and collect a list of the name attribute values. 
    * 
    * @return List of String objects reachable via name attribute
    */
   public ObjectSet getName()
   {
      ObjectSet result = new ObjectSet();
      
      for (Clazz obj : this)
      {
         result.add(obj.getName());
      }
      
      return result;
   }


   /**
    * Loop through the current set of Clazz objects and collect those Clazz objects where the name attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of Clazz objects that match the parameter
    */
   public ClazzSet createNameCondition(String value)
   {
      ClazzSet result = new ClazzSet();
      
      for (Clazz obj : this)
      {
         if (value.equals(obj.getName()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of Clazz objects and collect those Clazz objects where the name attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of Clazz objects that match the parameter
    */
   public ClazzSet createNameCondition(String lower, String upper)
   {
      ClazzSet result = new ClazzSet();
      
      for (Clazz obj : this)
      {
         if (lower.compareTo(obj.getName()) <= 0 && obj.getName().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of Clazz objects and assign value to the name attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of Clazz objects now with new attribute values.
    */
   public ClazzSet withName(String value)
   {
      for (Clazz obj : this)
      {
         obj.setName(value);
      }
      
      return this;
   }

   /**
    * Loop through the current set of Clazz objects and collect a set of the Attribute objects reached via attributes. 
    * 
    * @return Set of Attribute objects reachable via attributes
    */
   public AttributeSet getAttributes()
   {
      AttributeSet result = new AttributeSet();
      
      for (Clazz obj : this)
      {
         result.with(obj.getAttributes());
      }
      
      return result;
   }

   /**
    * Loop through the current set of Clazz objects and collect all contained objects with reference attributes pointing to the object passed as parameter. 
    * 
    * @param value The object required as attributes neighbor of the collected results. 
    * 
    * @return Set of Attribute objects referring to value via attributes
    */
   public ClazzSet filterAttributes(Object value)
   {
      ObjectSet neighbors = new ObjectSet();

      if (value instanceof Collection)
      {
         neighbors.addAll((Collection<?>) value);
      }
      else
      {
         neighbors.add(value);
      }
      
      ClazzSet answer = new ClazzSet();
      
      for (Clazz obj : this)
      {
         if ( ! Collections.disjoint(neighbors, obj.getAttributes()))
         {
            answer.add(obj);
         }
      }
      
      return answer;
   }

   /**
    * Loop through current set of ModelType objects and attach the Clazz object passed as parameter to the Attributes attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now with the new neighbor attached to their Attributes attributes.
    */
   public ClazzSet withAttributes(Attribute value)
   {
      for (Clazz obj : this)
      {
         obj.withAttributes(value);
      }
      
      return this;
   }

   /**
    * Loop through current set of ModelType objects and remove the Clazz object passed as parameter from the Attributes attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now without the old neighbor.
    */
   public ClazzSet withoutAttributes(Attribute value)
   {
      for (Clazz obj : this)
      {
         obj.withoutAttributes(value);
      }
      
      return this;
   }

   /**
    * Loop through the current set of Clazz objects and collect a set of the ClassModel objects reached via model. 
    * 
    * @return Set of ClassModel objects reachable via model
    */
   public ClassModelSet getModel()
   {
      ClassModelSet result = new ClassModelSet();
      
      for (Clazz obj : this)
      {
         result.with(obj.getModel());
      }
      
      return result;
   }

   /**
    * Loop through the current set of Clazz objects and collect all contained objects with reference model pointing to the object passed as parameter. 
    * 
    * @param value The object required as model neighbor of the collected results. 
    * 
    * @return Set of ClassModel objects referring to value via model
    */
   public ClazzSet filterModel(Object value)
   {
      ObjectSet neighbors = new ObjectSet();

      if (value instanceof Collection)
      {
         neighbors.addAll((Collection<?>) value);
      }
      else
      {
         neighbors.add(value);
      }
      
      ClazzSet answer = new ClazzSet();
      
      for (Clazz obj : this)
      {
         if (neighbors.contains(obj.getModel()) || (neighbors.isEmpty() && obj.getModel() == null))
         {
            answer.add(obj);
         }
      }
      
      return answer;
   }

   /**
    * Loop through current set of ModelType objects and attach the Clazz object passed as parameter to the Model attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now with the new neighbor attached to their Model attributes.
    */
   public ClazzSet withModel(ClassModel value)
   {
      for (Clazz obj : this)
      {
         obj.withModel(value);
      }
      
      return this;
   }

   /**
    * Loop through the current set of Clazz objects and collect a set of the AssocRole objects reached via roles. 
    * 
    * @return Set of AssocRole objects reachable via roles
    */
   public AssocRoleSet getRoles()
   {
      AssocRoleSet result = new AssocRoleSet();
      
      for (Clazz obj : this)
      {
         result.with(obj.getRoles());
      }
      
      return result;
   }

   /**
    * Loop through the current set of Clazz objects and collect all contained objects with reference roles pointing to the object passed as parameter. 
    * 
    * @param value The object required as roles neighbor of the collected results. 
    * 
    * @return Set of AssocRole objects referring to value via roles
    */
   public ClazzSet filterRoles(Object value)
   {
      ObjectSet neighbors = new ObjectSet();

      if (value instanceof Collection)
      {
         neighbors.addAll((Collection<?>) value);
      }
      else
      {
         neighbors.add(value);
      }
      
      ClazzSet answer = new ClazzSet();
      
      for (Clazz obj : this)
      {
         if ( ! Collections.disjoint(neighbors, obj.getRoles()))
         {
            answer.add(obj);
         }
      }
      
      return answer;
   }

   /**
    * Loop through current set of ModelType objects and attach the Clazz object passed as parameter to the Roles attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now with the new neighbor attached to their Roles attributes.
    */
   public ClazzSet withRoles(AssocRole value)
   {
      for (Clazz obj : this)
      {
         obj.withRoles(value);
      }
      
      return this;
   }

   /**
    * Loop through current set of ModelType objects and remove the Clazz object passed as parameter from the Roles attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now without the old neighbor.
    */
   public ClazzSet withoutRoles(AssocRole value)
   {
      for (Clazz obj : this)
      {
         obj.withoutRoles(value);
      }
      
      return this;
   }



   public ClazzPO createClazzPO()
   {
      return new ClazzPO(this.toArray(new Clazz[this.size()]));
   }
}
