package org.fulib.classmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFragmentMap
{
   // =============== Constants ===============

   public static final String CLASS       = "class";
   public static final String PACKAGE     = "package";
   public static final String CONSTRUCTOR = "constructor";
   public static final String ATTRIBUTE   = "attribute";
   public static final String METHOD      = "method";
   public static final String IMPORT      = "import";
   public static final String CLASS_BODY  = "classBody";
   public static final String CLASS_END   = "classEnd";
   public static final String GAP         = "gap:";

   public static final int PACKAGE_NEWLINES     = 2;
   public static final int IMPORT_NEWLINES      = 1;
   public static final int CLASS_NEWLINES       = 2;
   public static final int FIELD_NEWLINES       = 2;
   public static final int CONSTRUCTOR_NEWLINES = 2;
   public static final int METHOD_NEWLINES      = 2;

   public static final String PROPERTY_fileName = "fileName";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners = null;

   private String fileName;

   private ArrayList<CodeFragment> fragmentList = new ArrayList<>();

   private LinkedHashMap<String, CodeFragment> codeMap = new LinkedHashMap<>();

   // =============== Constructors ===============

   public FileFragmentMap()
   {
      CodeFragment startFragment = new CodeFragment().setKey("start:").setText("");
      this.fragmentList.add(startFragment);
   }

   public FileFragmentMap(String fileName)
   {
      this.setFileName(fileName);
   }

   // =============== Properties ===============

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
      this.firePropertyChange(PROPERTY_fileName, oldValue, value);
      return this;
   }

   public List<CodeFragment> getFragments()
   {
      return Collections.unmodifiableList(this.fragmentList);
   }

   @Deprecated
   public ArrayList<CodeFragment> getFragmentList()
   {
      return this.fragmentList;
   }

   public CodeFragment getFragment(String key)
   {
      return this.codeMap.get(key);
   }

   public boolean isClassBodyEmpty()
   {
      final CodeFragment startFragment = this.codeMap.get(CLASS);
      final CodeFragment endFragment = this.codeMap.get(CLASS_END);

      if (startFragment == null || endFragment == null)
      {
         return true;
      }

      final int startPos = this.fragmentList.indexOf(startFragment) + 1;
      final int endPos = this.fragmentList.lastIndexOf(endFragment);

      for (int i = startPos; i < endPos; i++)
      {
         CodeFragment fragment = this.fragmentList.get(i);
         if (!Objects.equals(fragment.getKey(), GAP))
         {
            return false;
         }
      }

      return true;
   }

   @Deprecated
   @SuppressWarnings("unused")
   public boolean classBodyIsEmpty(FileFragmentMap fragmentMap)
   {
      return this.isClassBodyEmpty();
   }

   // =============== Static Methods ===============

   // package-private for testability
   static String mergeClassDecl(String oldText, String newText)
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

   // package-private for testability
   // TODO test
   static String mergeAttributeDecl(String oldText, String newText)
   {
      // keep everything before public
      final int oldPublicPos = oldText.indexOf("public");
      final int newPublicPos = newText.indexOf("public");
      if (oldPublicPos >= 0 && newPublicPos >= 0)
      {
         return oldText.substring(0, oldPublicPos) + newText.substring(newPublicPos);
      }

      // keep everything before private
      final int newPrivatePos = newText.indexOf("private");
      final int oldPrivatePos = oldText.indexOf("private");
      if (oldPrivatePos >= 0 && newPrivatePos >= 0)
      {
         return oldText.substring(0, oldPrivatePos) + newText.substring(newPrivatePos);
      }

      return newText;
   }

   // =============== Methods ===============

   // --------------- Raw Modification ---------------

   public void add(CodeFragment fragment)
   {
      this.fragmentList.add(fragment);
      this.codeMap.put(fragment.getKey(), fragment);
   }

   public void remove(CodeFragment fragment)
   {
      final int pos = this.fragmentList.indexOf(fragment);
      if (pos < 0)
      {
         return;
      }

      this.fragmentList.remove(pos);
      this.codeMap.remove(fragment.getKey());

      final CodeFragment gap = this.fragmentList.get(pos - 1);
      if (Objects.equals(gap.getKey(), GAP))
      {
         this.fragmentList.remove(pos - 1);
      }
   }

   // --------------- Smart Modification ---------------

   /**
    * Adds or replaces the fragment with the given key, UNLESS it is marked as user-defined.
    * When no old fragment exists, a gap with the specified number of newlines is added after the new fragment.
    *
    * @param key
    *    the key
    * @param newText
    *    the new text
    * @param newLines
    *    the number of line breaks to insert after the text
    *
    * @return the old fragment, or {@code null} if not found
    */
   public CodeFragment add(String key, String newText, int newLines)
   {
      return this.replace(key, newText, newLines);
   }

   /**
    * Removes the fragment with the given key, UNLESS it is marked as user-defined.
    *
    * @param key
    *    the key
    *
    * @return the old fragment, or {@code null} if not found
    *
    * @since 1.2
    */
   public CodeFragment remove(String key)
   {
      return this.replace(key, null, 0);
   }

   /**
    * Behaves like {@link #add(String, String, int)} when removeFragment is true,
    * and like {@link #remove(String)} otherwise.
    *
    * @param key
    *    the key
    * @param newText
    *    the new text (ignored if removeFragment is true)
    * @param newLines
    *    the number of line breaks to insert after the text (ignored if removeFragment is true)
    * @param removeFragment
    *    whether to remove the fragment associated with the key
    *
    * @return the old fragment, or {@code null} if not found
    *
    * @see #add(String, String, int)
    * @see #remove(String)
    * @deprecated since 1.2; use {@link #add(String, String, int)} or {@link #remove(String)} instead
    */
   @Deprecated
   public CodeFragment add(String key, String newText, int newLines, boolean removeFragment)
   {
      return this.replace(key, removeFragment ? null : newText, newLines);
   }

   private CodeFragment replace(String key, String newText, int newLines)
   {
      CodeFragment old = this.codeMap.get(key);
      if (old == null)
      {
         if (newText == null)
         {
            return null;
         }

         return this.addNew(key, newText, newLines);
      }

      // TODO this also inspects method bodies. Perhaps we don't want that?
      final String oldText = old.getText();
      if (oldText.contains("// no"))
      {
         // do not overwrite
         return old;
      }

      if (newText == null)
      {
         this.remove(old);
         return old;
      }

      // keep annotations and modifiers
      if (newText.contains("@"))
      {
         // newtext contains annotations, thus it overrides annotations in the code
         // do not modify newtext
      }
      else if (key.equals(CLASS))
      {
         newText = mergeClassDecl(oldText, newText);
      }
      else if (key.startsWith(ATTRIBUTE))
      {
         newText = mergeAttributeDecl(oldText, newText);
      }

      old.setText(newText.trim());

      return old;
   }

   private CodeFragment addNew(String key, String newText, int newLines)
   {
      final CodeFragment gap = this.getNewLineGapFragment(newLines);
      final CodeFragment result = new CodeFragment().setKey(key).setText(newText);
      this.codeMap.put(key, result);

      if (key.startsWith(ATTRIBUTE) || key.startsWith(METHOD) || key.startsWith(CONSTRUCTOR))
      {
         this.add(result, CLASS_END);
         this.add(gap, CLASS_END);

         return result;
      }

      if (key.startsWith(IMPORT))
      {
         this.add(result, CLASS);
         this.add(gap, CLASS);

         return result;
      }

      this.add(result);
      this.add(gap);

      return result;
   }

   private CodeFragment getNewLineGapFragment(int newLines)
   {
      CodeFragment gap = new CodeFragment().setKey("gap:");

      StringBuilder text = new StringBuilder();
      for (int i = 0; i < newLines; i++)
      {
         text.append("\n");
      }

      gap.setText(text.toString());
      return gap;
   }

   private void add(CodeFragment result, String posKey)
   {
      final CodeFragment oldFragment = this.codeMap.get(posKey);
      final int pos;
      if (oldFragment == null || (pos = this.fragmentList.indexOf(oldFragment)) < 0)
      {
         this.fragmentList.add(result);
      }
      else
      {
         this.fragmentList.add(pos, result);
      }
   }

   // --------------- Post-Processing ---------------

   public void compressBlankLines()
   {
      int noOfBlankLines = 0;

      for (CodeFragment firstFragment : this.fragmentList)
      {
         if (!firstFragment.getText().matches("\\s*"))
         {
            noOfBlankLines = 0;
            continue;
         }

         for (int pos = firstFragment.getText().length() - 1; pos >= 0; pos--)
         {
            if (firstFragment.getText().charAt(pos) != '\n')
            {
               continue;
            }

            noOfBlankLines++;
            if (noOfBlankLines == 2)
            {
               firstFragment.setText(firstFragment.getText().substring(pos));
               break;
            }
            if (noOfBlankLines > 2)
            {
               firstFragment.setText(firstFragment.getText().substring(pos + 1));
               break;
            }
         }
      }
   }

   // --------------- Output ---------------

   public void writeFile()
   {
      final Path path = Paths.get(this.fileName);
      try
      {
         Files.createDirectories(path.getParent());

         try (final Writer writer = Files
            .newBufferedWriter(path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING))
         {
            this.write(writer);
         }
      }
      catch (IOException e)
      {
         // TODO better error handling
         e.printStackTrace();
      }
   }

   public void write(Writer writer) throws IOException
   {
      for (final CodeFragment fragment : this.fragmentList)
      {
         writer.write(fragment.getText());
      }
   }

   // --------------- Property Change Support ---------------

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

   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   // --------------- Misc. ---------------

   public void removeYou()
   {
   }

   @Override // no fulib
   public String toString()
   {
      final StringBuilder result = new StringBuilder();

      result.append(this.getFileName()).append('\n');
      for (final CodeFragment fragment : this.fragmentList)
      {
         result.append(fragment.getText());
      }

      return result.toString();
   }
}
