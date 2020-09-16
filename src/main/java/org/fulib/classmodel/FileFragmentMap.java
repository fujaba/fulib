package org.fulib.classmodel;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.fulib.parser.FulibClassLexer;
import org.fulib.parser.FulibClassParser;

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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Objects;

public class FileFragmentMap
{
   // =============== Constants ===============

   // @formatter:off
   /** @since 1.2 */ public static final String CLASS = "class";
   /** @since 1.2 */ public static final String PACKAGE = "package";
   /** @since 1.2 */ public static final String CONSTRUCTOR = "constructor";
   /** @since 1.2 */ public static final String ATTRIBUTE = "attribute";
   /** @since 1.3 */ public static final String STATIC_ATTRIBUTE = "staticAttribute";
   /** @since 1.2 */ public static final String METHOD = "method";
   /** @since 1.3 */ public static final String PROPERTY = "property";
   /** @since 1.2 */ public static final String IMPORT = "import";
   /** @since 1.2 */ public static final String CLASS_BODY = "classBody";
   /** @since 1.2 */ public static final String CLASS_DECL = "classDecl";
   /** @since 1.2 */ public static final String CLASS_END = "classEnd";
   /** @since 1.2 */ public static final String EOF = "eof";

   /** @since 1.2 */ public static final int PACKAGE_NEWLINES = 0;
   /** @since 1.2 */ public static final int IMPORT_NEWLINES = 1;
   /** @since 1.2 */ public static final int CLASS_NEWLINES = 2;
   /** @since 1.2 */ public static final int FIELD_NEWLINES = 1;
   /** @since 1.2 */ public static final int CONSTRUCTOR_NEWLINES = 2;
   /** @since 1.2 */ public static final int METHOD_NEWLINES = 2;
   /** @since 1.2 */ public static final int CLASS_END_NEWLINES = 1;
   // @formatter:on

   public static final String PROPERTY_fileName = "fileName";

   /** @since 1.3 */
   public static final String PROPERTY_FILE_NAME = "fileName";

