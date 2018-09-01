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
import org.fulib.classmodel.CodeFragment;
import de.uniks.networkparser.interfaces.Condition;
import java.util.Collection;
import de.uniks.networkparser.list.ObjectSet;

public class CodeFragmentSet extends SimpleSet<CodeFragment>
{
	public Class<?> getTypClass() {
		return CodeFragment.class;
	}

   public CodeFragmentSet()
   {
      // empty
   }

   public CodeFragmentSet(CodeFragment... objects)
   {
      for (CodeFragment obj : objects)
      {
         this.add(obj);
      }
   }

   public CodeFragmentSet(Collection<CodeFragment> objects)
   {
      this.addAll(objects);
   }

   public static final CodeFragmentSet EMPTY_SET = new CodeFragmentSet().withFlag(CodeFragmentSet.READONLY);


   public CodeFragmentPO createCodeFragmentPO()
   {
      return new CodeFragmentPO(this.toArray(new CodeFragment[this.size()]));
   }


   public String getEntryType()
   {
      return "org.fulib.classmodel.CodeFragment";
   }


   @Override
   public CodeFragmentSet getNewList(boolean keyValue)
   {
      return new CodeFragmentSet();
   }


   @SuppressWarnings("unchecked")
   public CodeFragmentSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<CodeFragment>)value);
      }
      else if (value != null)
      {
         this.add((CodeFragment) value);
      }
      
      return this;
   }
   
   public CodeFragmentSet without(CodeFragment value)
   {
      this.remove(value);
      return this;
   }


   /**
    * Loop through the current set of CodeFragment objects and collect a list of the key attribute values. 
    * 
    * @return List of String objects reachable via key attribute
    */
   public ObjectSet getKey()
   {
      ObjectSet result = new ObjectSet();
      
      for (CodeFragment obj : this)
      {
         result.add(obj.getKey());
      }
      
      return result;
   }


   /**
    * Loop through the current set of CodeFragment objects and collect those CodeFragment objects where the key attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of CodeFragment objects that match the parameter
    */
   public CodeFragmentSet createKeyCondition(String value)
   {
      CodeFragmentSet result = new CodeFragmentSet();
      
      for (CodeFragment obj : this)
      {
         if (value.equals(obj.getKey()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of CodeFragment objects and collect those CodeFragment objects where the key attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of CodeFragment objects that match the parameter
    */
   public CodeFragmentSet createKeyCondition(String lower, String upper)
   {
      CodeFragmentSet result = new CodeFragmentSet();
      
      for (CodeFragment obj : this)
      {
         if (lower.compareTo(obj.getKey()) <= 0 && obj.getKey().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of CodeFragment objects and assign value to the key attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of CodeFragment objects now with new attribute values.
    */
   public CodeFragmentSet withKey(String value)
   {
      for (CodeFragment obj : this)
      {
         obj.setKey(value);
      }
      
      return this;
   }


   /**
    * Loop through the current set of CodeFragment objects and collect a list of the text attribute values. 
    * 
    * @return List of String objects reachable via text attribute
    */
   public ObjectSet getText()
   {
      ObjectSet result = new ObjectSet();
      
      for (CodeFragment obj : this)
      {
         result.add(obj.getText());
      }
      
      return result;
   }


   /**
    * Loop through the current set of CodeFragment objects and collect those CodeFragment objects where the text attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of CodeFragment objects that match the parameter
    */
   public CodeFragmentSet createTextCondition(String value)
   {
      CodeFragmentSet result = new CodeFragmentSet();
      
      for (CodeFragment obj : this)
      {
         if (value.equals(obj.getText()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of CodeFragment objects and collect those CodeFragment objects where the text attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of CodeFragment objects that match the parameter
    */
   public CodeFragmentSet createTextCondition(String lower, String upper)
   {
      CodeFragmentSet result = new CodeFragmentSet();
      
      for (CodeFragment obj : this)
      {
         if (lower.compareTo(obj.getText()) <= 0 && obj.getText().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of CodeFragment objects and assign value to the text attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of CodeFragment objects now with new attribute values.
    */
   public CodeFragmentSet withText(String value)
   {
      for (CodeFragment obj : this)
      {
         obj.setText(value);
      }
      
      return this;
   }

}
