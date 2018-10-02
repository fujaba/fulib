package org.fulib.classmodel;

import org.fulib.Parser;
import org.fulib.StrUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Struct;
import java.util.ArrayList;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FileFragmentMap  
{

   private String fileName;

   public String getFileName()
   {
      return fileName;
   }

   public FileFragmentMap setFileName(String value)
   {
      if (value == null ? this.fileName != null : ! value.equals(this.fileName))
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


   public CodeFragment getFragment(String key)
   {
      return codeMap.get(key);
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
      return add(key, newText, newLines, false);
   }

   public CodeFragment add(String key, String newText, int newLines, boolean removeFragment)
   {

      CodeFragment result = codeMap.get(key);

      if (result != null)
      {
         if (result.getText().indexOf("// no") >= 0)
         {
            // do not overwrite
            return result;
         }

         if (removeFragment)
         {
            codeMap.remove(key);
            int pos = fragmentList.indexOf(result);
            fragmentList.remove(pos);
            CodeFragment gap = fragmentList.get(pos - 1);
            if (StrUtil.stringEquals(gap.getKey(), Parser.GAP))
            {
               fragmentList.remove(pos - 1);
            }
            return result;
         }

         // keep annotations and modifiers
         int newTextBracePos = newText.indexOf('{');
         if (newText.indexOf("@") >= 0)
         {
            // newtext contains annotations, thus it overrides annotations in the code
            // do not modify newtext
         }
         else if (key.equals(Parser.CLASS))
         {
            // keep annotations and implements clause "\\s*public\\s+class\\s+(\\w+)(\\.+)\\{"
            Pattern pattern = Pattern.compile("class\\s+(\\w+)\\s*(extends\\s+[^\\s]+)?");
            Matcher match = pattern.matcher(newText);
            boolean b = match.find();
            String className = match.group(1);
            String extendsClause = match.group(2);
            extendsClause = extendsClause == null ? "" : extendsClause + " ";

            int resultClassNamePos = result.getText().indexOf("class " + className);
            if (resultClassNamePos >= 0)
            {
               String prefix = result.getText().substring(0, resultClassNamePos);
               String middle = "class " + className + " " + extendsClause;
               String suffix = " \n{";

               int implementsPos = result.getText().indexOf("implements");
               if (implementsPos >= 0)
               {
                  suffix = " " + result.getText().substring(implementsPos);
               }

               newText = prefix + middle + suffix;
            }
         }
         else if (newTextBracePos >= 0)
         {
            // keep annotations and modifiers and signature up to {
            int resultBracePos = result.getText().indexOf('{');
            if (resultBracePos >= 0)
            {
               newText = result.getText().substring(0, resultBracePos) + newText.substring(newTextBracePos);
            }
         }
         else if (key.startsWith(Parser.ATTRIBUTE))
         {
            // keep everything before private
            int newTextPrivatePos = newText.indexOf("private");
            int resultPrivatePos = result.getText().indexOf("private");
            if (newTextPrivatePos >= 0 && resultPrivatePos >= 0)
            {
               newText = result.getText().substring(0, resultPrivatePos) + newText.substring(newTextPrivatePos);
            }
         }

         result.setText(newText.trim());

         return result;
      }

      result = new CodeFragment().setKey(key).setText(newText);
      codeMap.put(key, result);
      CodeFragment gap = getNewLineGapFragment(newLines);

      if (removeFragment) return result;

      if (key.startsWith(Parser.ATTRIBUTE) || key.startsWith(Parser.METHOD) || key.startsWith(Parser.CONSTRUCTOR))
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

   public boolean classBodyIsEmpty(FileFragmentMap fragmentMap)
   {
      CodeFragment startFragment = codeMap.get(Parser.CLASS);
      CodeFragment endFragment = codeMap.get(Parser.CLASS_END);

      if (startFragment == null || endFragment == null) return true;
      int endPos = fragmentList.indexOf(endFragment);

      for (int i = fragmentList.indexOf(startFragment) + 1; i < endPos; i++)
      {
         CodeFragment fragment = fragmentList.get(i);
         if ( ! StrUtil.stringEquals(fragment.getKey(), Parser.GAP))
         {
            return false;
         }
      }

      return true;
   }
   public void removeYou()
   {
   }

   public static final String PROPERTY_fileName = "fileName";

}