   // comments containing "no fulib", case insensitive, and with any whitespace between the words.
   private static final Pattern NO_FULIB_PATTERN = Pattern.compile("//.*no\\s+fulib|/\\*.*no\\s+fulib.*\\*/",
                                                                   Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

   private static final Pattern CLASS_DECL_PATTERN = Pattern.compile("^" + CLASS + "/(\\w+)/" + CLASS_DECL + "$");
   private static final Pattern CLASS_END_PATTERN = Pattern.compile("^" + CLASS + "/(\\w+)/" + CLASS_END + "$");
   private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile(
      "^" + CLASS + "/(\\w+)/(" + ATTRIBUTE + "|" + STATIC_ATTRIBUTE + ")/(\\w+)$");

   private static final String GAP_BEFORE = "#gap-before";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private String fileName;

   private final CompoundFragment root;

   // =============== Constructors ===============

   public FileFragmentMap()
   {
      this.root = new CompoundFragment();
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
      this.firePropertyChange(PROPERTY_FILE_NAME, oldValue, value);
      return this;
   }

   /**
    * @return a stream of all code fragments containing text
    *
    * @since 1.2
    */
   public Stream<CodeFragment> codeFragments()
   {
      return codeFragments(this.root);
   }

   /**
    * @return a list of all code fragments containing text
    *
    * @deprecated since 1.2; use {@link #codeFragments()} instead
    */
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

   /**
    * @param key
    *    the key to search for
    *
    * @return the code fragment with the given key,
    * or {@code null} if not found or the result is not a {@link CodeFragment} (e.g. a {@link CompoundFragment})
    */
   public CodeFragment getFragment(String key)
   {
      return findFragment(this.root, getParentKeys(key), 0, key);
   }

   private static CodeFragment findFragment(CompoundFragment parent, String[] parentKeys, int index, String key)
   {
      if (index == parentKeys.length)
      {
         final Fragment child = parent.getChildWithKey(key);
         return child instanceof CodeFragment ? (CodeFragment) child : null;
      }

      for (final Fragment child : parent.getChildren())
      {
         if (!(child instanceof CompoundFragment) || !child.getKey().equals(parentKeys[index]))
         {
            continue;
         }

         final CodeFragment fragment = findFragment((CompoundFragment) child, parentKeys, index + 1, key);
         if (fragment != null)
         {
            return fragment;
         }
      }
      return null;
   }

   static String[] getPath(String key)
   {
      return key.split("/");
   }

   static String[] getParentKeys(String fullKey)
   {
      final List<String> result = new ArrayList<>();
      for (int i = fullKey.indexOf('/'); 0 <= i && i < fullKey.length(); i = fullKey.indexOf('/', i + 1))
      {
         result.add(fullKey.substring(0, i));
      }
      return result.toArray(new String[0]);
   }

   /**
    * @return {@code false} if this file contains a class declaration whose body has at least one member, {@code true} otherwise
    *
    * @since 1.2
    */
   public boolean isClassBodyEmpty()
   {
      boolean inClassBody = false;

      for (final Iterator<CodeFragment> it = this.codeFragments().iterator(); it.hasNext(); )
      {
         final CodeFragment fragment = it.next();
         final String key = fragment.getKey();
         if (CLASS_DECL_PATTERN.matcher(key).matches())
         {
            inClassBody = true;
         }
         else if (CLASS_END_PATTERN.matcher(key).matches())
         {
            inClassBody = false;
         }
         else if (inClassBody && !isEmptyFragment(fragment))
         {
            return false;
         }
      }

      return true;
   }

   private static boolean isEmptyFragment(CodeFragment fragment)
   {
      return fragment.getText().isEmpty() || fragment.getKey().endsWith(GAP_BEFORE);
   }

   /**
    * @param fragmentMap
    *    unused
    *
    * @return see {@link #isClassBodyEmpty()}
    *
    * @deprecated since 1.2; use {@link #isClassBodyEmpty()} instead
    */
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
      // really the only information newText may contain in the current setup is the extends clause
      // - the class name is part of the key and will always be identical
      // - the visibility is always public and fulib does not allow other modifiers
      // - fulib does not allow interfaces
      // - fulib does not allow any comments <- TODO this may need to be updated when implementing Javadoc descriptions
      final int extendsIndex = newText.indexOf("extends");

      final CharStream input = CharStreams.fromString(oldText + "}");
      final FulibClassLexer lexer = new FulibClassLexer(input);
      final FulibClassParser parser = new FulibClassParser(new CommonTokenStream(lexer));
      final FulibClassParser.ClassDeclContext classDecl = parser.classDecl();
      final FulibClassParser.ClassMemberContext classMember = classDecl.classMember();

      if (extendsIndex < 0)
      {
         if (classMember.EXTENDS() == null)
         {
            return oldText;
         }

         // delete extends clause from oldText
         final int startIndex = classMember.EXTENDS().getSymbol().getStartIndex();
         final int endIndex = classMember.IMPLEMENTS() != null ?
            classMember.IMPLEMENTS().getSymbol().getStartIndex() :
            classMember.classBody().getStart().getStartIndex();
         return new StringBuilder(oldText).delete(startIndex, endIndex).toString();
      }

      final String superType = newText.substring(extendsIndex + "extends".length(), newText.lastIndexOf('{')).trim();

      if (classMember.EXTENDS() == null)
      {
         // insert extends clause
         final int insertIndex = classMember.IMPLEMENTS() != null ?
            classMember.IMPLEMENTS().getSymbol().getStartIndex() :
            classMember.classBody().getStart().getStartIndex();
         return new StringBuilder(oldText).insert(insertIndex, "extends " + superType + " ").toString();
      }

      // replace super type
      final int startIndex = classMember.extendsTypes.getStart().getStartIndex();
      final int endIndex = classMember.extendsTypes.getStop().getStopIndex() + 1;
      return new StringBuilder(oldText).replace(startIndex, endIndex, superType).toString();
   }

