package org.fulib.util;

import org.fulib.Generator;
import org.fulib.StrUtil;
import org.fulib.builder.Type;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.fulib.parser.FragmentMapBuilder;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Generator4TableClassFile extends AbstractGenerator
{
   // =============== Properties ===============

   @Override
   public Generator4TableClassFile setCustomTemplatesFile(String customTemplatesFile)
   {
      super.setCustomTemplatesFile(customTemplatesFile);
      return this;
   }

   // =============== Methods ===============

   public void generate(Clazz clazz)
   {
      String classFileName = clazz.getModel().getPackageSrcFolder() + "/tables/" + clazz.getName() + "Table.java";
      FileFragmentMap fragmentMap = FragmentMapBuilder.parse(classFileName);

      this.generatePackageDecl(clazz, fragmentMap);
      this.generateClassDecl(clazz, fragmentMap);
      this.generateConstructor(clazz, fragmentMap);
      this.generateStandardAttributes(clazz, fragmentMap);
      this.generateAttributes(clazz, fragmentMap);
      this.generateAssociations(clazz, fragmentMap);
      this.generateSelectColumns(clazz, fragmentMap);
      this.generateAddColumn(clazz, fragmentMap);
      this.generateFilter(clazz, fragmentMap);
      this.generateToSet(clazz, fragmentMap);
      this.generateToString(clazz, fragmentMap);

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

   private void generatePackageDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      // TODO template
      String result = String.format("package %s;", clazz.getModel().getPackageName() + ".tables");
      fragmentMap.add(FileFragmentMap.PACKAGE, result, 2);
   }

   private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/declarations.stg");

      final ST classDecl = group.getInstanceOf("classDecl");
      classDecl.add("name", clazz.getName() + "Table");
      classDecl.add("superClass", clazz.getSuperClass() != null ? clazz.getSuperClass().getName() + "Table" : null);
      fragmentMap.add(FileFragmentMap.CLASS, classDecl.render(), 2);
   }

   private void generateConstructor(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/constructor.stg");

      final ST constructor = group.getInstanceOf("constructor");
      constructor.add("className", clazz.getName());
      fragmentMap.add(FileFragmentMap.CONSTRUCTOR + ":" + clazz.getName() + "Table(" + clazz.getName() + "...)",
                      constructor.render(), 2, clazz.getModified());
   }

   private void generateStandardAttributes(Clazz clazz, FileFragmentMap fragmentMap)
   {
      STGroup group = this.getSTGroup("org/fulib/templates/attributes.pojo.stg");
      ST attrTemplate;
      String result;

      fragmentMap.add(FileFragmentMap.IMPORT + ":java.util.ArrayList", "import java.util.ArrayList;", 1);
      fragmentMap.add(FileFragmentMap.IMPORT + ":java.util.LinkedHashMap", "import java.util.LinkedHashMap;", 1);

      ArrayList<Attribute> standardAttributes = new ArrayList<>();

      standardAttributes.add(new Attribute().setName("table").setType("ArrayList<ArrayList<Object>>")
                                            .setInitialization("new ArrayList<>()"));

      standardAttributes.add(new Attribute().setName("columnName").setType("String").setInitialization("null"));

      standardAttributes.add(new Attribute().setName("columnMap").setType("LinkedHashMap<String, Integer>")
                                            .setInitialization("new LinkedHashMap<>()"));

      // here so the attribute templates have a class name
      new Clazz().setName(clazz.getName() + "Table").withAttributes(standardAttributes);

      for (Attribute attr : standardAttributes)
      {
         attrTemplate = group.getInstanceOf("attrDecl").add("attr", attr);
         result = attrTemplate.render();

         fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":" + attr.getName(), result, 2, clazz.getModified());

         attrTemplate = group.getInstanceOf("attrGet").add("attr", attr);
         result = attrTemplate.render();

         fragmentMap
            .add(FileFragmentMap.METHOD + ":get" + StrUtil.cap(attr.getName()) + "()", result, 2, attr.getModified());

         attrTemplate = group.getInstanceOf("attrSet").add("attr", attr);
         result = attrTemplate.render();

         fragmentMap.add(
            FileFragmentMap.METHOD + ":set" + StrUtil.cap(attr.getName()) + "(" + attr.getType().replace(" ", "") + ")",
            result, 3, attr.getModified());
      }
   }

   private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/attributes.stg");

      for (Attribute attr : clazz.getAttributes())
      {
         final ST expandMethod = group.getInstanceOf("expandMethod");
         expandMethod.add("roleName", attr.getName());
         expandMethod.add("typeName", attr.getType());
         expandMethod.add("className", clazz.getName());
         fragmentMap.add(FileFragmentMap.METHOD + ":expand" + StrUtil.cap(attr.getName()) + "(String...)",
                         expandMethod.render(), 2, attr.getModified());
      }
   }

   private void generateAssociations(Clazz clazz, FileFragmentMap fragmentMap)
   {
      String fullClassName = clazz.getModel().getPackageName() + "." + clazz.getName();
      fragmentMap.add(FileFragmentMap.IMPORT + ":" + fullClassName, "import " + fullClassName + ";", 1);

      STGroup group = this.getSTGroup("org/fulib/templates/tables/associations.stg");
      String result;
      ST st;
      for (AssocRole role : clazz.getRoles())
      {
         if (role.getName() == null)
         {
            continue; //===================================
         }

         String otherClassName = role.getOther().getClazz().getName();

         // getMethod(roleName,toMany,className,otherClassName) ::=
         st = group.getInstanceOf("expandMethod");
         st.add("roleName", role.getName());
         st.add("toMany", role.getCardinality() != Type.ONE);
         st.add("className", clazz.getName());
         st.add("otherClassName", otherClassName);
         result = st.render();
         fragmentMap.add(FileFragmentMap.METHOD + ":expand" + StrUtil.cap(role.getName()) + "(String...)", result, 2,
                         role.getModified());

         // hasMethod(roleName,toMany,className,otherClassName) ::=
         st = group.getInstanceOf("hasMethod");
         st.add("roleName", role.getName());
         st.add("toMany", role.getCardinality() != Type.ONE);
         st.add("className", clazz.getName());
         st.add("otherClassName", otherClassName);
         result = st.render();
         fragmentMap
            .add(FileFragmentMap.METHOD + ":has" + StrUtil.cap(role.getName()) + "(" + otherClassName + "Table)",
                 result, 2, role.getModified());

         fullClassName = clazz.getModel().getPackageName() + "." + otherClassName;
         fragmentMap.add(FileFragmentMap.IMPORT + ":" + fullClassName, "import " + fullClassName + ";", 1);
      }
   }

   private void generateSelectColumns(Clazz clazz, FileFragmentMap fragmentMap)
   {
      fragmentMap.add(FileFragmentMap.IMPORT + ":java.util.Arrays", "import java.util.Arrays;", 1);

      final STGroup group = this.getSTGroup("org/fulib/templates/tables/selectColumns.stg");

      final ST selectColumns = group.getInstanceOf("selectColumns");
      selectColumns.add("className", clazz.getName());
      fragmentMap
         .add(FileFragmentMap.METHOD + ":selectColumns(String...)", selectColumns.render(), 2, clazz.getModified());

      final ST dropColumns = group.getInstanceOf("dropColumns");
      dropColumns.add("className", clazz.getName());
      fragmentMap.add(FileFragmentMap.METHOD + ":dropColumns(String...)", dropColumns.render(), 2, clazz.getModified());
   }

   private void generateAddColumn(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/selectColumns.stg");

      final ST addColumn = group.getInstanceOf("addColumn");
      addColumn.add("className", clazz.getName());
      fragmentMap.add(FileFragmentMap.METHOD
                      + ":addColumn(String,java.util.function.Function<java.util.LinkedHashMap<String,Object>,Object>)",
                      addColumn.render(), 2, clazz.getModified());
   }

   private void generateFilter(Clazz clazz, FileFragmentMap fragmentMap)
   {
      fragmentMap
         .add(FileFragmentMap.IMPORT + ":java.util.function.Predicate", "import java.util.function.Predicate;", 1);

      final STGroup group = this.getSTGroup("org/fulib/templates/tables/filter.stg");

      if (clazz.getModified() || clazz.getSuperClass() != null)
      {
         // do not generate filter method
         fragmentMap.add(FileFragmentMap.METHOD + ":filter(Predicate<" + clazz.getName() + ">)", "", 2, true);
      }
      else
      {
         final ST filter = group.getInstanceOf("filter");
         filter.add("className", clazz.getName());
         fragmentMap.add(FileFragmentMap.METHOD + ":filter(Predicate<" + clazz.getName() + ">)", filter.render(), 2);
      }

      final ST filterRow = group.getInstanceOf("filterRow");
      filterRow.add("className", clazz.getName());

      fragmentMap
         .add(FileFragmentMap.METHOD + ":filterRow(Predicate<LinkedHashMap<String,Object>>)", filterRow.render(), 2,
              clazz.getModified());
   }

   private void generateToSet(Clazz clazz, FileFragmentMap fragmentMap)
   {
      fragmentMap.add(FileFragmentMap.IMPORT + ":java.util.LinkedHashSet", "import java.util.LinkedHashSet;", 1);

      boolean removeFragment = clazz.getModified();
      if (clazz.getModified() || clazz.getSuperClass() != null)
      {
         // do not generate toSet method
         fragmentMap.add(FileFragmentMap.METHOD + ":toSet()", "", 2, removeFragment);
      }
      else
      {
         final STGroup group = this.getSTGroup("org/fulib/templates/tables/toSet.stg");

         final ST st = group.getInstanceOf("toSet");
         st.add("className", clazz.getName());
         fragmentMap.add(FileFragmentMap.METHOD + ":toSet()", st.render(), 2, removeFragment);
      }
   }

   private void generateToString(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/toString.stg");

      final ST st = group.getInstanceOf("toString");
      fragmentMap.add(FileFragmentMap.METHOD + ":toString()", st.render(), 2, clazz.getModified());
   }
}
