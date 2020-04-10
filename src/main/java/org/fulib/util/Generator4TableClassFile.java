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
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import static org.fulib.classmodel.FileFragmentMap.*;

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

   @Override
   protected String getSourceFileName(Clazz clazz)
   {
      return clazz.getModel().getPackageSrcFolder() + "/tables/" + clazz.getName() + "Table.java";
   }

   @Override
   public void generate(Clazz clazz, FileFragmentMap fragmentMap)
   {
      this.generatePackageDecl(clazz, fragmentMap);
      this.generateImports(clazz, fragmentMap);
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

      fragmentMap.add(CLASS_END, "}", CLASS_END_NEWLINES);
   }

   private void generatePackageDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/declarations.stg");

      final ST packageDecl = group.getInstanceOf("packageDecl");
      packageDecl.add("packageName", clazz.getModel().getPackageName() + ".tables");
      fragmentMap.add(PACKAGE, packageDecl.render(), PACKAGE_NEWLINES);
   }

   private void generateImports(Clazz clazz, FileFragmentMap fragmentMap)
   {
      // needs to be sorted
      final Set<String> qualifiedNames = new TreeSet<>();

      this.collectImports(clazz, qualifiedNames);

      // add fragments

      final STGroup group = this.getSTGroup("org/fulib/templates/declarations.stg");
      for (final String qualifiedName : qualifiedNames)
      {
         final ST importDecl = group.getInstanceOf("importDecl");
         importDecl.add("qualifiedName", qualifiedName);
         fragmentMap.add(IMPORT + ":" + qualifiedName, importDecl.render(), IMPORT_NEWLINES);
      }
   }

   private void collectImports(Clazz clazz, Set<String> qualifiedNames)
   {
      qualifiedNames.add("java.util.Arrays");
      qualifiedNames.add("java.util.List");
      qualifiedNames.add("java.util.ArrayList");
      qualifiedNames.add("java.util.function.Function");
      qualifiedNames.add("java.util.function.Predicate");
      qualifiedNames.add("java.util.Set");
      qualifiedNames.add("java.util.HashSet");
      qualifiedNames.add("java.util.LinkedHashSet");
      qualifiedNames.add("java.util.Map");
      qualifiedNames.add("java.util.LinkedHashMap");

      // qualified name of the class
      final String packageName = clazz.getModel().getPackageName();
      qualifiedNames.add(packageName + "." + clazz.getName());

      // qualified names of association target classes
      for (final AssocRole role : clazz.getRoles())
      {
         final AssocRole other = role.getOther();
         if (other.getName() == null)
         {
            continue;
         }

         final String otherClassName = other.getClazz().getName();
         final String fullClassName = packageName + "." + otherClassName;
         qualifiedNames.add(fullClassName);
      }
   }

   private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/declarations.stg");

      final ST classDecl = group.getInstanceOf("classDecl");
      classDecl.add("name", clazz.getName() + "Table");
      classDecl.add("superClass", clazz.getSuperClass() != null ? clazz.getSuperClass().getName() + "Table" : null);
      fragmentMap.add(CLASS, classDecl.render(), CLASS_NEWLINES);
   }

   private void generateConstructor(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/constructor.stg");

      final ST constructor = group.getInstanceOf("constructor");
      constructor.add("className", clazz.getName());
      fragmentMap.add(CONSTRUCTOR + ":" + clazz.getName() + "Table(" + clazz.getName() + "...)", constructor.render(),
                      CONSTRUCTOR_NEWLINES, clazz.getModified());
   }

   private void generateStandardAttributes(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/attributes.pojo.stg");

      // here so the attribute templates have a class name
      final Clazz owner = new Clazz().setName(clazz.getName() + "Table");

      final Attribute table = new Attribute();
      table.setName("table");
      table.setType("List<List<Object>>");
      table.setInitialization("new ArrayList<>()");
      table.setClazz(owner);
      this.generateStandardAttribute(clazz, fragmentMap, group, table);

      final STGroup attributesGroup = this.getSTGroup("org/fulib/templates/tables/attributes.stg");
      final ST getColumn = attributesGroup.getInstanceOf("getColumn");
      fragmentMap.add(METHOD + ":getColumn()", getColumn.render(), METHOD_NEWLINES);

      final Attribute columnName = new Attribute();
      columnName.setName("columnName");
      columnName.setType("String");
      columnName.setClazz(owner);
      this.generateStandardAttribute(clazz, fragmentMap, group, columnName);

      final Attribute columnMap = new Attribute();
      columnMap.setName("columnMap");
      columnMap.setType("Map<String, Integer>");
      columnMap.setInitialization("new LinkedHashMap<>()");
      columnMap.setClazz(owner);
      this.generateStandardAttribute(clazz, fragmentMap, group, columnMap);
   }

   private void generateStandardAttribute(Clazz clazz, FileFragmentMap fragmentMap, STGroup group, Attribute attr)
   {
      final ST attrDecl = group.getInstanceOf("attrDecl");
      attrDecl.add("attr", attr);
      fragmentMap.add(ATTRIBUTE + ":" + attr.getName(), attrDecl.render(), FIELD_NEWLINES, clazz.getModified());

      final ST attrGet = group.getInstanceOf("attrGet");
      attrGet.add("attr", attr);
      fragmentMap.add(METHOD + ":get" + StrUtil.cap(attr.getName()) + "()", attrGet.render(), METHOD_NEWLINES,
                      attr.getModified());

      final ST attrSet = group.getInstanceOf("attrSet");
      attrSet.add("attr", attr);
      fragmentMap.add(METHOD + ":set" + StrUtil.cap(attr.getName()) + "(" + attr.getType().replace(" ", "") + ")",
                      attrSet.render(), METHOD_NEWLINES, attr.getModified());
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
         fragmentMap.add(METHOD + ":expand" + StrUtil.cap(attr.getName()) + "(String...)", expandMethod.render(),
                         METHOD_NEWLINES, attr.getModified());
      }
   }

   private void generateAssociations(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/associations.stg");

      for (AssocRole role : clazz.getRoles())
      {
         if (role.getName() == null)
         {
            continue;
         }

         String otherClassName = role.getOther().getClazz().getName();

         // getMethod(roleName,toMany,className,otherClassName) ::=
         final ST st = group.getInstanceOf("expandMethod");
         st.add("roleName", role.getName());
         st.add("toMany", role.getCardinality() != Type.ONE);
         st.add("className", clazz.getName());
         st.add("otherClassName", otherClassName);
         fragmentMap.add(METHOD + ":expand" + StrUtil.cap(role.getName()) + "(String...)", st.render(), METHOD_NEWLINES,
                         role.getModified());

         // hasMethod(roleName,toMany,className,otherClassName) ::=
         final ST hasMethod = group.getInstanceOf("hasMethod");
         hasMethod.add("roleName", role.getName());
         hasMethod.add("toMany", role.getCardinality() != Type.ONE);
         hasMethod.add("className", clazz.getName());
         hasMethod.add("otherClassName", otherClassName);
         fragmentMap
            .add(METHOD + ":has" + StrUtil.cap(role.getName()) + "(" + otherClassName + "Table)", hasMethod.render(),
                 METHOD_NEWLINES, role.getModified());
      }
   }

   private void generateSelectColumns(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/selectColumns.stg");

      final ST selectColumns = group.getInstanceOf("selectColumns");
      selectColumns.add("className", clazz.getName());
      fragmentMap
         .add(METHOD + ":selectColumns(String...)", selectColumns.render(), METHOD_NEWLINES, clazz.getModified());

      final ST dropColumns = group.getInstanceOf("dropColumns");
      dropColumns.add("className", clazz.getName());
      fragmentMap.add(METHOD + ":dropColumns(String...)", dropColumns.render(), METHOD_NEWLINES, clazz.getModified());
   }

   private void generateAddColumn(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/selectColumns.stg");

      final ST addColumn = group.getInstanceOf("addColumn");
      addColumn.add("className", clazz.getName());
      fragmentMap
         .add(METHOD + ":addColumn(String,Function<? super Map<String,Object>,?>)", addColumn.render(), METHOD_NEWLINES,
              clazz.getModified());
   }

   private void generateFilter(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/filter.stg");

      if (clazz.getModified() || clazz.getSuperClass() != null)
      {
         // do not generate filter method
         fragmentMap.remove(METHOD + ":filter(Predicate<? super " + clazz.getName() + ">)");
      }
      else
      {
         final ST filter = group.getInstanceOf("filter");
         filter.add("className", clazz.getName());
         fragmentMap
            .add(METHOD + ":filter(Predicate<? super " + clazz.getName() + ">)", filter.render(), METHOD_NEWLINES);
      }

      final ST filterRow = group.getInstanceOf("filterRow");
      filterRow.add("className", clazz.getName());

      fragmentMap.add(METHOD + ":filterRow(Predicate<? super Map<String,Object>>)", filterRow.render(), METHOD_NEWLINES,
                      clazz.getModified());
   }

   private void generateToSet(Clazz clazz, FileFragmentMap fragmentMap)
   {
      if (clazz.getModified() || clazz.getSuperClass() != null)
      {
         // do not generate toSet method
         fragmentMap.remove(METHOD + ":toSet()");
      }
      else
      {
         final STGroup group = this.getSTGroup("org/fulib/templates/tables/toSet.stg");

         final ST st = group.getInstanceOf("toSet");
         st.add("className", clazz.getName());
         fragmentMap.add(METHOD + ":toSet()", st.render(), METHOD_NEWLINES);
      }
   }

   private void generateToString(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/toString.stg");

      final ST st = group.getInstanceOf("toString");
      fragmentMap.add(METHOD + ":toString()", st.render(), METHOD_NEWLINES, clazz.getModified());
   }
}
