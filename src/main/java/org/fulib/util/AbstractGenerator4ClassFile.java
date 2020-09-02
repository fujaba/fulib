package org.fulib.util;

import org.fulib.Generator;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.fulib.parser.FragmentMapBuilder;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fulib.classmodel.FileFragmentMap.*;

/**
 * @author Adrian Kunz
 * @since 1.2
 */
public abstract class AbstractGenerator4ClassFile
{
   // =============== Constants ===============

   private static final Pattern SIGNATURE_PATTERN = Pattern.compile("^\\s*(\\w+)\\s*:\\s*(.*)\\s*$");
   private static final Pattern IMPORT_PATTERN = Pattern.compile("import\\(((?:static\\s+)?(?:\\w+\\.)*(\\w+))\\)");

   // =============== Fields ===============

   private String customTemplatesFile;

   private Map<String, STGroup> stGroups = new HashMap<>();

   // =============== Properties ===============

   public String getCustomTemplatesFile()
   {
      return this.customTemplatesFile;
   }

   public AbstractGenerator4ClassFile setCustomTemplatesFile(String customTemplatesFile)
   {
      this.customTemplatesFile = customTemplatesFile;
      return this;
   }

   // =============== Methods ===============

   /**
    * @deprecated since 1.2
    */
   @Deprecated
   public void generate(Clazz clazz)
   {
      final String classFileName = this.getSourceFileName(clazz);
      final FileFragmentMap fragmentMap = Files.exists(Paths.get(classFileName)) ?
         FragmentMapBuilder.parse(classFileName) :
         new FileFragmentMap(classFileName);

      this.generate(clazz, fragmentMap);

      if (clazz.getModified() && fragmentMap.isClassBodyEmpty())
      {
         Path path = Paths.get(classFileName);
         try
         {
            Files.deleteIfExists(path);
            Logger.getLogger(Generator.class.getName()).info("\n   deleting empty file " + classFileName);
         }
         catch (IOException e)
         {
            e.printStackTrace();
         }
      }
      else
      {
         fragmentMap.compressBlankLines();
         fragmentMap.writeFile();
      }
   }

   public abstract String getSourceFileName(Clazz clazz);

   /**
    * @since 1.2
    */
   public abstract void generate(Clazz clazz, FileFragmentMap fragmentMap);

   /**
    * @param origFileName
    *    the original template file name
    *
    * @return the loaded ST group
    *
    * @deprecated since 1.2; use {@link #getSTGroup(String)} instead
    */
   @Deprecated
   public STGroup createSTGroup(String origFileName)
   {
      return this.getSTGroup(origFileName);
   }

   public STGroup getSTGroup(String origFileName)
   {
      return this.stGroups.computeIfAbsent(origFileName, this::loadSTGroup);
   }

   private STGroup loadSTGroup(String origFileName)
   {
      STGroup group;
      try
      {
         group = new STGroupFile(this.customTemplatesFile);
         STGroup origGroup = new STGroupFile(origFileName);
         group.importTemplates(origGroup);
      }
      catch (Exception e)
      {
         group = new STGroupFile(origFileName);
      }
      group.registerRenderer(String.class, new StringRenderer());
      return group;
   }

