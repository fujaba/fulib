package org.fulib.util;

import org.fulib.Generator;
import org.fulib.Parser;
import org.fulib.StrUtil;
import org.fulib.TypeScriptParser;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Generator4TypeScriptClassFile
{
   private String customTemplatesFile;

   public Generator4TypeScriptClassFile setCustomTemplatesFile(String customTemplatesFile)
   {
      this.customTemplatesFile = customTemplatesFile;
      return this;
   }

   public void generate(Clazz clazz)
   {
      String classFileName = clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".ts";
      FileFragmentMap fragmentMap = TypeScriptParser.parse(classFileName);

      generateClassDecl(clazz, fragmentMap);

      generateAttributes(clazz, fragmentMap);

      generateConstructor(clazz, fragmentMap);

      generateRoles(clazz, fragmentMap);

      generateRemoveYou(clazz, fragmentMap);

      fragmentMap.add(Parser.CLASS_END, "}", 1);

      if (clazz.getModified() == true && fragmentMap.classBodyIsEmpty(fragmentMap)) {
         Path path = Paths.get(classFileName);
         try {
            Files.deleteIfExists(path);
            Logger.getLogger(Generator.class.getName())
                  .info("\n   deleting empty file " + classFileName);
         } catch (IOException e) {
            e.printStackTrace();
         }
      } else {
         fragmentMap.writeFile();
      }
   }



   private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap) {
      STGroup group = createSTGroup("templates/typescript/tsClassDecl.stg");
      ST st = group.getInstanceOf("classDecl");
      st.add("name", clazz.getName());
      String result = st.render();
      fragmentMap.add(Parser.CLASS, result, 1);
   }



   private void generateConstructor(Clazz clazz, FileFragmentMap fragmentMap) {
      STGroup group = createSTGroup("templates/typescript/tsClassDecl.stg");

      StringBuilder buf = new StringBuilder();

      // init attributes
      for (Attribute attribute : clazz.getAttributes())
      {
         String initValue = "0";
         if (attribute.getType().equals(ClassModelBuilder.STRING)) initValue = "''";

         buf.append("this.").append(attribute.getName()).append(" = " + initValue + ";\n");
      }

      // init roles
      for (AssocRole role : clazz.getRoles())
      {
         String initValue = "[]";

         if (role.getCardinality() == ClassModelBuilder.ONE)
            initValue = "null";

         buf.append("this._").append(role.getName()).append(" = ").append(initValue).append(";\n");
      }


      ST st = group.getInstanceOf("constructor");
      st.add("body", buf.toString());
      String result = st.render();
      fragmentMap.add(Parser.METHOD + ":constructor()", result, 2);
   }



   private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap) {
      STGroup group;
      ST attrTemplate;
      String result;

      for (Attribute attr : clazz.getAttributes())
      {
         group = createSTGroup("templates/typescript/attributes.stg");

         String attrType = attr.getType();

         attrType = StrUtil.downFirstChar(attrType);

         if ( " double int ".indexOf(attrType) >= 0)
         {
            attrType = "number";
         }

         attrTemplate = group.getInstanceOf("attrDecl");
         attrTemplate.add("type", attrType);
         attrTemplate.add("name", attr.getName());
         result = attrTemplate.render();

         fragmentMap.add(Parser.ATTRIBUTE + ":" + attr.getName(), result, 2, attr.getModified());
      }
   }



   private void generateRoles(Clazz clazz, FileFragmentMap fragmentMap)
   {
      STGroup group;

      String result;
      ST st;
      for (AssocRole role : clazz.getRoles())
      {
         if (role.getName() == null) {
            continue; //=====================================
         }

         group = createSTGroup("templates/typescript/associations.stg");

         String roleType = role.getOther().getClazz().getName();

         if ( ! roleType.equals(clazz.getName()))
         {
            String importText = String.format("import %s from \"./%s\";\n\n", roleType, roleType);
            fragmentMap.add(Parser.IMPORT + ":" + roleType, importText, 0);
         }

         if (role.getCardinality() != ClassModelBuilder.ONE) roleType += "[]";

         st = group.getInstanceOf("roleAttrDecl");
         st.add("roleName", role.getName());
         st.add("roleType", roleType);
         result = st.render();

         fragmentMap.add(Parser.ATTRIBUTE + ":_" + role.getName(), result, 2, role.getModified());

         st = group.getInstanceOf("getMethod");

         st.add("roleName", role.getName());
         st.add("roleType", roleType);
         result = st.render();

         fragmentMap.add(Parser.METHOD + ":get " + role.getName() + "()", result, 2, role.getModified());


         st = group.getInstanceOf("setMethod");
         st.add("roleName", role.getName());
         st.add("toMany", role.getCardinality() != ClassModelBuilder.ONE);
         st.add("myClassName", clazz.getName());
         st.add("otherClassName", role.getOther().getClazz().getName());
         st.add("otherRoleName", role.getOther().getName());
         st.add("otherToMany", role.getOther().getCardinality() != ClassModelBuilder.ONE);
         st.add("roleType", roleType);
         result = st.render();

         String signature = "set " + role.getName() + "(" + role.getOther().getClazz().getName() + ")";
         if (role.getCardinality() != ClassModelBuilder.ONE) {
            signature = "with" + StrUtil.cap(role.getName()) + "(any[])";
         }

         fragmentMap.add(Parser.METHOD + ":" + signature, result, 3, role.getModified());


         if (role.getCardinality() != ClassModelBuilder.ONE) {

            st = group.getInstanceOf("withoutMethod");
            st.add("roleName", role.getName());
            st.add("toMany", role.getCardinality() != ClassModelBuilder.ONE);
            st.add("myClassName", clazz.getName());
            st.add("otherClassName", role.getOther().getClazz().getName());
            st.add("otherRoleName", role.getOther().getName());
            st.add("otherToMany", role.getOther().getCardinality() != ClassModelBuilder.ONE);
            st.add("roleType", roleType);
            result = st.render();

            fragmentMap.add(Parser.METHOD + ":without" + StrUtil.cap(role.getName()) + "(any[])", result, 3, role.getModified());
         }
      }
   }



   private void generateRemoveYou(Clazz clazz, FileFragmentMap fragmentMap) {
      STGroup group = createSTGroup("templates/typescript/tsClassDecl.stg");

      StringBuilder buf = new StringBuilder();

      for (AssocRole role : clazz.getRoles())
      {
         if (role.getCardinality() == ClassModelBuilder.ONE)
         {
            buf.append("this.").append(role.getName()).append(" = null;\n");
         }
         else
         {
            buf.append("this.without").append(StrUtil.cap(role.getName()))
                  .append("(this._").append(role.getName()).append(");\n");
         }
      }


      ST st = group.getInstanceOf("removeYou");
      st.add("body", buf.toString());
      String result = st.render();
      fragmentMap.add(Parser.METHOD + ":removeYou()", result, 2);
   }



   public STGroup createSTGroup(String origFileName)
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
