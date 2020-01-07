package org.fulib.util;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adrian Kunz
 *
 * @since 1.2
 */
public class AbstractGenerator
{
   // =============== Fields ===============

   private String customTemplatesFile;

   private Map<String, STGroup> stGroups = new HashMap<>();

   // =============== Properties ===============

   public String getCustomTemplatesFile()
   {
      return this.customTemplatesFile;
   }

   public AbstractGenerator setCustomTemplatesFile(String customTemplatesFile)
   {
      this.customTemplatesFile = customTemplatesFile;
      return this;
   }

   // =============== Methods ===============

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
}