   // package-private for testability
   // TODO test
   static String mergeAttributeDecl(String oldText, String newText)
   {
      final FulibClassParser.FieldContext oldField = parseField(oldText);
      final FulibClassParser.FieldMemberContext oldFieldMember = oldField.fieldMember();

      if (oldFieldMember.fieldNamePart().size() != 1)
      {
         // oldText is of the form 'int x, y;' or similar - merging that is too complicated
         return oldText;
      }

      final FulibClassParser.FieldNamePartContext oldFieldPart = oldFieldMember.fieldNamePart(0);

      final FulibClassParser.FieldContext newField = parseField(newText);
      final FulibClassParser.FieldMemberContext newFieldMember = newField.fieldMember();
      final FulibClassParser.FieldNamePartContext newFieldPart = newFieldMember.fieldNamePart(0);

      // newText provides the following information:
      // - type
      // - (name) - this is part of the key and will always be identical
      // - initializer (optional)
      // changes need to be performed from right to left so indices are not messed up

      final StringBuilder builder = new StringBuilder(oldText);
      if (newFieldPart.EQ() == null)
      {
         if (oldFieldPart.EQ() != null)
         {
            // delete everything between the attribute name and the semicolon
            final int start = oldFieldPart.IDENTIFIER().getSymbol().getStopIndex() + 1;
            final int stop = oldFieldMember.SEMI().getSymbol().getStartIndex();
            builder.delete(start, stop);
         }
      }
      else
      {
         final FulibClassParser.ExprContext newExpr = newFieldPart.expr();
         final String newExprText = newText.substring(newExpr.getStart().getStartIndex(),
                                                      newExpr.getStop().getStopIndex() + 1);

         if (oldFieldPart.EQ() != null)
         {
            // replace expr in oldText
            final FulibClassParser.ExprContext oldExpr = oldFieldPart.expr();
            final int start = oldExpr.getStart().getStartIndex();
            final int stop = oldExpr.getStop().getStopIndex() + 1;
            builder.replace(start, stop, newExprText);
         }
         else
         {
            final int insertIndex = oldFieldMember.SEMI().getSymbol().getStartIndex();
            builder.insert(insertIndex, " = " + newExprText);
         }
      }

      final List<FulibClassParser.ArraySuffixContext> arraySuffixes = oldFieldPart.arraySuffix();
      if (!arraySuffixes.isEmpty())
      {
         // delete array suffixes - they can mess with type replacement
         final int start = arraySuffixes.get(0).getStart().getStartIndex();
         final int end = arraySuffixes.get(arraySuffixes.size() - 1).getStop().getStopIndex() + 1;
         builder.delete(start, end);
      }

      // replace old type with new
      final FulibClassParser.TypeContext newType = newFieldMember.type();
      final String newTypeText = newText.substring(newType.getStart().getStartIndex(),
                                                   newType.getStop().getStopIndex() + 1);
      final FulibClassParser.TypeContext oldType = oldFieldMember.type();
      final int start = oldType.getStart().getStartIndex();
      final int stop = oldType.getStop().getStopIndex() + 1;
      builder.replace(start, stop, newTypeText);

      return builder.toString();
   }

   private static FulibClassParser.FieldContext parseField(String newText)
   {
      final CharStream newInput = CharStreams.fromString(newText);
      final FulibClassLexer newLexer = new FulibClassLexer(newInput);
      final FulibClassParser newParser = new FulibClassParser(new CommonTokenStream(newLexer));
      return newParser.field();
   }

   // =============== Methods ===============

   // --------------- Raw Modification ---------------

   /**
    * Behaves like {@link #append(CodeFragment)}.
    *
    * @param fragment
    *    the fragment to add
    *
    * @see #append(CodeFragment)
    * @deprecated since 1.2; this method is ambiguous.
    * Use {@link #append(CodeFragment)} to append the fragment to the end of the file,
    * or {@link #insert(CodeFragment)} to insert it grouping by key.
    */
   @Deprecated
   public void add(CodeFragment fragment)
   {
      this.append(fragment);
   }

