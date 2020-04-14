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

import static org.fulib.classmodel.FileFragmentMap.FIELD_NEWLINES;
import static org.fulib.classmodel.FileFragmentMap.METHOD_NEWLINES;

/**
 * @author Adrian Kunz
 *
 * @since 1.2
 */
public abstract class AbstractGenerator4ClassFile
{
   // =============== Constants ===============

   private static final Pattern SIGNATURE_PATTERN = Pattern.compile("^\\s*(\\w+)\\s*:\\s*(.*)\\s*$");

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
      FileFragmentMap fragmentMap = FragmentMapBuilder.parse(classFileName);

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
            fragmentMap.add(signature, namedST.render(), newLines);
         }
      }
   }
}
