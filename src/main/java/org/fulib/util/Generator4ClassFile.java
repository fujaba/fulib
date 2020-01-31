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
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import static org.fulib.classmodel.FileFragmentMap.*;

public class Generator4ClassFile extends AbstractGenerator
{
   // =============== Constants ===============

   // indentation used for method bodies.
   // Used by generateMethod to normalize method bodies in preparation for ST's automatic indentation.
   private static final String METHOD_BODY_INDENT = "      ";

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

      fragmentMap.add(CLASS_END, "}", CLASS_END_NEWLINES);

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
      fragmentMap.add(PACKAGE, packageDecl.render(), PACKAGE_NEWLINES);
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
         fragmentMap.add(IMPORT + ":" + qualifiedName, importDecl.render(), IMPORT_NEWLINES);
      }

      for (final String qualifiedName : staticImports)
      {
         final ST importDecl = group.getInstanceOf("importDecl");
         importDecl.add("qualifiedName", qualifiedName);
         importDecl.add("static", true);
         fragmentMap.add(IMPORT + ":" + qualifiedName, importDecl.render(), IMPORT_NEWLINES);
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
      if (clazz.getRoles().stream().anyMatch(AssocRole::isJavaFX) //
          || clazz.getAttributes().stream().anyMatch(Attribute::isJavaFX))
      {
         qualifiedNames.add("javafx.beans.property.*");
      }

      // ArrayList required by removeYou template
      if (clazz.getRoles().stream().anyMatch(r -> r.isToMany() && (r.getAggregation() || !r.isJavaFX())))
      {
         qualifiedNames.add("java.util.ArrayList");
      }

      // any non-primitive attributes
      if (clazz.getAttributes().stream().anyMatch(a -> !a.isCollection() && !a.isPrimitive()))
      {
         qualifiedNames.add("java.util.Objects");
      }

      // any to-n roles or to-n attributes
      if (clazz.getRoles().stream().anyMatch(a -> a.getCardinality() != Type.ONE) //
          || clazz.getAttributes().stream().anyMatch(Attribute::isCollection))
      {
         qualifiedNames.add("java.util.Collection");
         qualifiedNames.add("java.util.Collections");
         qualifiedNames.add("java.util.Arrays");
      }

      for (final AssocRole role : clazz.getRoles())
      {
         this.addCollectionTypeImports(role.getCollectionType(), qualifiedNames);
      }

      for (final Attribute attribute : clazz.getAttributes())
      {
         this.addCollectionTypeImports(attribute.getCollectionType(), qualifiedNames);
      }
   }

   private void addCollectionTypeImports(CollectionType collectionType, Set<String> qualifiedNames)
   {
      if (collectionType == null)
      {
         return;
      }

      qualifiedNames.add(collectionType.getItf().getQualifiedName());
      qualifiedNames.add(collectionType.getQualifiedImplName());
   }

   private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/declarations.stg");
      final ST classDecl = group.getInstanceOf("classDecl");
      classDecl.add("name", clazz.getName());
      classDecl.add("superClass", clazz.getSuperClass() != null ? clazz.getSuperClass().getName() : null);
      fragmentMap.add(CLASS, classDecl.render(), CLASS_NEWLINES);
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
      final STGroup group = this.getSTGroup(
         "org/fulib/templates/attributes." + attr.getPropertyStyle().toLowerCase() + ".stg");

      final String baseType = attr.getType();
      final String boxType = attr.getBoxType();

      final String attrName = attr.getName();
      final String capAttrName = StrUtil.cap(attrName);
      final boolean modified = attr.getModified();

      final ST propertyDecl = group.getInstanceOf("propertyDecl").add("attr", attr);
      fragmentMap.add(ATTRIBUTE + ":PROPERTY_" + attrName, propertyDecl.render(), FIELD_NEWLINES, modified);

      final ST attrDecl = group.getInstanceOf("attrDecl").add("attr", attr);
      fragmentMap.add(ATTRIBUTE + ":" + attrName, attrDecl.render(), FIELD_NEWLINES, modified);

      if (attr.isJavaFX())
      {
         final ST initMethod = group.getInstanceOf("initMethod").add("attr", attr);
         fragmentMap.add(METHOD + ":_init" + capAttrName + "()", initMethod.render(), METHOD_NEWLINES, modified);
      }
      else
      {
         fragmentMap.remove(METHOD + ":_init" + capAttrName + "()");
      }

      final ST attrGet = group.getInstanceOf("attrGet").add("attr", attr);
      fragmentMap.add(METHOD + ":get" + capAttrName + "()", attrGet.render(), METHOD_NEWLINES, modified);

      if (attr.isCollection())
      {
         final ST attrWithItem = group.getInstanceOf("attrWithItem").add("attr", attr);
         fragmentMap.add(METHOD + ":with" + capAttrName + "(" + boxType + ")", attrWithItem.render(), METHOD_NEWLINES,
                         modified);

         final ST attrWithArray = group.getInstanceOf("attrWithArray").add("attr", attr);
         fragmentMap.add(METHOD + ":with" + capAttrName + "(" + boxType + "...)", attrWithArray.render(),
                         METHOD_NEWLINES, modified);

         final ST attrWithColl = group.getInstanceOf("attrWithColl").add("attr", attr);
         fragmentMap.add(METHOD + ":with" + capAttrName + "(Collection<? extends " + boxType + ">)",
                         attrWithColl.render(), METHOD_NEWLINES, modified);

         final ST attrWithoutItem = group.getInstanceOf("attrWithoutItem").add("attr", attr);
         fragmentMap.add(METHOD + ":without" + capAttrName + "(" + boxType + ")", attrWithoutItem.render(),
                         METHOD_NEWLINES, modified);

         final ST attrWithoutArray = group.getInstanceOf("attrWithoutArray").add("attr", attr);
         fragmentMap.add(METHOD + ":without" + capAttrName + "(" + boxType + "...)", attrWithoutArray.render(),
                         METHOD_NEWLINES, modified);

         final ST attrWithoutColl = group.getInstanceOf("attrWithoutColl").add("attr", attr);
         fragmentMap.add(METHOD + ":without" + capAttrName + "(Collection<? extends " + boxType + ">)",
                         attrWithoutColl.render(), METHOD_NEWLINES, modified);

         // remove "set" method
         fragmentMap.remove(METHOD + ":set" + capAttrName + "(" + baseType + ")");
      }
      else // usual attribute
      {
         final ST attrSet = group.getInstanceOf("attrSet").add("attr", attr);
         fragmentMap.add(METHOD + ":set" + capAttrName + "(" + baseType + ")", attrSet.render(), METHOD_NEWLINES,
                         modified);

         // remove "with" and "without" methods
         fragmentMap.remove(METHOD + ":with" + capAttrName + "(" + boxType + ")");
         fragmentMap.remove(METHOD + ":with" + capAttrName + "(" + boxType + "...)");
         fragmentMap.remove(METHOD + ":with" + capAttrName + "(Collection<? extends " + boxType + ">)");
         fragmentMap.remove(METHOD + ":without" + capAttrName + "(" + boxType + ")");
         fragmentMap.remove(METHOD + ":without" + capAttrName + "(" + boxType + "...)");
         fragmentMap.remove(METHOD + ":without" + capAttrName + "(Collection<? extends " + boxType + ">)");
      }

      if (attr.isJavaFX())
      {
         final ST propertyGet = group.getInstanceOf("propertyGet").add("attr", attr);
         fragmentMap.add(METHOD + ":" + attrName + "Property()", propertyGet.render(), METHOD_NEWLINES, modified);
      }
      else
      {
         fragmentMap.remove(METHOD + ":" + attrName + "Property()");
      }
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

         this.generateAssociation(fragmentMap, role);
      }
   }

   private void generateAssociation(FileFragmentMap fragmentMap, AssocRole role)
   {
      final STGroup group = this.getSTGroup(
         "org/fulib/templates/associations." + role.getPropertyStyle().toLowerCase() + ".stg");
      final boolean javaFX = role.isJavaFX();

      final String roleName = role.getName();
      final String capRoleName = StrUtil.cap(roleName);
      final boolean toMany = role.isToMany();
      final boolean modified = role.getModified();

      final AssocRole other = role.getOther();
      final String otherClassName = other.getClazz().getName();

      final ST propertyDecl = group.getInstanceOf("propertyDecl").add("role", role).add("other", other);
      fragmentMap.add(ATTRIBUTE + ":PROPERTY_" + roleName, propertyDecl.render(), FIELD_NEWLINES, modified);

      final ST roleAttrDecl = group.getInstanceOf("roleAttrDecl").add("role", role).add("other", other);
      fragmentMap.add(ATTRIBUTE + ":" + roleName, roleAttrDecl.render(), FIELD_NEWLINES, modified);

      if (javaFX)
      {
         // add _init method
         final ST initMethod = group.getInstanceOf("initMethod").add("role", role).add("other", other);
         fragmentMap.add(METHOD + ":_init" + capRoleName + "()", initMethod.render(), METHOD_NEWLINES, modified);
      }
      else
      {
         // remove _init method
         fragmentMap.remove(METHOD + ":_init" + capRoleName + "()");
      }

      final ST getMethod = group.getInstanceOf("getMethod").add("role", role).add("other", other);
      fragmentMap.add(METHOD + ":get" + capRoleName + "()", getMethod.render(), METHOD_NEWLINES, modified);

      if (toMany)
      {
         final ST withItem = group.getInstanceOf("withItem").add("role", role).add("other", other);
         fragmentMap.add(METHOD + ":with" + capRoleName + "(" + otherClassName + ")", withItem.render(),
                         METHOD_NEWLINES, modified);

         final ST withArray = group.getInstanceOf("withArray").add("role", role).add("other", other);
         fragmentMap.add(METHOD + ":with" + capRoleName + "(" + otherClassName + "...)", withArray.render(),
                         METHOD_NEWLINES, modified);

         final ST withColl = group.getInstanceOf("withColl").add("role", role).add("other", other);
         fragmentMap.add(METHOD + ":with" + capRoleName + "(Collection<? extends " + otherClassName + ">)",
                         withColl.render(), METHOD_NEWLINES, modified);

         final ST withoutItem = group.getInstanceOf("withoutItem").add("role", role).add("other", other);
         fragmentMap.add(METHOD + ":without" + capRoleName + "(" + otherClassName + ")", withoutItem.render(),
                         METHOD_NEWLINES, modified);

         final ST withoutArray = group.getInstanceOf("withoutArray").add("role", role).add("other", other);
         fragmentMap.add(METHOD + ":without" + capRoleName + "(" + otherClassName + "...)", withoutArray.render(),
                         METHOD_NEWLINES, modified);

         final ST withoutColl = group.getInstanceOf("withoutColl").add("role", role).add("other", other);
         fragmentMap.add(METHOD + ":without" + capRoleName + "(Collection<? extends " + otherClassName + ">)",
                         withoutColl.render(), METHOD_NEWLINES, modified);

         // remove "set" method
         fragmentMap.remove(METHOD + ":set" + capRoleName + "(" + otherClassName + ")");
      }
      else
      {
         final ST attrSet = group.getInstanceOf("setMethod").add("role", role).add("other", other);
         fragmentMap.add(METHOD + ":set" + capRoleName + "(" + otherClassName + ")", attrSet.render(), METHOD_NEWLINES,
                         modified);

         // remove "with" and "without" methods
         fragmentMap.remove(METHOD + ":with" + capRoleName + "(" + otherClassName + ")");
         fragmentMap.remove(METHOD + ":with" + capRoleName + "(" + otherClassName + "...)");
         fragmentMap.remove(METHOD + ":with" + capRoleName + "(Collection<? extends " + otherClassName + ">)");
         fragmentMap.remove(METHOD + ":without" + capRoleName + "(" + otherClassName + ")");
         fragmentMap.remove(METHOD + ":without" + capRoleName + "(" + otherClassName + "...)");
         fragmentMap.remove(METHOD + ":without" + capRoleName + "(Collection<? extends " + otherClassName + ">)");
      }

      if (javaFX && !toMany)
      {
         final ST propertyMethod = group.getInstanceOf("propertyMethod").add("role", role).add("other", other);
         fragmentMap.add(METHOD + ":" + roleName + "Property()", propertyMethod.render(), METHOD_NEWLINES, modified);
      }
      else
      {
         fragmentMap.remove(METHOD + ":" + roleName + "Property()");
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
      String body = method.getMethodBody();
      if (body.startsWith(METHOD_BODY_INDENT))
      {
         // remove indent from all body lines, e.g.:
         // ......foo bar
         // ......baz
         // ->
         // foo bar
         // baz
         body = body.substring(METHOD_BODY_INDENT.length()).replace("\n" + METHOD_BODY_INDENT, "\n");
      }
      if (body.endsWith("\n"))
      {
         body = body.substring(0, body.length() - 1);
      }

      final STGroup group = this.getSTGroup("org/fulib/templates/method.stg");
      final ST template = group.getInstanceOf("method");
      template.add("method", method);
      template.add("body", body);
      fragmentMap.add(method.getSignature(), template.render(), METHOD_NEWLINES, method.getModified());
   }

   // --------------- Additional Fragments ---------------

   private void generatePropertyChangeSupport(Clazz clazz, FileFragmentMap fragmentMap)
   {
      if (clazz.getAttributes().isEmpty() && clazz.getRoles().isEmpty())
      {
         return;
      }

      final STGroup group = this.getSTGroup("org/fulib/templates/propertyChangeSupport.stg");

      final ST listenersField = group.getInstanceOf("listenersField");
      fragmentMap.add(ATTRIBUTE + ":listeners", listenersField.render(), FIELD_NEWLINES, clazz.getModified());

      final ST firePropertyChange = group.getInstanceOf("firePropertyChange");
      fragmentMap.add(METHOD + ":firePropertyChange(String,Object,Object)", firePropertyChange.render(),
                      METHOD_NEWLINES, clazz.getModified());

      final ST addPCL1 = group.getInstanceOf("addPropertyChangeListener1");
      fragmentMap.add(METHOD + ":addPropertyChangeListener(PropertyChangeListener)", addPCL1.render(), METHOD_NEWLINES,
                      clazz.getModified());

      final ST addPCL2 = group.getInstanceOf("addPropertyChangeListener2");
      fragmentMap.add(METHOD + ":addPropertyChangeListener(String,PropertyChangeListener)", addPCL2.render(),
                      METHOD_NEWLINES, clazz.getModified());

      final ST removePCL1 = group.getInstanceOf("removePropertyChangeListener1");
      fragmentMap.add(METHOD + ":removePropertyChangeListener(PropertyChangeListener)", removePCL1.render(),
                      METHOD_NEWLINES, clazz.getModified());

      final ST removePCL2 = group.getInstanceOf("removePropertyChangeListener2");
      fragmentMap.add(METHOD + ":removePropertyChangeListener(String,PropertyChangeListener)", removePCL2.render(),
                      METHOD_NEWLINES, clazz.getModified());
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
         toString.add("superClass", clazz.getSuperClass() != null);
         fragment = toString.render();
      }

      fragmentMap.add(METHOD + ":toString()", fragment, METHOD_NEWLINES, modified);
   }

   private void generateRemoveYou(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/removeYou.stg");
      final ST removeYou = group.getInstanceOf("removeYou");
      removeYou.add("superClass", clazz.getSuperClass() != null);
      removeYou.add("roles", clazz.getRoles().stream().filter(r -> r.getName() != null).toArray());
      fragmentMap.add(METHOD + ":removeYou()", removeYou.render(), METHOD_NEWLINES, clazz.getModified());
   }
}