   /**
    * Generates multiple related fragments.
    * Their signatures are extracted from a template themselves, which should have the following format:
    *
    * <pre>{@code
    * signatures(foo, bar) ::= <<
    * fragment1: class/<foo.name>/method/get<bar>
    * fragment2: class/<foo.name>/method/set<bar>
    * >>
    *
    * fragment1(foo, bar) ::= << ... >>
    *
    * fragment2(foo, bar) ::= << ... >>
    * }</pre>
    * <p>
    * In detail:
    *
    * <ul>
    *    <li>All templates are taken from the same ST group, given by {@code group}.</li>
    *    <li>
    *       The template named by {@code signaturesTemplate} lists (separated by line breaks) all the other templates
    *       that are related, along with their signature.
    *       The format is {@code templateName : signature}
    *       (any number of whitespace symbols is allowed around the colon or the line itself).
    *    </li>
    *    <li>
    *       All templates, including the signatures, must have the same number and names of parameters.
    *       This is a consequence of the {@code addTarget} lambda expression, which is supposed to add the arguments,
    *       being applied to all templates.
    *    </li>
    *    <li>
    *       The {@code targetModified} parameter decides whether the fragments will be added or removed from the {@code fragmentMap}.
    *       {@code true} indicates removal, {@code false} addition.
    *       In removal mode, the templates specified by the signatures template will not be rendered at all,
    *       as that is obviously not necessary to remove the fragments.
    *    </li>
    *    <li>
    *       The number of newlines after the fragment is by default {@link FileFragmentMap#METHOD_NEWLINES},
    *       unless the signature contains the string {@code /attribute/},
    *       in which case {@link FileFragmentMap#FIELD_NEWLINES} is used.
    *    </li>
    *    <li>
    *       As an additional feature for convenience, any occurrence of {@code import(foo.bar.Baz)} in the rendered template strings,
    *       where {@code foo.bar.Baz} is a fully qualified class name, will be replaced with {@code Baz},
    *       and an import statement will be added at the top of the fragment map: {@code import foo.bar.Baz;}.
    *       No effort is made to avoid this within string literals or other used-supplied content.
    *       Thus, this feature is exposed to end users of the Fulib API.
    *    </li>
    * </ul>
    *
    * @param fragmentMap
    *    the fragment map to add or remove generated fragments to or from
    * @param group
    *    the ST group holding the templates
    * @param signaturesTemplate
    *    the template holding the signatures
    * @param targetModified
    *    whether or not the member was modified. if true, fragments will be removed instead of added
    * @param addTarget
    *    a lambda expression that adds arguments to both the signatures template and all other templates specified by the signatures
    */
   protected void generateFromSignatures(FileFragmentMap fragmentMap, STGroup group, String signaturesTemplate,
      boolean targetModified, Consumer<? super ST> addTarget)
   {
      final ST signatureST = group.getInstanceOf(signaturesTemplate);
      addTarget.accept(signatureST);
      final String signatureString = signatureST.render();

      for (final String line : signatureString.split("\n"))
      {
         final Matcher matcher = SIGNATURE_PATTERN.matcher(line);
         if (!matcher.matches())
         {
            System.err.println("invalid signature, ignoring: " + line);
            continue;
         }

         final String signature = matcher.group(2);

         if (targetModified)
         {
            fragmentMap.remove(signature);
         }
         else
         {
            final String templateName = matcher.group(1);
            final int newLines = signature.contains("/attribute/") ? FIELD_NEWLINES : METHOD_NEWLINES;
            final ST namedST = group.getInstanceOf(templateName);
            addTarget.accept(namedST);

            final String rendered = namedST.render();
            final String result = replaceImports(rendered, fragmentMap);
            fragmentMap.add(signature, result, newLines);
         }
      }
   }

   private String replaceImports(String template, FileFragmentMap fragmentMap)
   {
      final Matcher matcher = IMPORT_PATTERN.matcher(template);

      // adapted from Matcher.replaceAll
      boolean result = matcher.find();
      if (!result)
      {
         return template;
      }

      final STGroup group = this.getImportGroup();

      final StringBuffer sb = new StringBuffer(template.length());
      do
      {
         final String qualifiedName = matcher.group(1);
         this.addImport(fragmentMap, group, qualifiedName, false);

         matcher.appendReplacement(sb, "$2"); // group 2 = simple name
         result = matcher.find();
      }
      while (result);

      matcher.appendTail(sb);
      return sb.toString();
   }

   protected STGroup getImportGroup()
   {
      return this.getSTGroup("org/fulib/templates/declarations.stg");
   }

   protected void addImport(FileFragmentMap fragmentMap, STGroup group, String qualifiedName, boolean isStatic)
   {
      final ST importDecl = group.getInstanceOf("importDecl");
      importDecl.add("qualifiedName", qualifiedName);
      importDecl.add("static", isStatic);
      fragmentMap.add(IMPORT + '/' + qualifiedName, importDecl.render(), IMPORT_NEWLINES);
   }
}
