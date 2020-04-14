package org.fulib.classmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
   public static final String CLASS_DECL  = "classDecl";
   public static final String CLASS_END   = "classEnd";
   public static final String GAP         = "gap:";

   public static final int PACKAGE_NEWLINES     = 2;
   public static final int IMPORT_NEWLINES      = 1;
   public static final int CLASS_NEWLINES       = 2;
   public static final int FIELD_NEWLINES       = 2;
   public static final int CONSTRUCTOR_NEWLINES = 2;
   public static final int METHOD_NEWLINES      = 2;
   public static final int CLASS_END_NEWLINES   = 1;

   public static final String PROPERTY_fileName = "fileName";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private String fileName;

   private CompoundFragment root;

   // =============== Constructors ===============

   public FileFragmentMap()
   {
      this.root = new CompoundFragment();
      this.root.setKey("root");
   }

   public FileFragmentMap(String fileName)
   {
      this();
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

   public Stream<CodeFragment> codeFragments()
   {
      return codeFragments(this.root);
   }

   @Deprecated
   public ArrayList<CodeFragment> getFragmentList()
   {
      return this.codeFragments().collect(Collectors.toCollection(ArrayList::new));
   }

   private static Stream<CodeFragment> codeFragments(Fragment fragment)
   {
      if (fragment instanceof CodeFragment)
      {
         return Stream.of((CodeFragment) fragment);
      }
      else if (fragment instanceof CompoundFragment)
      {
         return ((CompoundFragment) fragment).getChildren().stream().flatMap(FileFragmentMap::codeFragments);
      }
      else
      {
         throw new AssertionError("cannot handle " + fragment.getClass().getName());
      }
   }

   public CodeFragment getFragment(String key)
   {
      final String[] path = this.getPath(key);
      final Fragment ancestor = this.root.getAncestor(path);
      return ancestor instanceof CodeFragment ? (CodeFragment) ancestor : null;
   }

   private String[] getPath(String key)
   {
      return key.split("/");
   }

   public boolean isClassBodyEmpty()
   {
      // TODO inefficient
      final CodeFragment startFragment = this.getFragment(CLASS);
      final CodeFragment endFragment = this.getFragment(CLASS_END);

      if (startFragment == null || endFragment == null)
      {
         return true;
      }

      final List<CodeFragment> fragmentList = this.getFragmentList();

      final int startPos = fragmentList.indexOf(startFragment) + 1;
      final int endPos = fragmentList.lastIndexOf(endFragment);

      for (int i = startPos; i < endPos; i++)
      {
         final CodeFragment fragment = fragmentList.get(i);
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

   /**
    * @see #append(CodeFragment)
    *
    * @deprecated since 1.2; this method is ambiguous.
    * Use {@link #append(CodeFragment)} to add a fragment to the end,
    * or {@link #insert(CodeFragment)} to intelligently insert it in a fitting place.
    */
   @Deprecated
   public void add(CodeFragment fragment)
   {
      this.append(fragment);
   }

   public void append(CodeFragment fragment)
   {
      final String[] path = this.getPath(fragment.getKey());
      CompoundFragment parent = this.root;

      for (int i = 0; i < path.length - 1; i++)
      {
         final String key = path[i];
         final List<Fragment> children = parent.getChildren();
         final Fragment lastChild = children.get(children.size() - 1);

         if (!key.equals(lastChild.getKey()))
         {
            final CompoundFragment newChild = new CompoundFragment();
            newChild.setKey(key);
            parent.withChildren(newChild);
            parent = newChild;
         }
         else if (lastChild instanceof CompoundFragment)
         {
            parent = (CompoundFragment) lastChild;
         }
         else
         {
            throw illegalAppend(path, i);
         }
      }

      parent.withChildren(fragment);
   }

   private static IllegalStateException illegalAppend(String[] path, int index)
   {
      final String fullPath = String.join("/", path);
      final String errorPath = Arrays.stream(path, 0, index + 1).collect(Collectors.joining("/"));
      throw new IllegalStateException(
         String.format("cannot add child '%s' as '%s' is not a compound fragment", fullPath, errorPath));
   }

   public void insert(CodeFragment fragment)
   {
      final String[] path = this.getPath(fragment.getKey());

      CompoundFragment parent = this.root;
      for (int i = 0; i < path.length - 1; i++)
      {
         final String key = path[i];
         final Fragment child = parent.getChild(key);
         if (child == null)
         {
            final CompoundFragment newChild = new CompoundFragment();
            newChild.setKey(key);
            parent.withChildren(newChild);
            parent = newChild;
         }
         else if (child instanceof CompoundFragment)
         {
            parent = (CompoundFragment) child;
         }
         else
         {
            throw illegalAppend(path, i);
         }
      }

      parent.withChildren(fragment);
   }

   public void remove(CodeFragment fragment)
   {
      final CompoundFragment parent = fragment.getParent();
      if (parent == null)
      {
         return;
      }

      final List<Fragment> children = parent.getChildren();
      final int pos = children.indexOf(fragment);
      if (pos < 0)
      {
         return;
      }

      final Fragment gap = children.get(pos - 1);
      if (GAP.equals(gap.getKey()))
      {
         this.root.withoutChildren(gap);
      }

      this.root.withoutChildren(children);
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
      CodeFragment old = this.getFragment(key);
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
      final CodeFragment result = new CodeFragment().setKey(key).setText(newText);
      final String newLinesStr = String.join("", Collections.nCopies(newLines, "\n"));
      final CodeFragment gap = new CodeFragment().setKey(key + "#newLines").setText(newLinesStr);

      this.insert(result);
      this.insert(gap);

      return result;
   }

   // --------------- Post-Processing ---------------

   public void compressBlankLines()
   {
      final AtomicInteger noOfBlankLines = new AtomicInteger();

      this.codeFragments().forEach(firstFragment -> {
         final String text = firstFragment.getText();
         if (!text.matches("\\s*"))
         {
            noOfBlankLines.set(0);
            return;
         }

         for (int pos = text.length() - 1; pos >= 0; pos--)
         {
            if (text.charAt(pos) != '\n')
            {
               continue;
            }

            noOfBlankLines.getAndIncrement();
            if (noOfBlankLines.get() == 2)
            {
               firstFragment.setText(text.substring(pos));
               break;
            }
            if (noOfBlankLines.get() > 2)
            {
               firstFragment.setText(text.substring(pos + 1));
               break;
            }
         }
      });
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
      this.root.write(writer);
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
      final StringWriter result = new StringWriter();

      try
      {
         this.write(result);
      }
      catch (IOException ignored)
      {
      }

      return result.toString();
   }
}