   /**
    * Appends the code fragment to the end, without grouping like {@link #insert(CodeFragment)}.
    * E.g., a fragment with key {@code class/Foo/attribute/moo} will be appended to the following tree as highlighted.
    *
    * <pre>{@code
    * class
    *    Foo
    *       attribute
    *          bar
    *       method
    *          getBar()
    *          setBar(String)
    *       *attribute*
    *          *moo*
    * }</pre>
    * <p>
    * Parent nodes are automatically inserted.
    * Note how the {@code class/Foo/attribute} subtree was added a second time in order to maintain member order.
    * This makes it possible to add duplicate members, which may be unwanted after initial code loading and during
    * member generation.
    *
    * @param fragment
    *    the fragment to append
    *
    * @throws IllegalStateException
    *    if a fragment with the key already exists - the
    *    {@link #replace(String, String, int)} API is designed for this case.
    * @see #insert(CodeFragment)
    * @since 1.2
    */
   public void append(CodeFragment fragment)
   {
      final String fullKey = fragment.getKey();
      CompoundFragment next = this.root;

      for (final String subKey : getParentKeys(fullKey))
      {
         final List<Fragment> children = next.getChildren();

         if (children.isEmpty())
         {
            next = new CompoundFragment().setKey(subKey).setParent(next);
            continue;
         }

         final Fragment lastChild = children.get(children.size() - 1);
         if (!subKey.equals(lastChild.getKey()))
         {
            next = new CompoundFragment().setKey(subKey).setParent(next);
            continue;
         }

         if (lastChild instanceof CompoundFragment)
         {
            next = (CompoundFragment) lastChild;
            continue;
         }

         throw new IllegalStateException(
            String.format("cannot add child '%s' as '%s' is not a compound fragment", fullKey, subKey));
      }

      next.withChildren(fragment);
   }

   /**
    * Inserts the code fragment grouping by the key.
    * E.g., a fragment with key {@code class/Foo/attribute/moo} will be inserted in the following tree as highlighted.
    *
    * <pre>{@code
    * class
    *    Foo
    *       attribute
    *          bar
    *          *moo*
    *       method
    *          getBar()
    *          setBar(String)
    * }</pre>
    * <p>
    * Parent nodes are automatically inserted.
    * In this case, if the {@code class/Foo/attribute} subtree was missing, it would have been automatically inserted.
    * The insertion point is always the last child of an existing parent.
    *
    * <pre>{@code
    * class
    *    Foo
    *       method
    *          getBar()
    *          setBar(String)
    *       *attribute*
    *          *moo*
    * }</pre>
    *
    * @param fragment
    *    the fragment to insert
    *
    * @throws IllegalStateException
    *    if one of the parent keys denotes a text fragment instead of a compound fragment
    *    (for example, if the key was {@code class/Foo/attribute/bar/baz} - the parent key
    *    {@code class/Foo/attribute/bar} refers to the text fragment holding the attribute 'bar')
    * @see #append(CodeFragment)
    * @since 1.2
    */
   public void insert(CodeFragment fragment)
   {
      CompoundFragment parent = this.root;
      final String fullKey = fragment.getKey();
      for (final String subKey : getParentKeys(fullKey))
      {
         final Fragment next = parent.getChildWithKey(subKey);
         if (next == null)
         {
            parent = new CompoundFragment().setKey(subKey).setParent(parent);
         }
         else if (next instanceof CompoundFragment)
         {
            parent = (CompoundFragment) next;
         }
         else
         {
            throw new IllegalStateException(
               String.format("cannot add child '%s' as '%s' is not a compound fragment", fullKey, subKey));
         }
      }

      parent.withChildren(fragment);
   }

   /**
    * Removes the fragment by removing it from it's {@linkplain Fragment#getParent() parent}'s
    * {@linkplain CompoundFragment#withoutChildren(Fragment) children}.
    *
    * @param fragment
    *    the fragment to remove
    *
    * @since 1.2
    */
   public void remove(CodeFragment fragment)
   {
      final CompoundFragment parent = fragment.getParent();
      if (parent == null)
      {
         return;
      }

      final Fragment gapBefore = parent.getChildWithKey(fragment.getKey() + GAP_BEFORE);
      if (gapBefore != null)
      {
         parent.withoutChildren(gapBefore);
      }

      parent.withoutChildren(fragment);
   }

