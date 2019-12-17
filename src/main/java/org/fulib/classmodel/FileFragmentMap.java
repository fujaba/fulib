package org.fulib.classmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFragmentMap  
{
   public static final String CLASS       = "class";
   public static final String PACKAGE     = "package";
   public static final String CONSTRUCTOR = "constructor";
   public static final String ATTRIBUTE   = "attribute";
   public static final String METHOD      = "method";
   public static final String IMPORT      = "import";
   public static final String CLASS_BODY  = "classBody";
   public static final String CLASS_END   = "classEnd";
   public static final String GAP         = "gap:";

   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   private LinkedHashMap<String, CodeFragment> codeMap = new LinkedHashMap<>();
   private ArrayList<CodeFragment> fragmentList = new ArrayList<>();

   public ArrayList<CodeFragment> getFragmentList()
   {
      return fragmentList;
   }

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
	         if (Objects.equals(gap.getKey(), GAP))
            {
               fragmentList.remove(pos - 1);
            }
            return result;
         }

         // keep annotations and modifiers
         if (newText.indexOf("@") >= 0)
         {
            // newtext contains annotations, thus it overrides annotations in the code
            // do not modify newtext
         }
         else if (key.equals(CLASS))
         {
            newText = mergeClassDecl(result.getText(), newText);
         }
         else if (key.startsWith(ATTRIBUTE))
         {
            // keep everything before public
            int newTextPublicPos = newText.indexOf("public");
            int resultPublicPos = result.getText().indexOf("public");
            if (newTextPublicPos >= 0 && resultPublicPos >= 0)
            {
               newText = result.getText().substring(0, resultPublicPos) + newText.substring(newTextPublicPos);
            }
         }
         else if (key.startsWith(ATTRIBUTE)) // ToDo: this looks wrong, remove it?
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

      if (removeFragment) return result;

      result = new CodeFragment().setKey(key).setText(newText);
      codeMap.put(key, result);
      CodeFragment gap = getNewLineGapFragment(newLines);


      if (key.startsWith(ATTRIBUTE) || key.startsWith(METHOD) || key.startsWith(CONSTRUCTOR))
      {
         add(result, CLASS_END);

         add(gap, CLASS_END);

         return result;
      }

      if (key.startsWith(IMPORT))
      {
         CodeFragment oldFragment = codeMap.get(CLASS);
         int pos = fragmentList.indexOf(oldFragment);

         // go to the gap before this
         pos--;

         pos = Math.max(0, pos);

         fragmentList.add(pos, gap);
         pos++;
         //         fragmentList.add(pos, gap);
         //         pos++;
         fragmentList.add(pos, result);

         return result;
      }

      add(result);
      add(gap, CLASS_END);

      return result;
   }

   public static String mergeClassDecl(String oldText, String newText)
   {
      // keep annotations and implements clause "\\s*public\\s+class\\s+(\\w+)(\\.+)\\{"
      final Pattern pattern = Pattern.compile("class\\s+(\\w+)\\s*(extends\\s+[^\\s]+)?");
      final Matcher match = pattern.matcher(newText);

      if (!match.find())
      {
         // TODO error?
         return newText;
      }

      final String className = match.group(1);
      final String extendsClause = match.group(2);

      final int oldClassNamePos = oldText.indexOf("class " + className);
      if (oldClassNamePos < 0)
      {
         // TODO error?
         return newText;
      }

      final StringBuilder newTextBuilder = new StringBuilder();

      // prefix
      newTextBuilder.append(oldText, 0, oldClassNamePos);

      // middle
      newTextBuilder.append("class ").append(className);
      if (extendsClause != null)
      {
         newTextBuilder.append(" ").append(extendsClause);
      }

      // suffix
      final int implementsPos = oldText.indexOf("implements");
      if (implementsPos >= 0)
      {
         newTextBuilder.append(" ").append(oldText, implementsPos, oldText.length());
      }
      else
      {
         newTextBuilder.append("\n{");
      }

      return newTextBuilder.toString();
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
      CodeFragment startFragment = codeMap.get(CLASS);
      CodeFragment endFragment = codeMap.get(CLASS_END);

      if (startFragment == null || endFragment == null) return true;
      int endPos = fragmentList.indexOf(endFragment);

      for (int i = fragmentList.indexOf(startFragment) + 1; i < endPos; i++)
      {
         CodeFragment fragment = fragmentList.get(i);
	      if ( !Objects.equals(fragment.getKey(), GAP))
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

   private String fileName;

   public String getFileName()
   {
      return this.fileName;
   }

   public FileFragmentMap setFileName(String value)
   {
      if (Objects.equals(value, this.fileName))
      {
         return this;
      }

      final String oldValue = this.fileName;
      this.fileName = value;
      this.firePropertyChange("fileName", oldValue, value);
      return this;
   }

}
