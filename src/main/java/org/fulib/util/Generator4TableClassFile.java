package org.fulib.util;

import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.Set;
import java.util.TreeSet;

import static org.fulib.classmodel.FileFragmentMap.*;

public class Generator4TableClassFile extends AbstractGenerator4ClassFile
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
   public String getSourceFileName(Clazz clazz)
   {
      return clazz.getModel().getPackageSrcFolder() + "/tables/" + clazz.getName() + "Table.java";
   }

   @Override
   public void generate(Clazz clazz, FileFragmentMap fragmentMap)
   {
      this.generatePackageDecl(clazz, fragmentMap);
      this.generateImports(clazz, fragmentMap);
      this.generateClassDecl(clazz, fragmentMap);
      this.generateStandardAttributes(clazz, fragmentMap);
      this.generateAttributes(clazz, fragmentMap);
      this.generateAssociations(clazz, fragmentMap);

      final STGroup group = this.getSTGroup("org/fulib/templates/tables/members.stg");
      this.generateFromSignatures(fragmentMap, group, "tableSignatures", clazz.getModified(),
                                  st -> st.add("clazz", clazz));

      fragmentMap.add(CLASS + '/' + clazz.getName() + "Table" + '/' + CLASS_END, "}", CLASS_END_NEWLINES);
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
         fragmentMap.add(IMPORT + '/' + qualifiedName, importDecl.render(), IMPORT_NEWLINES);
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
      fragmentMap.add(CLASS + '/' + clazz.getName() + "Table" + '/' + CLASS_DECL, classDecl.render(), CLASS_NEWLINES);
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
      this.generateStandardAttribute(fragmentMap, group, table);

      final STGroup attributesGroup = this.getSTGroup("org/fulib/templates/tables/attributes.stg");
      final ST getColumn = attributesGroup.getInstanceOf("getColumn");
      fragmentMap.add(CLASS + '/' + clazz.getName() + "Table" + '/' + METHOD + "/getColumn()", getColumn.render(),
                      METHOD_NEWLINES);

      final Attribute columnName = new Attribute();
      columnName.setName("columnName");
      columnName.setType("String");
      columnName.setClazz(owner);
      this.generateStandardAttribute(fragmentMap, group, columnName);

      final Attribute columnMap = new Attribute();
      columnMap.setName("columnMap");
      columnMap.setType("Map<String,Integer>");
      columnMap.setInitialization("new LinkedHashMap<>()");
      columnMap.setClazz(owner);
      this.generateStandardAttribute(fragmentMap, group, columnMap);
   }

   private void generateStandardAttribute(FileFragmentMap fragmentMap, STGroup group, Attribute attr)
   {
      this.generateFromSignatures(fragmentMap, group, "attrSignatures", false, st -> st.add("attr", attr));
   }

   private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/tables/attributes.stg");

      for (Attribute attr : clazz.getAttributes())
      {
         this.generateFromSignatures(fragmentMap, group, "tableAttributeSignatures", attr.getModified(),
                                     st -> st.add("attr", attr));
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

         this.generateFromSignatures(fragmentMap, group, "tableRoleSignatures", role.getModified(),
                                     st -> st.add("role", role).add("other", role.getOther()));
      }
   }
}
