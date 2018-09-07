package org.fulib.classmodel;

import org.fulib.Parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;


public class FileFragmentMap
{

   private String fileName;

   public String getFileName()
   {
      return fileName;
   }

   public FileFragmentMap setFileName(String value)
   {
      if ((value == null && this.fileName != null) || ! value.equals(this.fileName))
      {
         String oldValue = this.fileName;
         this.fileName = value;
         firePropertyChange("fileName", oldValue, value);
      }
      return this;
   }


   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null)
      {
         listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }


   private LinkedHashMap<String, CodeFragment> codeMap = new LinkedHashMap<>();
   private ArrayList<CodeFragment> fragmentList = new ArrayList<>();


   //==========================================================================

   public FileFragmentMap()
   {
      CodeFragment startFragment = new CodeFragment().setKey("start:").setText("");
      fragmentList.add(startFragment);
   }

   public FileFragmentMap(String fileName)
   {
      this.setFileName(fileName);
   }




   @Override // no fulib
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
         if (result.getText().contains("// no"))
         {
            // do not overwrite
            return result;
         }

         result.setText(newText.trim());

         return result;
      }

      result = new CodeFragment().setKey(key).setText(newText);
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
      CodeFragment gap = new CodeFragment().setKey("gap:");

      String text = "";
      for (int i = 0; i <newLines; i++)
      {
         text += "\n";
      }

      gap.setText(text);
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