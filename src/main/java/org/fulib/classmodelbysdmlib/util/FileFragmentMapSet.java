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
import org.fulib.classmodelbysdmlib.FileFragmentMap;

import java.util.Collection;
import de.uniks.networkparser.list.ObjectSet;

public class FileFragmentMapSet extends SimpleSet<FileFragmentMap>
{
	public Class<?> getTypClass() {
		return FileFragmentMap.class;
	}

   public FileFragmentMapSet()
   {
      // empty
   }

   public FileFragmentMapSet(FileFragmentMap... objects)
   {
      for (FileFragmentMap obj : objects)
      {
         this.add(obj);
      }
   }

   public FileFragmentMapSet(Collection<FileFragmentMap> objects)
   {
      this.addAll(objects);
   }

   public static final FileFragmentMapSet EMPTY_SET = new FileFragmentMapSet().withFlag(FileFragmentMapSet.READONLY);


   public FileFragmentMapPO createFileFragmentMapPO()
   {
      return new FileFragmentMapPO(this.toArray(new FileFragmentMap[this.size()]));
   }


   public String getEntryType()
   {
      return "org.fulib.classmodelbysdmlib.FileFragmentMap";
   }


   @Override
   public FileFragmentMapSet getNewList(boolean keyValue)
   {
      return new FileFragmentMapSet();
   }


   @SuppressWarnings("unchecked")
   public FileFragmentMapSet with(Object value)
   {
      if (value == null)
      {
         return this;
      }
      else if (value instanceof java.util.Collection)
      {
         this.addAll((Collection<FileFragmentMap>)value);
      }
      else if (value != null)
      {
         this.add((FileFragmentMap) value);
      }
      
      return this;
   }
   
   public FileFragmentMapSet without(FileFragmentMap value)
   {
      this.remove(value);
      return this;
   }


   /**
    * Loop through the current set of FileFragmentMap objects and collect a list of the fileName attribute values. 
    * 
    * @return List of String objects reachable via fileName attribute
    */
   public ObjectSet getFileName()
   {
      ObjectSet result = new ObjectSet();
      
      for (FileFragmentMap obj : this)
      {
         result.add(obj.getFileName());
      }
      
      return result;
   }


   /**
    * Loop through the current set of FileFragmentMap objects and collect those FileFragmentMap objects where the fileName attribute matches the parameter value. 
    * 
    * @param value Search value
    * 
    * @return Subset of FileFragmentMap objects that match the parameter
    */
   public FileFragmentMapSet createFileNameCondition(String value)
   {
      FileFragmentMapSet result = new FileFragmentMapSet();
      
      for (FileFragmentMap obj : this)
      {
         if (value.equals(obj.getFileName()))
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of FileFragmentMap objects and collect those FileFragmentMap objects where the fileName attribute is between lower and upper. 
    * 
    * @param lower Lower bound 
    * @param upper Upper bound 
    * 
    * @return Subset of FileFragmentMap objects that match the parameter
    */
   public FileFragmentMapSet createFileNameCondition(String lower, String upper)
   {
      FileFragmentMapSet result = new FileFragmentMapSet();
      
      for (FileFragmentMap obj : this)
      {
         if (lower.compareTo(obj.getFileName()) <= 0 && obj.getFileName().compareTo(upper) <= 0)
         {
            result.add(obj);
         }
      }
      
      return result;
   }


   /**
    * Loop through the current set of FileFragmentMap objects and assign value to the fileName attribute of each of it. 
    * 
    * @param value New attribute value
    * 
    * @return Current set of FileFragmentMap objects now with new attribute values.
    */
   public FileFragmentMapSet withFileName(String value)
   {
      for (FileFragmentMap obj : this)
      {
         obj.setFileName(value);
      }
      
      return this;
   }

}
