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
import org.fulib.classmodel.AssocRole;
import de.uniks.networkparser.interfaces.Condition;
import java.util.Collection;
import de.uniks.networkparser.list.NumberList;
import de.uniks.networkparser.list.ObjectSet;
import org.fulib.classmodel.util.ClazzSet;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.util.AssocRoleSet;

public class AssocRoleSet extends SimpleSet<AssocRole>
{
	public Class<?> getTypClass() {
		return AssocRole.class;
	}

   public AssocRoleSet()
   {
      // empty
   }

   public AssocRoleSet(AssocRole... objects)
   {
      for (AssocRole obj : objects)
      {
         this.add(obj);
      }
   }

   public AssocRoleSet(Collection<AssocRole> objects)
   {
      this.addAll(objects);
   }

   public static final AssocRoleSet EMPTY_SET = new AssocRoleSet().withFlag(AssocRoleSet.READONLY);


   public String getEntryType()
   {
      return "org.fulib.classmodel.AssocRole";
   }


   @Override
   public AssocRoleSet getNewList(boolean keyValue)
   {
      return new AssocRoleSet();
   }


   @SuppressWarnings("unchecked")
   public AssocRoleSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<AssocRole>)value);
      }
      else if (value != null)
      {
         this.add((AssocRole) value);
      }
      
      return this;
   }
   
   public AssocRoleSet without(AssocRole value)
   {
      this.remove(value);
      return this;
   }


   /**
    * Loop through the current set of AssocRole objects and collect a list of the cardinality attribute values. 
    * 
    * @return List of int objects reachable via cardinality attribute
    */
   public NumberList getCardinality()
   {
      NumberList result = new NumberList();
      
      for (AssocRole obj : this)
      {
         result.add(obj.getCardinality());
      }
      
      return result;
   }


   /**
    * Loop through the current set of AssocRole objects and collect those AssocRole objects where the cardinality attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of AssocRole objects that match the parameter
    */
   public AssocRoleSet createCardinalityCondition(int value)
   {
      AssocRoleSet result = new AssocRoleSet();
      
      for (AssocRole obj : this)
      {
         if (value == obj.getCardinality())
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of AssocRole objects and collect those AssocRole objects where the cardinality attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of AssocRole objects that match the parameter
    */
   public AssocRoleSet createCardinalityCondition(int lower, int upper)
   {
      AssocRoleSet result = new AssocRoleSet();
      
      for (AssocRole obj : this)
      {
         if (lower <= obj.getCardinality() && obj.getCardinality() <= upper)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of AssocRole objects and assign value to the cardinality attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of AssocRole objects now with new attribute values.
    */
   public AssocRoleSet withCardinality(int value)
   {
      for (AssocRole obj : this)
      {
         obj.setCardinality(value);
      }
      
      return this;
   }


   /**
    * Loop through the current set of AssocRole objects and collect a list of the name attribute values. 
    * 
    * @return List of String objects reachable via name attribute
    */
   public ObjectSet getName()
   {
      ObjectSet result = new ObjectSet();
      
      for (AssocRole obj : this)
      {
         result.add(obj.getName());
      }
      
      return result;
   }


   /**
    * Loop through the current set of AssocRole objects and collect those AssocRole objects where the name attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of AssocRole objects that match the parameter
    */
   public AssocRoleSet createNameCondition(String value)
   {
      AssocRoleSet result = new AssocRoleSet();
      
      for (AssocRole obj : this)
      {
         if (value.equals(obj.getName()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of AssocRole objects and collect those AssocRole objects where the name attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of AssocRole objects that match the parameter
    */
   public AssocRoleSet createNameCondition(String lower, String upper)
   {
      AssocRoleSet result = new AssocRoleSet();
      
      for (AssocRole obj : this)
      {
         if (lower.compareTo(obj.getName()) <= 0 && obj.getName().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of AssocRole objects and assign value to the name attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of AssocRole objects now with new attribute values.
    */
   public AssocRoleSet withName(String value)
   {
      for (AssocRole obj : this)
      {
         obj.setName(value);
      }
      
      return this;
   }

   /**
    * Loop through the current set of AssocRole objects and collect a set of the Clazz objects reached via clazz. 
    * 
    * @return Set of Clazz objects reachable via clazz
    */
   public ClazzSet getClazz()
   {
      ClazzSet result = new ClazzSet();
      
      for (AssocRole obj : this)
      {
         result.with(obj.getClazz());
      }
      
      return result;
   }

   /**
    * Loop through the current set of AssocRole objects and collect all contained objects with reference clazz pointing to the object passed as parameter. 
    * 
    * @param value The object required as clazz neighbor of the collected results. 
    * 
    * @return Set of Clazz objects referring to value via clazz
    */
   public AssocRoleSet filterClazz(Object value)
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
      
      AssocRoleSet answer = new AssocRoleSet();
      
      for (AssocRole obj : this)
      {
         if (neighbors.contains(obj.getClazz()) || (neighbors.isEmpty() && obj.getClazz() == null))
         {
            answer.add(obj);
         }
      }
      
      return answer;
   }

   /**
    * Loop through current set of ModelType objects and attach the AssocRole object passed as parameter to the Clazz attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now with the new neighbor attached to their Clazz attributes.
    */
   public AssocRoleSet withClazz(Clazz value)
   {
      for (AssocRole obj : this)
      {
         obj.withClazz(value);
      }
      
      return this;
   }

   /**
    * Loop through the current set of AssocRole objects and collect a set of the AssocRole objects reached via other. 
    * 
    * @return Set of AssocRole objects reachable via other
    */
   public AssocRoleSet getOther()
   {
      AssocRoleSet result = new AssocRoleSet();
      
      for (AssocRole obj : this)
      {
         result.with(obj.getOther());
      }
      
      return result;
   }

   /**
    * Loop through the current set of AssocRole objects and collect all contained objects with reference other pointing to the object passed as parameter. 
    * 
    * @param value The object required as other neighbor of the collected results. 
    * 
    * @return Set of AssocRole objects referring to value via other
    */
   public AssocRoleSet filterOther(Object value)
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
      
      AssocRoleSet answer = new AssocRoleSet();
      
      for (AssocRole obj : this)
      {
         if (neighbors.contains(obj.getOther()) || (neighbors.isEmpty() && obj.getOther() == null))
         {
            answer.add(obj);
         }
      }
      
      return answer;
   }

   /**
    * Follow other reference zero or more times and collect all reachable objects. Detect cycles and deal with them. 
    * 
    * @return Set of AssocRole objects reachable via other transitively (including the start set)
    */
   public AssocRoleSet getOtherTransitive()
   {
      AssocRoleSet todo = new AssocRoleSet().with(this);
      
      AssocRoleSet result = new AssocRoleSet();
      
      while ( ! todo.isEmpty())
      {
         AssocRole current = todo.first();
         
         todo.remove(current);
         
         if ( ! result.contains(current))
         {
            result.add(current);
            
            if ( ! result.contains(current.getOther()))
            {
               todo.with(current.getOther());
            }
         }
      }
      
      return result;
   }

   /**
    * Loop through current set of ModelType objects and attach the AssocRole object passed as parameter to the Other attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now with the new neighbor attached to their Other attributes.
    */
   public AssocRoleSet withOther(AssocRole value)
   {
      for (AssocRole obj : this)
      {
         obj.withOther(value);
      }
      
      return this;
   }



   public AssocRolePO createAssocRolePO()
   {
      return new AssocRolePO(this.toArray(new AssocRole[this.size()]));
   }

   /**
    * Loop through the current set of AssocRole objects and collect a list of the roleType attribute values. 
    * 
    * @return List of String objects reachable via roleType attribute
    */
   public ObjectSet getRoleType()
   {
      ObjectSet result = new ObjectSet();
      
      for (AssocRole obj : this)
      {
         result.add(obj.getRoleType());
      }
      
      return result;
   }


   /**
    * Loop through the current set of AssocRole objects and collect those AssocRole objects where the roleType attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of AssocRole objects that match the parameter
    */
   public AssocRoleSet createRoleTypeCondition(String value)
   {
      AssocRoleSet result = new AssocRoleSet();
      
      for (AssocRole obj : this)
      {
         if (value.equals(obj.getRoleType()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of AssocRole objects and collect those AssocRole objects where the roleType attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of AssocRole objects that match the parameter
    */
   public AssocRoleSet createRoleTypeCondition(String lower, String upper)
   {
      AssocRoleSet result = new AssocRoleSet();
      
      for (AssocRole obj : this)
      {
         if (lower.compareTo(obj.getRoleType()) <= 0 && obj.getRoleType().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of AssocRole objects and assign value to the roleType attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of AssocRole objects now with new attribute values.
    */
   public AssocRoleSet withRoleType(String value)
   {
      for (AssocRole obj : this)
      {
         obj.setRoleType(value);
      }
      
      return this;
   }

}
