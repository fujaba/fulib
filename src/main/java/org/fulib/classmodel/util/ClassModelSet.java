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
import org.fulib.classmodel.ClassModel;

import java.util.Collection;
import de.uniks.networkparser.list.ObjectSet;
import java.util.Collections;

import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.util.ClazzSet;

public class ClassModelSet extends SimpleSet<ClassModel>
{
	public Class<?> getTypClass() {
		return ClassModel.class;
	}

   public ClassModelSet()
   {
      // empty
   }

   public ClassModelSet(ClassModel... objects)
   {
      for (ClassModel obj : objects)
      {
         this.add(obj);
      }
   }

   public ClassModelSet(Collection<ClassModel> objects)
   {
      this.addAll(objects);
   }

   public static final ClassModelSet EMPTY_SET = new ClassModelSet().withFlag(ClassModelSet.READONLY);



   public String getEntryType()
   {
      return "org.fulib.classmodel.ClassModel";
   }


   @Override
   public ClassModelSet getNewList(boolean keyValue)
   {
      return new ClassModelSet();
   }


   @SuppressWarnings("unchecked")
   public ClassModelSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<ClassModel>)value);
      }
      else if (value != null)
      {
         this.add((ClassModel) value);
      }
      
      return this;
   }
   
   public ClassModelSet without(ClassModel value)
   {
      this.remove(value);
      return this;
   }


   /**
    * Loop through the current set of ClassModel objects and collect a list of the packageName attribute values. 
    * 
    * @return List of String objects reachable via packageName attribute
    */
   public ObjectSet getPackageName()
   {
      ObjectSet result = new ObjectSet();
      
      for (ClassModel obj : this)
      {
         result.add(obj.getPackageName());
      }
      
      return result;
   }


   /**
    * Loop through the current set of ClassModel objects and collect those ClassModel objects where the packageName attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of ClassModel objects that match the parameter
    */
   public ClassModelSet createPackageNameCondition(String value)
   {
      ClassModelSet result = new ClassModelSet();
      
      for (ClassModel obj : this)
      {
         if (value.equals(obj.getPackageName()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of ClassModel objects and collect those ClassModel objects where the packageName attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of ClassModel objects that match the parameter
    */
   public ClassModelSet createPackageNameCondition(String lower, String upper)
   {
      ClassModelSet result = new ClassModelSet();
      
      for (ClassModel obj : this)
      {
         if (lower.compareTo(obj.getPackageName()) <= 0 && obj.getPackageName().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of ClassModel objects and assign value to the packageName attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of ClassModel objects now with new attribute values.
    */
   public ClassModelSet withPackageName(String value)
   {
      for (ClassModel obj : this)
      {
         obj.setPackageName(value);
      }
      
      return this;
   }

   /**
    * Loop through the current set of ClassModel objects and collect a set of the Clazz objects reached via classes. 
    * 
    * @return Set of Clazz objects reachable via classes
    */
   public ClazzSet getClasses()
   {
      ClazzSet result = new ClazzSet();
      
      for (ClassModel obj : this)
      {
         result.with(obj.getClasses());
      }
      
      return result;
   }

   /**
    * Loop through the current set of ClassModel objects and collect all contained objects with reference classes pointing to the object passed as parameter. 
    * 
    * @param value The object required as classes neighbor of the collected results. 
    * 
    * @return Set of Clazz objects referring to value via classes
    */
   public ClassModelSet filterClasses(Object value)
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
      
      ClassModelSet answer = new ClassModelSet();
      
      for (ClassModel obj : this)
      {
         if ( ! Collections.disjoint(neighbors, obj.getClasses()))
         {
            answer.add(obj);
         }
      }
      
      return answer;
   }

   /**
    * Loop through current set of ModelType objects and attach the ClassModel object passed as parameter to the Classes attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now with the new neighbor attached to their Classes attributes.
    */
   public ClassModelSet withClasses(Clazz value)
   {
      for (ClassModel obj : this)
      {
         obj.withClasses(value);
      }
      
      return this;
   }

   /**
    * Loop through current set of ModelType objects and remove the ClassModel object passed as parameter from the Classes attribute of each of it. 
    * 
    * @param value value    * @return The original set of ModelType objects now without the old neighbor.
    */
   public ClassModelSet withoutClasses(Clazz value)
   {
      for (ClassModel obj : this)
      {
         obj.withoutClasses(value);
      }
      
      return this;
   }


   /**
    * Loop through the current set of ClassModel objects and collect a list of the mainJavaDir attribute values. 
    * 
    * @return List of String objects reachable via mainJavaDir attribute
    */
   public ObjectSet getMainJavaDir()
   {
      ObjectSet result = new ObjectSet();
      
      for (ClassModel obj : this)
      {
         result.add(obj.getMainJavaDir());
      }
      
      return result;
   }


   /**
    * Loop through the current set of ClassModel objects and collect those ClassModel objects where the mainJavaDir attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of ClassModel objects that match the parameter
    */
   public ClassModelSet createMainJavaDirCondition(String value)
   {
      ClassModelSet result = new ClassModelSet();
      
      for (ClassModel obj : this)
      {
         if (value.equals(obj.getMainJavaDir()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of ClassModel objects and collect those ClassModel objects where the mainJavaDir attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of ClassModel objects that match the parameter
    */
   public ClassModelSet createMainJavaDirCondition(String lower, String upper)
   {
      ClassModelSet result = new ClassModelSet();
      
      for (ClassModel obj : this)
      {
         if (lower.compareTo(obj.getMainJavaDir()) <= 0 && obj.getMainJavaDir().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of ClassModel objects and assign value to the mainJavaDir attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of ClassModel objects now with new attribute values.
    */
   public ClassModelSet withMainJavaDir(String value)
   {
      for (ClassModel obj : this)
      {
         obj.setMainJavaDir(value);
      }
      
      return this;
   }


   /**
    * Loop through the current set of ClassModel objects and collect a list of the testJavaDir attribute values. 
    * 
    * @return List of String objects reachable via testJavaDir attribute
    */
   public ObjectSet getTestJavaDir()
   {
      ObjectSet result = new ObjectSet();
      
      for (ClassModel obj : this)
      {
         result.add(obj.getTestJavaDir());
      }
      
      return result;
   }


   /**
    * Loop through the current set of ClassModel objects and collect those ClassModel objects where the testJavaDir attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of ClassModel objects that match the parameter
    */
   public ClassModelSet createTestJavaDirCondition(String value)
   {
      ClassModelSet result = new ClassModelSet();
      
      for (ClassModel obj : this)
      {
         if (value.equals(obj.getTestJavaDir()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of ClassModel objects and collect those ClassModel objects where the testJavaDir attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of ClassModel objects that match the parameter
    */
   public ClassModelSet createTestJavaDirCondition(String lower, String upper)
   {
      ClassModelSet result = new ClassModelSet();
      
      for (ClassModel obj : this)
      {
         if (lower.compareTo(obj.getTestJavaDir()) <= 0 && obj.getTestJavaDir().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of ClassModel objects and assign value to the testJavaDir attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of ClassModel objects now with new attribute values.
    */
   public ClassModelSet withTestJavaDir(String value)
   {
      for (ClassModel obj : this)
      {
         obj.setTestJavaDir(value);
      }
      
      return this;
   }



   public ClassModelPO createClassModelPO()
   {
      return new ClassModelPO(this.toArray(new ClassModel[this.size()]));
   }
}
