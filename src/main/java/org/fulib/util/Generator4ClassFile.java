package org.fulib.util;

import org.fulib.builder.Type;
import org.fulib.classmodel.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.fulib.classmodel.FileFragmentMap.*;

public class Generator4ClassFile extends AbstractGenerator4ClassFile
{
   // =============== Constants ===============

   // indentation used for method bodies.
   // Used by generateMethod to normalize method bodies in preparation for ST's automatic indentation.
   private static final String METHOD_BODY_INDENT = "      ";

   private static final Pattern SIGNATURE_PATTERN = Pattern.compile("^\\s*(\\w+)\\s*:\\s*(.*)\\s*$");

   // =============== Properties ===============

   @Override
   public Generator4ClassFile setCustomTemplatesFile(String customTemplatesFile)
   {
      super.setCustomTemplatesFile(customTemplatesFile);
      return this;
   }

   // =============== Methods ===============

   @Override
   public String getSourceFileName(Clazz clazz)
   {
      return clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".java";
   }

   @Override
   public void generate(Clazz clazz, FileFragmentMap fragmentMap)
   {
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
      for (String importItem : clazz.getImports())
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
      if (clazz.getRoles().stream().anyMatch(AssocRole::isToMany) //
          || clazz.getAttributes().stream().anyMatch(Attribute::isCollection))
      {
         qualifiedNames.add("java.util.Collection");
         qualifiedNames.add("java.util.Collections");
         qualifiedNames.add("java.util.Arrays");
      }

      for (final AssocRole role : clazz.getRoles())
      {
         if (role.isToMany())
         {
            this.addCollectionTypeImports(role.getCollectionType(), qualifiedNames);
         }
      }

      for (final Attribute attribute : clazz.getAttributes())
      {
         if (attribute.isCollection())
         {
            this.addCollectionTypeImports(attribute.getCollectionType(), qualifiedNames);
         }
      }
   }

   private void addCollectionTypeImports(CollectionType collectionType, Set<String> qualifiedNames)
   {
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

      this.generateFromSignatures(fragmentMap, group, "attrSignatures", attr.getModified(), st -> st.add("attr", attr));
   }

   private void generateFromSignatures(FileFragmentMap fragmentMap, STGroup group, String signaturesTemplate,
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
            final int newLines = signature.startsWith("attribute:") ? FIELD_NEWLINES : METHOD_NEWLINES;
            final ST namedST = group.getInstanceOf(templateName);
            addTarget.accept(namedST);
            fragmentMap.add(signature, namedST.render(), newLines);
         }
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

      this.generateFromSignatures(fragmentMap, group, "roleSignatures", role.getModified(),
                                  st -> st.add("role", role).add("other", role.getOther()));
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

      final String finalBody = body;
      final STGroup group = this.getSTGroup("org/fulib/templates/method.stg");
      addOrRemove(fragmentMap, method.getSignature(), METHOD_NEWLINES, method.getModified(),
                  () -> group.getInstanceOf("method").add("method", method).add("body", finalBody).render());
   }

   // --------------- Additional Fragments ---------------

   private void generatePropertyChangeSupport(Clazz clazz, FileFragmentMap fragmentMap)
   {
      if (clazz.getAttributes().isEmpty() && clazz.getRoles().isEmpty())
      {
         return;
      }

      final STGroup group = this.getSTGroup("org/fulib/templates/propertyChangeSupport.stg");
      this.generateFromSignatures(fragmentMap, group, "propertyChangeSignatures", clazz.getModified(), st -> {});
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
      addOrRemove(fragmentMap, METHOD + ":removeYou()", METHOD_NEWLINES, clazz.getModified(), () -> group
         .getInstanceOf("removeYou")
         .add("superClass", clazz.getSuperClass() != null)
         .add("roles", clazz.getRoles().stream().filter(r -> r.getName() != null).toArray())
         .render());
   }

   // --------------- Helpers ---------------

   private static void addOrRemove(FileFragmentMap map, String key, int newLines, boolean modified,
      Supplier<? extends String> template)
   {
      if (modified)
      {
         map.remove(key);
      }
      else
      {
         map.add(key, template.get(), newLines);
      }
   }
}