   // --------------- Smart Modification ---------------

   /**
    * Adds or replaces the fragment with the given key.
    * If no fragment with the given key exists, a gap with the specified number of line breaks is added before the new
    * fragment.
    * If the fragment exists and is marked as user-defined (contains a comment with the words "no fulib",
    * case-insensitive and separated by any string of whitespace), no action is performed.
    * Otherwise, the text of the fragment is replaced with the new text and no newlines are added.
    *
    * @param key
    *    the key
    * @param newText
    *    the new text
    * @param newLines
    *    the number of line breaks to insert before the text
    *
    * @return the fragment with the given key, or {@code null} if not found
    */
   public CodeFragment add(String key, String newText, int newLines)
   {
      return this.replace(key, newText, newLines);
   }

   /**
    * Removes the fragment with the given key, unless it is marked as user-defined (contains a comment with the words
    * "no fulib", case-insensitive and separated by any string of whitespace).
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
    * Behaves like {@link #add(String, String, int)} when {@code removeFragment} is {@code false},
    * and like {@link #remove(String)} otherwise.
    *
    * @param key
    *    the key
    * @param newText
    *    the new text (ignored if {@code removeFragment} is {@code true})
    * @param newLines
    *    the number of line breaks to insert before the text (ignored if {@code removeFragment} is {@code true})
    * @param removeFragment
    *    whether to remove or add/replace the fragment with the given key
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

      final String oldText = old.getText();
      if (NO_FULIB_PATTERN.matcher(oldText).find())
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
      else if (CLASS_DECL_PATTERN.matcher(key).matches())
      {
         newText = mergeClassDecl(oldText, newText);
      }
      else if (ATTRIBUTE_PATTERN.matcher(key).matches())
      {
         newText = mergeAttributeDecl(oldText, newText);
      }

      old.setText(newText.trim());

      return old;
   }

   private CodeFragment addNew(String key, String newText, int newLines)
   {

      // we need to add the leading whitespace of the newText to the gap and trim newText.
      // this is to prevent a problem where a fragment was inserted with leading whitespace in the first generation
      // phase, and later replaced without whitespace (replacing always does trim()).
      // the effect was that new members were not indented correctly.

      final int start = trimStartIndex(newText);
      final String trimmedText = newText.trim();

      final StringBuilder gapBuilder = new StringBuilder(newLines + start);
      for (int i = 0; i < newLines; i++)
      {
         gapBuilder.append('\n');
      }
      gapBuilder.append(newText, 0, start);

      final String gapText = gapBuilder.toString();

      final CodeFragment gap = new CodeFragment().setKey(key + GAP_BEFORE).setText(gapText);
      final CodeFragment result = new CodeFragment().setKey(key).setText(trimmedText);

      this.insert(gap);
      this.insert(result);

      return result;
   }

   private static int trimStartIndex(String str)
   {
      // adapted from String.trim()
      int start = 0;
      while (start < str.length() && str.charAt(start) <= ' ')
      {
         start++;
      }
      return start;
   }

   // --------------- Post-Processing ---------------

   /**
    * Modifies all code fragments so there are no more than 2 consecutive line breaks anywhere in the resulting text.
    *
    * @since 1.2
    */
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

   /**
    * Concatenates all code fragments and writes the resulting text to the file specified by {@link #getFileName()}.
    */
   public void writeFile()
   {
      final Path path = Paths.get(this.fileName);
      try
      {
         Files.createDirectories(path.getParent());

         try (final Writer writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE,
                                                            StandardOpenOption.TRUNCATE_EXISTING))
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

   /**
    * Concatenates all code fragments and writes the resulting text to given writer.
    *
    * @param writer
    *    the writer
    *
    * @since 1.2
    */
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
      // no fulib
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
