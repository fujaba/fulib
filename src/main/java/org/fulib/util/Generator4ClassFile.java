package org.fulib.util;

import org.fulib.Generator;
import org.fulib.StrUtil;
import org.fulib.builder.Type;
import org.fulib.classmodel.*;
import org.fulib.parser.FragmentMapBuilder;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import static org.fulib.classmodel.FileFragmentMap.METHOD;

public class Generator4ClassFile extends AbstractGenerator
{
   // =============== Properties ===============

   @Override
   public Generator4ClassFile setCustomTemplatesFile(String customTemplatesFile)
   {
      super.setCustomTemplatesFile(customTemplatesFile);
      return this;
   }

   // =============== Methods ===============

   public void generate(Clazz clazz)
   {
      String classFileName = clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".java";
      FileFragmentMap fragmentMap = FragmentMapBuilder.parse(classFileName);

      // doGenerate code for class
      this.generatePackageDecl(clazz, fragmentMap);

      this.generateClassDecl(clazz, fragmentMap);

      this.generateAttributes(clazz, fragmentMap);

      this.generateAssociations(clazz, fragmentMap);

      this.generateMethods(clazz, fragmentMap);

      this.generatePropertyChangeSupport(clazz, fragmentMap);

      this.generateToString(clazz, fragmentMap);

      this.generateRemoveYou(clazz, fragmentMap);

      fragmentMap.add(FileFragmentMap.CLASS_END, "}", 1);

      this.generateImports(clazz, fragmentMap);

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

   // --------------- Declarations ---------------

   private void generatePackageDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/declarations.stg");
      final ST packageDecl = group.getInstanceOf("packageDecl");
      packageDecl.add("packageName", clazz.getModel().getPackageName());
      fragmentMap.add(FileFragmentMap.PACKAGE, packageDecl.render(), 2);
   }

   private void generateImports(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final Set<String> qualifiedNames = new TreeSet<>();

      // static imports are collected separately.
      // we could just stuff them in qualifiedNames with "static " at the beginning,
      // but that doesn't work for the fragment keys and it doesn't sort nicely.
      final Set<String> staticImports = new TreeSet<>();

      // parse user-supplied imports
      for (String importItem : clazz.getImportList())
      {
         if (importItem.startsWith("import "))
         {
            final int end = importItem.length() - (importItem.endsWith(";") ? 1 : 0);
            if (importItem.startsWith("import static "))
            {
               // import static org.foo.Bar.baz;
               staticImports.add(importItem.substring("import static ".length(), end));
            }
            else
            {
               // import org.foo.Bar;
               qualifiedNames.add(importItem.substring("import ".length(), end));
            }
         }
         else if (importItem.startsWith("static "))
         {
            // static org.foo.Bar.baz
            staticImports.add(importItem.substring("static ".length()));
         }
         else
         {
            // org.foo.Bar
            qualifiedNames.add(importItem);
         }
      }

      this.addDefaultImports(clazz, qualifiedNames);

      // add fragments

      final STGroup group = this.getSTGroup("org/fulib/templates/declarations.stg");
      for (final String qualifiedName : qualifiedNames)
      {
         final ST importDecl = group.getInstanceOf("importDecl");
         importDecl.add("qualifiedName", qualifiedName);
         fragmentMap.add(FileFragmentMap.IMPORT + ":" + qualifiedName, importDecl.render(), 1);
      }

      for (final String qualifiedName : staticImports)
      {
         final ST importDecl = group.getInstanceOf("importDecl");
         importDecl.add("qualifiedName", qualifiedName);
         importDecl.add("static", true);
         fragmentMap.add(FileFragmentMap.IMPORT + ":" + qualifiedName, importDecl.render(), 1);
      }
   }

