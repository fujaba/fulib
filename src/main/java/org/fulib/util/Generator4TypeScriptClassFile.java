package org.fulib.util;

import org.fulib.Generator;
import org.fulib.StrUtil;
import org.fulib.TypeScriptParser;
import org.fulib.builder.Type;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class Generator4TypeScriptClassFile extends AbstractGenerator
{
   // =============== Properties ===============

   @Override
   public Generator4TypeScriptClassFile setCustomTemplatesFile(String customTemplatesFile)
   {
      super.setCustomTemplatesFile(customTemplatesFile);
      return this;
   }

   // =============== Methods ===============

   public void generate(Clazz clazz)
   {
      String classFileName = clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".ts";
      FileFragmentMap fragmentMap = TypeScriptParser.parse(classFileName);

      this.generateClassDecl(clazz, fragmentMap);

      this.generateAttributes(clazz, fragmentMap);

      this.generateConstructor(clazz, fragmentMap);

      this.generateRoles(clazz, fragmentMap);

      this.generateRemoveYou(clazz, fragmentMap);

      fragmentMap.add(FileFragmentMap.CLASS_END, "}", 1);

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
         fragmentMap.writeFile();
      }
   }

   private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      STGroup group = this.getSTGroup("org/fulib/templates/typescript/tsClassDecl.stg");
      ST st = group.getInstanceOf("classDecl");
      st.add("name", clazz.getName());
      String result = st.render();
      fragmentMap.add(FileFragmentMap.CLASS, result, 1);
   }

   private void generateConstructor(Clazz clazz, FileFragmentMap fragmentMap)
   {
      STGroup group = this.getSTGroup("org/fulib/templates/typescript/tsClassDecl.stg");

      StringBuilder buf = new StringBuilder();

      // init attributes
      for (Attribute attribute : clazz.getAttributes())
      {
         String initValue = "0";
         if (attribute.getType().equals(Type.STRING))
            initValue = "''";

         buf.append("this.").append(attribute.getName()).append(" = ").append(initValue).append(";\n");
      }

      // init roles
      for (AssocRole role : clazz.getRoles())
      {
         String initValue = "[]";

         if (role.getCardinality() == Type.ONE)
            initValue = "null";

         buf.append("this._").append(role.getName()).append(" = ").append(initValue).append(";\n");
      }

      ST st = group.getInstanceOf("constructor");
      st.add("body", buf.toString());
      String result = st.render();
      fragmentMap.add(FileFragmentMap.METHOD + ":constructor()", result, 2);
   }

   private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap)
   {
      STGroup group;
      ST attrTemplate;
      String result;

      for (Attribute attr : clazz.getAttributes())
      {
         group = this.getSTGroup("org/fulib/templates/typescript/attributes.stg");

         String attrType = attr.getType();

         attrType = StrUtil.downFirstChar(attrType);

         if (" double int ".contains(attrType))
         {
            attrType = "number";
         }

         attrTemplate = group.getInstanceOf("attrDecl");
         attrTemplate.add("type", attrType);
         attrTemplate.add("name", attr.getName());
         result = attrTemplate.render();

         fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":" + attr.getName(), result, 2, attr.getModified());
      }
   }

   private void generateRoles(Clazz clazz, FileFragmentMap fragmentMap)
   {
      STGroup group;

      String result;
      ST st;
      for (AssocRole role : clazz.getRoles())
      {
         if (role.getName() == null)
         {
            continue; //=====================================
         }

         group = this.getSTGroup("org/fulib/templates/typescript/associations.stg");

         String roleType = role.getOther().getClazz().getName();

         if (!roleType.equals(clazz.getName()))
         {
            String importText = String.format("import %s from \"./%s\";\n\n", roleType, roleType);
            fragmentMap.add(FileFragmentMap.IMPORT + ":" + roleType, importText, 0);
         }

         if (role.getCardinality() != Type.ONE)
            roleType += "[]";

         st = group.getInstanceOf("roleAttrDecl");
         st.add("roleName", role.getName());
         st.add("roleType", roleType);
         result = st.render();

         fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":_" + role.getName(), result, 2, role.getModified());

         st = group.getInstanceOf("getMethod");

         st.add("roleName", role.getName());
         st.add("roleType", roleType);
         result = st.render();

         fragmentMap.add(FileFragmentMap.METHOD + ":get " + role.getName() + "()", result, 2, role.getModified());

         st = group.getInstanceOf("setMethod");
         st.add("roleName", role.getName());
         st.add("toMany", role.getCardinality() != Type.ONE);
         st.add("myClassName", clazz.getName());
         st.add("otherClassName", role.getOther().getClazz().getName());
         st.add("otherRoleName", role.getOther().getName());
         st.add("otherToMany", role.getOther().getCardinality() != Type.ONE);
         st.add("roleType", roleType);
         result = st.render();

         String signature = "set " + role.getName() + "(" + role.getOther().getClazz().getName() + ")";
         if (role.getCardinality() != Type.ONE)
         {
            signature = "with" + StrUtil.cap(role.getName()) + "(any[])";
         }

         fragmentMap.add(FileFragmentMap.METHOD + ":" + signature, result, 3, role.getModified());

         if (role.getCardinality() != Type.ONE)
         {

            st = group.getInstanceOf("withoutMethod");
            st.add("roleName", role.getName());
            st.add("toMany", role.getCardinality() != Type.ONE);
            st.add("myClassName", clazz.getName());
            st.add("otherClassName", role.getOther().getClazz().getName());
            st.add("otherRoleName", role.getOther().getName());
            st.add("otherToMany", role.getOther().getCardinality() != Type.ONE);
            st.add("roleType", roleType);
            result = st.render();

            fragmentMap.add(FileFragmentMap.METHOD + ":without" + StrUtil.cap(role.getName()) + "(any[])", result, 3,
                            role.getModified());
         }
      }
   }

   private void generateRemoveYou(Clazz clazz, FileFragmentMap fragmentMap)
   {
      STGroup group = this.getSTGroup("org/fulib/templates/typescript/tsClassDecl.stg");

      StringBuilder buf = new StringBuilder();

      for (AssocRole role : clazz.getRoles())
      {
         if (role.getCardinality() == Type.ONE)
         {
            buf.append("this.").append(role.getName()).append(" = null;\n");
         }
         else
         {
            buf.append("this.without").append(StrUtil.cap(role.getName())).append("(this._").append(role.getName())
               .append(");\n");
         }
      }

      ST st = group.getInstanceOf("removeYou");
      st.add("body", buf.toString());
      String result = st.render();
      fragmentMap.add(FileFragmentMap.METHOD + ":removeYou()", result, 2);
   }
}
