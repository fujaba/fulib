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
   
package org.fulib.classmodel;

import de.uniks.networkparser.interfaces.SendableEntity;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.uniks.networkparser.EntityUtil;
import org.fulib.Parser;

public  class FileFragmentMap implements SendableEntity
{

   
   //==========================================================================
   
   protected PropertyChangeSupport listeners = null;

   public FileFragmentMap(String fileName)
   {
      this.withFileName(fileName);
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null) {
   		listeners.firePropertyChange(propertyName, oldValue, newValue);
   		return true;
   	}
   	return false;
   }
   
   public boolean addPropertyChangeListener(PropertyChangeListener listener) 
   {
   	if (listeners == null) {
   		listeners = new PropertyChangeSupport(this);
   	}
   	listeners.addPropertyChangeListener(listener);
   	return true;
   }
   
   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
   	if (listeners == null) {
   		listeners = new PropertyChangeSupport(this);
   	}
   	listeners.addPropertyChangeListener(propertyName, listener);
   	return true;
   }
   
   public boolean removePropertyChangeListener(PropertyChangeListener listener) {
   	if (listeners != null) {
   		listeners.removePropertyChangeListener(listener);
   	}
   	return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
   	if (listeners != null) {
   		listeners.removePropertyChangeListener(propertyName, listener);
   	}
   	return true;
   }

   
   //==========================================================================
   
   
   public void removeYou()
   {
      firePropertyChange("REMOVE_YOU", this, null);
   }


   private LinkedHashMap<String, CodeFragment> codeMap = new LinkedHashMap<>();
   private ArrayList<CodeFragment> fragmentList = new ArrayList<>();


   //==========================================================================

   public FileFragmentMap()
   {
      CodeFragment startFragment = new CodeFragment().withKey("start:").withText("");
      fragmentList.add(startFragment);
   }

   //==========================================================================
   
   public static final String PROPERTY_FILENAME = "fileName";
   
   private String fileName;

   public String getFileName()
   {
      return this.fileName;
   }
   
   public void setFileName(String value)
   {
      if ( ! EntityUtil.stringEquals(this.fileName, value)) {
      
         String oldValue = this.fileName;
         this.fileName = value;
         this.firePropertyChange(PROPERTY_FILENAME, oldValue, value);
      }
   }
   
   public FileFragmentMap withFileName(String value)
   {
      setFileName(value);
      return this;
   } 


   @Override
   public String toString()
   {
      StringBuilder result = new StringBuilder();

      result.append(" ").append(this.getFileName());

      result.append("\n");

      result.append(getFileText());

      return result.substring(1);
   }

   private String getFileText()
   {
      StringBuilder fileBody = new StringBuilder();

      for(CodeFragment fragment : this.fragmentList)
      {
         fileBody.append(fragment.getText());
      }

      return fileBody.toString();
   }


   public CodeFragment add(String key, String newText, int newLines)
   {
      CodeFragment result = codeMap.get(key);

      if (result != null)
      {
         result.withText(newText.trim());

         return result;
      }

      result = new CodeFragment().withKey(key).withText(newText);
      CodeFragment gap = getNewLineGapFragment(newLines);

      if (key.startsWith(Parser.ATTRIBUTE) || key.startsWith(Parser.METHOD))
      {
         add(result, Parser.CLASS_END);

         add(gap, Parser.CLASS_END);

         return result;
      }

      if (key.startsWith(Parser.IMPORT))
      {
         CodeFragment oldFragment = codeMap.get(Parser.CLASS);
         int pos = fragmentList.indexOf(oldFragment);

         // go to the gap before this
         pos--;

         fragmentList.add(pos, gap);
         pos++;
         fragmentList.add(pos, gap);
         pos++;
         fragmentList.add(pos, result);

         return result;
      }

      add(result);
      add(gap, Parser.CLASS_END);

      return result;
   }

   private CodeFragment getNewLineGapFragment(int newLines)
   {
      CodeFragment gap = new CodeFragment().withKey("gap:");

      String text = "";
      for (int i = 0; i <newLines; i++)
      {
         text += "\n";
      }

      gap.withText(text);
      return gap;
   }

   private void add(CodeFragment result, String posKey)
   {
      CodeFragment oldFragment = codeMap.get(posKey);
      int pos = fragmentList.indexOf(oldFragment);
      if (pos == -1)
      {
         fragmentList.add(result);
      }
      else
      {
         fragmentList.add(pos, result);
      }

      codeMap.put(result.getKey(), result);
   }

   public void add(CodeFragment fragment)
   {
      fragmentList.add(fragment);
      codeMap.put(fragment.getKey(), fragment);
   }

   public void writeFile()
   {
      try
      {
         Path path = Paths.get(this.fileName);
         Files.createDirectories(path.getParent());
         Files.write(path, getFileText().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