   private void addDefaultImports(Clazz clazz, Set<String> qualifiedNames)
   {
      // any roles or attributes
      if (!clazz.getRoles().isEmpty() || !clazz.getAttributes().isEmpty())
      {
         qualifiedNames.add("java.beans.PropertyChangeSupport");
         qualifiedNames.add("java.beans.PropertyChangeListener");
      }

      // any JavaFX roles or attributes
      if (clazz.getRoles().stream().anyMatch(a -> Type.JAVA_FX.equals(a.getPropertyStyle())) //
          || clazz.getAttributes().stream().anyMatch(a -> Type.JAVA_FX.equals(a.getPropertyStyle())))
      {
         qualifiedNames.add("javafx.beans.property.*");
      }

      // any non-primitive attributes
      if (clazz.getAttributes().stream().anyMatch(a -> !isPrimitive(a.getType())))
      {
         qualifiedNames.add("java.util.Objects");
      }

      // any to-n roles or to-n attributes
      if (clazz.getRoles().stream().anyMatch(a -> a.getCardinality() != Type.ONE) //
          || clazz.getAttributes().stream().anyMatch(a -> a.getType().endsWith(Type.__LIST)))
      {
         qualifiedNames.add("java.util.Collection");
      }
   }

   private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/declarations.stg");
      final ST classDecl = group.getInstanceOf("classDecl");
      classDecl.add("name", clazz.getName());
      classDecl.add("superClass", clazz.getSuperClass() != null ? clazz.getSuperClass().getName() : null);
      fragmentMap.add(FileFragmentMap.CLASS, classDecl.render(), 2);
   }

   // --------------- Attributes ---------------

   private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap)
   {
      for (Attribute attr : clazz.getAttributes())
      {
         this.generateAttribute(fragmentMap, attr);
      }
   }

   private void generateAttribute(FileFragmentMap fragmentMap, Attribute attr)
   {
      final STGroup group;
      if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
      {
         group = this.getSTGroup("org/fulib/templates/JavaFXattributes.stg");
         group.importTemplates(this.getSTGroup("org/fulib/templates/attributes.stg"));
      }
      else
      {
         group = this.getSTGroup("org/fulib/templates/attributes.stg");
      }

      String attrType = attr.getType();
      if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
      {
         attrType = getBoxType(attrType);
      }

      String baseType = attrType;
      String boxType = baseType;
      if (attrType.endsWith(Type.__LIST))
      {
         baseType = attrType.substring(0, attrType.length() - Type.__LIST.length());
         boxType = getBoxType(baseType);
         attrType = String.format("java.util.ArrayList<%s>", boxType);
      }

      final String className = attr.getClazz().getName();
      final String attrName = attr.getName();
      final String capAttrName = StrUtil.cap(attrName);
      final boolean modified = attr.getModified();

      final ST propertyDecl = group.getInstanceOf("propertyDecl");
      propertyDecl.add("name", attrName);
      fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":PROPERTY_" + attrName, propertyDecl.render(), 2, modified);

      final ST attrDecl = group.getInstanceOf("attrDecl");
      attrDecl.add("type", attrType);
      attrDecl.add("name", attrName);
      attrDecl.add("value", attr.getInitialization());
      fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":" + attrName, attrDecl.render(), 2, modified);

      if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
      {
         final ST initMethod = group.getInstanceOf("initMethod");
         initMethod.add("name", attrName);
         initMethod.add("type", attrType);
         fragmentMap.add(METHOD + ":_init" + capAttrName + "()", initMethod.render(), 2, modified);
      }
      else
      {
         fragmentMap.add(METHOD + ":_init" + capAttrName + "()", "", 2, true);
      }

      final ST attrGet = group.getInstanceOf("attrGet");
      attrGet.add("type", attrType);
      attrGet.add("name", attrName);
      fragmentMap.add(METHOD + ":get" + capAttrName + "()", attrGet.render(), 2, modified);

      if (attr.getType().endsWith(Type.__LIST))
      {
         final ST attrWithItem = group.getInstanceOf("attrWithItem");
         attrWithItem.add("class", className);
         attrWithItem.add("listType", attrType);
         attrWithItem.add("baseType", boxType);
         attrWithItem.add("name", attrName);
         fragmentMap.add(METHOD + ":with" + capAttrName + "(" + boxType + ")", attrWithItem.render(), 3, modified);

         final ST attrWithArray = group.getInstanceOf("attrWithArray");
         attrWithArray.add("class", className);
         attrWithArray.add("baseType", boxType);
         attrWithArray.add("name", attrName);
         fragmentMap.add(METHOD + ":with" + capAttrName + "(" + boxType + "...)", attrWithArray.render(), 3, modified);

         final ST attrWithColl = group.getInstanceOf("attrWithColl");
         attrWithColl.add("class", className);
         attrWithColl.add("baseType", boxType);
         attrWithColl.add("name", attrName);
         fragmentMap
            .add(METHOD + ":with" + capAttrName + "(Collection<? extends " + boxType + ">)", attrWithColl.render(), 3,
                 modified);

         final ST attrWithoutItem = group.getInstanceOf("attrWithoutItem");
         attrWithoutItem.add("class", className);
         attrWithoutItem.add("baseType", boxType);
         attrWithoutItem.add("name", attrName);
         fragmentMap
            .add(METHOD + ":without" + capAttrName + "(" + boxType + ")", attrWithoutItem.render(), 3, modified);

         final ST attrWithoutArray = group.getInstanceOf("attrWithoutArray");
         attrWithoutArray.add("class", className);
         attrWithoutArray.add("baseType", boxType);
         attrWithoutArray.add("name", attrName);
         fragmentMap
            .add(METHOD + ":without" + capAttrName + "(" + boxType + "...)", attrWithoutArray.render(), 3, modified);

         final ST attrWithoutColl = group.getInstanceOf("attrWithoutColl");
         attrWithoutColl.add("class", className);
         attrWithoutColl.add("baseType", boxType);
         attrWithoutColl.add("name", attrName);
         fragmentMap.add(METHOD + ":without" + capAttrName + "(Collection<? extends " + boxType + ">)",
                         attrWithoutColl.render(), 3, modified);

         // remove "set" method
         fragmentMap.add(METHOD + ":set" + capAttrName + "(" + baseType + ")", "", 3, true);
      }
      else // usual attribute
      {
         final ST attrSet = group.getInstanceOf("attrSet");
         attrSet.add("class", className);
         attrSet.add("type", attr.getType());
         attrSet.add("name", attrName);
         attrSet.add("useEquals", !isPrimitive(attr.getType()));
         fragmentMap.add(METHOD + ":set" + capAttrName + "(" + attr.getType() + ")", attrSet.render(), 3, modified);

         // remove "with" and "without" methods
         fragmentMap.add(METHOD + ":with" + capAttrName + "(" + boxType + ")", "", 3, true);
         fragmentMap.add(METHOD + ":with" + capAttrName + "(" + boxType + "...)", "", 3, true);
         fragmentMap.add(METHOD + ":with" + capAttrName + "(Collection<? extends " + boxType + ">)", "", 3, true);
         fragmentMap.add(METHOD + ":without" + capAttrName + "(" + boxType + ")", "", 3, true);
         fragmentMap.add(METHOD + ":without" + capAttrName + "(" + boxType + "...)", "", 3, true);
         fragmentMap.add(METHOD + ":without" + capAttrName + "(Collection<? extends " + boxType + ">)", "", 3, true);
      }

      if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
      {
         final ST propertyGet = group.getInstanceOf("propertyGet");
         propertyGet.add("name", attrName);
         propertyGet.add("type", attrType);
         fragmentMap.add(METHOD + ":" + attrName + "Property()", propertyGet.render(), 3, modified);
      }
      else
      {
         fragmentMap.add(METHOD + ":" + attrName + "Property()", "", 3, true);
      }
   }

   private static String getBoxType(String attrType)
   {
      switch (attrType)
      {
      case "boolean":
         return "Boolean";
      case "byte":
         return "Byte";
      case "short":
         return "Short";
      case "char":
         return "Character"; // !
      case "int":
         return "Integer"; // !
      case "long":
         return "Long";
      case "float":
         return "Float";
      case "double":
         return "Double";
      }
      return attrType;
   }

   private static boolean isPrimitive(String attrType)
   {
      return !attrType.equals(getBoxType(attrType));
   }

   // --------------- Associations ---------------

   private void generateAssociations(Clazz clazz, FileFragmentMap fragmentMap)
   {
      for (AssocRole role : clazz.getRoles())
      {
         if (role.getName() == null)
         {
            continue; //=====================================
         }

         this.generateAssociation(clazz, fragmentMap, role);
      }
   }

   private void generateAssociation(Clazz clazz, FileFragmentMap fragmentMap, AssocRole role)
   {
      final STGroup group;
      if (Type.JAVA_FX.equals(role.getPropertyStyle()))
      {
         group = this.getSTGroup("org/fulib/templates/JavaFXassociations.stg");
         group.importTemplates(this.getSTGroup("org/fulib/templates/associations.stg"));
      }
      else
      {
         group = this.getSTGroup("org/fulib/templates/associations.stg");
      }

      final String roleName = role.getName();
      final String capRoleName = StrUtil.cap(roleName);
      final int cardinality = role.getCardinality();
      final String className = clazz.getName();
      final boolean modified = role.getModified();

      final AssocRole other = role.getOther();
      final String otherRoleName = other.getName();
      final int otherCardinality = other.getCardinality();
      final String otherClassName = other.getClazz().getName();

      String roleType = otherClassName;

      // provide empty_set in this class
      if (cardinality != Type.ONE && !Type.JAVA_FX.equals(role.getPropertyStyle()))
      {
         // add empty set decl
         roleType = String.format(role.getRoleType(), otherClassName);

         final ST emptySetDecl = group.getInstanceOf("emptySetDecl");
         emptySetDecl.add("roleName", roleName);
         emptySetDecl.add("otherClassName", otherClassName);
         emptySetDecl.add("roleType", roleType);
         fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":EMPTY_" + roleName, emptySetDecl.render(), 3, modified);
      }
      else
      {
         // remove empty set decl
         fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":EMPTY_" + roleName, "", 3, true);
      }

      final ST propertyDecl = group.getInstanceOf("propertyDecl");
      propertyDecl.add("roleName", roleName);
      fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":PROPERTY_" + roleName, propertyDecl.render(), 2, modified);

      final ST roleAttrDecl = group.getInstanceOf("roleAttrDecl");
      roleAttrDecl.add("roleName", roleName);
      roleAttrDecl.add("roleType", roleType);
      roleAttrDecl.add("toMany", cardinality != Type.ONE);
      roleAttrDecl.add("otherClassName", otherClassName);
      fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":" + roleName, roleAttrDecl.render(), 2, modified);

      if (Type.JAVA_FX.equals(role.getPropertyStyle()))
      {
         // add _init method
         final ST initMethod = group.getInstanceOf("initMethod");
         initMethod.add("roleName", roleName);
         initMethod.add("toMany", cardinality != Type.ONE);
         initMethod.add("myClassName", className);
         initMethod.add("otherClassName", otherClassName);
         initMethod.add("otherRoleName", otherRoleName);
         initMethod.add("otherToMany", otherCardinality != Type.ONE);
         fragmentMap.add(METHOD + ":_init" + capRoleName + "()", initMethod.render(), 2, modified);
      }
      else
      {
         // remove _init method
         fragmentMap.add(METHOD + ":_init" + capRoleName + "()", "", 2, true);
      }

      final ST getMethod = group.getInstanceOf("getMethod");
      getMethod.add("roleName", roleName);
      getMethod.add("toMany", cardinality != Type.ONE);
      getMethod.add("otherClassName", otherClassName);
      getMethod.add("roleType", roleType);
      fragmentMap.add(METHOD + ":get" + capRoleName + "()", getMethod.render(), 2, modified);

      if (cardinality != Type.ONE)
      {
         final ST withItem = group.getInstanceOf("withItem");
         withItem.add("myClassName", className);
         withItem.add("roleName", roleName);
         withItem.add("otherClassName", otherClassName);
         withItem.add("otherRoleName", otherRoleName);
         withItem.add("otherToMany", otherCardinality != Type.ONE);
         withItem.add("roleType", roleType);
         fragmentMap.add(METHOD + ":with" + capRoleName + "(" + otherClassName + ")", withItem.render(), 3, modified);

         final ST withArray = group.getInstanceOf("withArray");
         withArray.add("myClassName", className);
         withArray.add("roleName", roleName);
         withArray.add("otherClassName", otherClassName);
         fragmentMap
            .add(METHOD + ":with" + capRoleName + "(" + otherClassName + "...)", withArray.render(), 3, modified);

         final ST withColl = group.getInstanceOf("withColl");
         withColl.add("myClassName", className);
         withColl.add("roleName", roleName);
         withColl.add("otherClassName", otherClassName);
         fragmentMap
            .add(METHOD + ":with" + capRoleName + "(Collection<? extends " + otherClassName + ">)", withColl.render(),
                 3, modified);

         final ST withoutItem = group.getInstanceOf("withoutItem");
         withoutItem.add("myClassName", className);
         withoutItem.add("roleName", roleName);
         withoutItem.add("otherClassName", otherClassName);
         withoutItem.add("otherRoleName", otherRoleName);
         withoutItem.add("otherToMany", otherCardinality != Type.ONE);
         fragmentMap.add(METHOD + ":without" + capRoleName + "(" + otherClassName + ")", withoutItem.render(), 3, modified);

         final ST withoutArray = group.getInstanceOf("withoutArray");
         withoutArray.add("myClassName", className);
         withoutArray.add("roleName", roleName);
         withoutArray.add("otherClassName", otherClassName);
         fragmentMap
            .add(METHOD + ":without" + capRoleName + "(" + otherClassName + "...)", withoutArray.render(), 3, modified);

         final ST withoutColl = group.getInstanceOf("withoutColl");
         withoutColl.add("myClassName", className);
         withoutColl.add("roleName", roleName);
         withoutColl.add("otherClassName", otherClassName);
         fragmentMap.add(METHOD + ":without" + capRoleName + "(Collection<? extends " + otherClassName + ">)",
                         withoutColl.render(), 3, modified);

         // remove "set" method
         fragmentMap.add(METHOD + ":set" + capRoleName + "(" + otherClassName + ")", "", 3, true);
      }
      else
      {
         final ST attrSet = group.getInstanceOf("setMethod");
         attrSet.add("myClassName", className);
         attrSet.add("roleName", roleName);
         attrSet.add("otherClassName", otherClassName);
         attrSet.add("otherRoleName", otherRoleName);
         attrSet.add("otherToMany", otherCardinality != Type.ONE);
         fragmentMap.add(METHOD + ":set" + capRoleName + "(" + otherClassName + ")", attrSet.render(), 3, modified);

         // remove "with" and "without" methods
         fragmentMap.add(METHOD + ":with" + capRoleName + "(" + otherClassName + ")", "", 3, true);
         fragmentMap.add(METHOD + ":with" + capRoleName + "(" + otherClassName + "...)", "", 3, true);
         fragmentMap
            .add(METHOD + ":with" + capRoleName + "(Collection<? extends " + otherClassName + ">)", "", 3, true);

         fragmentMap.add(METHOD + ":without" + capRoleName + "(" + otherClassName + ")", "", 3, true);
         fragmentMap.add(METHOD + ":without" + capRoleName + "(" + otherClassName + "...)", "", 3, true);
         fragmentMap
            .add(METHOD + ":without" + capRoleName + "(Collection<? extends " + otherClassName + ">)", "", 3, true);
      }

      if (Type.JAVA_FX.equals(role.getPropertyStyle()) && cardinality == Type.ONE)
      {
         final ST propertyMethod = group.getInstanceOf("propertyMethod");
         propertyMethod.add("roleName", roleName);
         propertyMethod.add("otherClassName", otherClassName);
         fragmentMap.add(METHOD + ":" + roleName + "Property()", propertyMethod.render(), 3, modified);
      }
      else
      {
         fragmentMap.add(METHOD + ":" + roleName + "Property()", "", 3, true);
      }
   }

   // --------------- Methods ---------------

   private void generateMethods(Clazz clazz, FileFragmentMap fragmentMap)
   {
      for (FMethod method : clazz.getMethods())
      {
         this.generateMethod(fragmentMap, method);
      }
   }

   private void generateMethod(FileFragmentMap fragmentMap, FMethod method)
   {
      final String signature = method.getSignature();
      String methodBody = method.getMethodBody();
      if (methodBody == null)
      {
         methodBody = "      // hello world\n";
      }
      final String fragment = "   " + method.getDeclaration() + " { \n" + methodBody + "   }";

      fragmentMap.add(signature, fragment, 2, method.getModified());
   }

   // --------------- Additional Fragments ---------------

   private void generatePropertyChangeSupport(Clazz clazz, FileFragmentMap fragmentMap)
   {
      if (clazz.getAttributes().isEmpty() && clazz.getRoles().isEmpty())
      {
         return;
      }

      final STGroup group = this.getSTGroup("org/fulib/templates/propertyChangeSupport.stg");

      // TODO template
      final String listeners = "   protected PropertyChangeSupport listeners = null;";
      fragmentMap.add(FileFragmentMap.ATTRIBUTE + ":listeners", listeners, 2, clazz.getModified());

      final ST firePropertyChange = group.getInstanceOf("firePropertyChange");
      fragmentMap.add(METHOD + ":firePropertyChange(String,Object,Object)", firePropertyChange.render(), 2,
                      clazz.getModified());

      final ST addPCL1 = group.getInstanceOf("addPropertyChangeListener1");
      fragmentMap
         .add(METHOD + ":addPropertyChangeListener(PropertyChangeListener)", addPCL1.render(), 2, clazz.getModified());

      final ST addPCL2 = group.getInstanceOf("addPropertyChangeListener2");
      fragmentMap.add(METHOD + ":addPropertyChangeListener(String,PropertyChangeListener)", addPCL2.render(), 2,
                      clazz.getModified());

      final ST removePCL1 = group.getInstanceOf("removePropertyChangeListener1");
      fragmentMap.add(METHOD + ":removePropertyChangeListener(PropertyChangeListener)", removePCL1.render(), 2,
                      clazz.getModified());

      final ST removePCL2 = group.getInstanceOf("removePropertyChangeListener2");
      fragmentMap.add(METHOD + ":removePropertyChangeListener(String,PropertyChangeListener)", removePCL2.render(), 2,
                      clazz.getModified());
   }

   private void generateToString(Clazz clazz, FileFragmentMap fragmentMap)
   {
      ArrayList<String> nameList = new ArrayList<>();
      boolean modified = false;
      for (Attribute attr : clazz.getAttributes())
      {
         if (attr.getType().equals(Type.STRING))
         {
            nameList.add(attr.getName());
         }

         if (!modified && attr.getModified())
         {
            modified = true;
         }
      }

      final String fragment;
      if (nameList.isEmpty())
      {
         fragment = "";
      }
      else
      {
         final STGroup group = this.getSTGroup("org/fulib/templates/toString.stg");
         final ST toString = group.getInstanceOf("toString");
         toString.add("names", nameList);
         fragment = toString.render();
      }

      fragmentMap.add(METHOD + ":toString()", fragment, 2, modified);
   }

   private void generateRemoveYou(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final List<String> toOneList = new ArrayList<>();
      final List<String> toManyList = new ArrayList<>();
      final List<String> toOneAggregationList = new ArrayList<>();
      final List<String> toManyAggregationList = new ArrayList<>();
      final List<String> toManyTypes = new ArrayList<>();
      final List<Boolean> javaFXStyles = new ArrayList<>();

      for (AssocRole role : clazz.getRoles())
      {
         if (role.getName() == null)
         {
            continue; //=============================
         }

         if (role.getCardinality() == Type.ONE)
         {
            if (role.getAggregation())
            {
               toOneAggregationList.add(role.getName());
            }
            else
            {
               toOneList.add(role.getName());
            }
         }
         else
         {
            if (role.getAggregation())
            {
               toManyAggregationList.add(role.getName());
               toManyTypes.add(role.getOther().getClazz().getName());
            }
            else
            {
               toManyList.add(role.getName());
               javaFXStyles.add(Type.JAVA_FX.equals(role.getPropertyStyle()));
            }
         }
      }

      final STGroup group = this.getSTGroup("org/fulib/templates/removeYou.stg");
      final ST removeYou = group.getInstanceOf("removeYou");
      removeYou.add("toOneNames", toOneList);
      removeYou.add("toManyNames", toManyList);
      removeYou.add("toOneAggregations", toOneAggregationList);
      removeYou.add("toManyAggregations", toManyAggregationList);
      removeYou.add("toManyTypes", toManyTypes);
      removeYou.add("javaFXStyles", javaFXStyles);
      if (clazz.getSuperClass() != null)
      {
         removeYou.add("superClass", "yes");
      }

      fragmentMap.add(METHOD + ":removeYou()", removeYou.render(), 2, clazz.getModified());
   }
}
