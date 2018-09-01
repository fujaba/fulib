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

import de.uniks.networkparser.list.SimpleSet;
import org.fulib.classmodel.Attribute;
import de.uniks.networkparser.interfaces.Condition;
import java.util.Collection;
import de.uniks.networkparser.list.ObjectSet;
import org.fulib.classmodel.util.ClazzSet;
import org.fulib.classmodel.Clazz;

public class AttributeSet extends SimpleSet<Attribute>
{
	public Class<?> getTypClass() {
		return Attribute.class;
	}

   public AttributeSet()
   {
      // empty
   }

   public AttributeSet(Attribute... objects)
   {
      for (Attribute obj : objects)
      {
         this.add(obj);
      }
   }

   public AttributeSet(Collection<Attribute> objects)
   {
      this.addAll(objects);
   }

   public static final AttributeSet EMPTY_SET = new AttributeSet().withFlag(AttributeSet.READONLY);


   public AttributePO createAttributePO()
   {
      return new AttributePO(this.toArray(new Attribute[this.size()]));
   }


   public String getEntryType()
   {
      return "org.fulib.classmodel.Attribute";
   }


   @Override
   public AttributeSet getNewList(boolean keyValue)
   {
      return new AttributeSet();
   }


   @SuppressWarnings("unchecked")
   public AttributeSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<Attribute>)value);
      }
      else if (value != null)
      {
         this.add((Attribute) value);
      }
      
      return this;
   }
   
   public AttributeSet without(Attribute value)
   {
      this.remove(value);
      return this;
   }


   /**
    * Loop through the current set of Attribute objects and collect a list of the initialization attribute values. 
    * 
    * @return List of String objects reachable via initialization attribute
    */
   public ObjectSet getInitialization()
   {
      ObjectSet result = new ObjectSet();
      
      for (Attribute obj : this)
      {
         result.add(obj.getInitialization());
      }
      
      return result;
   }


   /**
    * Loop through the current set of Attribute objects and collect those Attribute objects where the initialization attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of Attribute objects that match the parameter
    */
   public AttributeSet createInitializationCondition(String value)
   {
      AttributeSet result = new AttributeSet();
      
      for (Attribute obj : this)
      {
         if (value.equals(obj.getInitialization()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of Attribute objects and collect those Attribute objects where the initialization attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of Attribute objects that match the parameter
    */
   public AttributeSet createInitializationCondition(String lower, String upper)
   {
      AttributeSet result = new AttributeSet();
      
      for (Attribute obj : this)
      {
         if (lower.compareTo(obj.getInitialization()) <= 0 && obj.getInitialization().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of Attribute objects and assign value to the initialization attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of Attribute objects now with new attribute values.
    */
   public AttributeSet withInitialization(String value)
   {
      for (Attribute obj : this)
      {
         obj.setInitialization(value);
      }
      
      return this;
   }


   /**
    * Loop through the current set of Attribute objects and collect a list of the name attribute values. 
    * 
    * @return List of String objects reachable via name attribute
    */
   public ObjectSet getName()
   {
      ObjectSet result = new ObjectSet();
      
      for (Attribute obj : this)
      {
         result.add(obj.getName());
      }
      
      return result;
   }


   /**
    * Loop through the current set of Attribute objects and collect those Attribute objects where the name attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of Attribute objects that match the parameter
    */
   public AttributeSet createNameCondition(String value)
   {
      AttributeSet result = new AttributeSet();
      
      for (Attribute obj : this)
      {
         if (value.equals(obj.getName()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of Attribute objects and collect those Attribute objects where the name attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of Attribute objects that match the parameter
    */
   public AttributeSet createNameCondition(String lower, String upper)
   {
      AttributeSet result = new AttributeSet();
      
      for (Attribute obj : this)
      {
         if (lower.compareTo(obj.getName()) <= 0 && obj.getName().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of Attribute objects and assign value to the name attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of Attribute objects now with new attribute values.
    */
   public AttributeSet withName(String value)
   {
      for (Attribute obj : this)
      {
         obj.setName(value);
      }
      
      return this;
   }


   /**
    * Loop through the current set of Attribute objects and collect a list of the type attribute values. 
    * 
    * @return List of String objects reachable via type attribute
    */
   public ObjectSet getType()
   {
      ObjectSet result = new ObjectSet();
      
      for (Attribute obj : this)
      {
         result.add(obj.getType());
      }
      
      return result;
   }


   /**
    * Loop through the current set of Attribute objects and collect those Attribute objects where the type attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of Attribute objects that match the parameter
    */
   public AttributeSet createTypeCondition(String value)
   {
      AttributeSet result = new AttributeSet();
      
      for (Attribute obj : this)
      {
         if (value.equals(obj.getType()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of Attribute objects and collect those Attribute objects where the type attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of Attribute objects that match the parameter
    */
   public AttributeSet createTypeCondition(String lower, String upper)
   {
      AttributeSet result = new AttributeSet();
      
      for (Attribute obj : this)
      {
         if (lower.compareTo(obj.getType()) <= 0 && obj.getType().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of Attribute objects and assign value to the type attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of Attribute objects now with new attribute values.
    */
   public AttributeSet withType(String value)
   {
      for (Attribute obj : this)
      {
         obj.setType(value);
      }
      
      return this;
   }

   /**
    * Loop through the current set of Attribute objects and collect a set of the Clazz objects reached via clazz. 
    * 
    * @return Set of Clazz objects reachable via clazz
    */
   public ClazzSet getClazz()
   {
      ClazzSet result = new ClazzSet();
      
      for (Attribute obj : this)
      {
         result.with(obj.getClazz());
      }
      
      return result;
   }

   /**
    * Loop through the current set of Attribute objects and collect all contained objects with reference clazz pointing to the object passed as parameter. 
    * 
    * @param value The object required as clazz neighbor of the collected results. 
    * 
    * @return Set of Clazz objects referring to value via clazz
    */
   public AttributeSet filterClazz(Object value)
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
      
      AttributeSet answer = new AttributeSet();
      
      for (Attribute obj : this)
      {
         if (neighbors.contains(obj.getClazz()) || (neighbors.isEmpty() && obj.getClazz() == null))
         {
            answer.add(obj);
         }
      }
      
      return answer;
   }

   /**
    * Loop through current set of ModelType objects and attach the Attribute object passed as parameter to the Clazz attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now with the new neighbor attached to their Clazz attributes.
    */
   public AttributeSet withClazz(Clazz value)
   {
      for (Attribute obj : this)
      {
         obj.withClazz(value);
      }
      
      return this;
   }

}
