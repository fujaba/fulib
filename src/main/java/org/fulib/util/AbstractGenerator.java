package org.fulib.util;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.util.HashMap;
import java.util.Map;

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

   protected STGroup getSTGroup(String origFileName)
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
