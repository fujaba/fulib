package org.fulib.util;

import org.fulib.builder.Type;
import org.fulib.classmodel.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.fulib.classmodel.FileFragmentMap.*;

public class Generator4ClassFile extends AbstractGenerator4ClassFile
{
   // =============== Constants ===============

   // indentation used for method bodies.
   // Used by generateMethod to normalize method bodies in preparation for ST's automatic indentation.
   private static final String METHOD_BODY_INDENT = "      ";

   // =============== Properties ===============

   /**
    * @since 1.2
    */
   @Override
   public Generator4ClassFile setCustomTemplatesFile(String customTemplatesFile)
   {
      super.setCustomTemplatesFile(customTemplatesFile);
      return this;
   }

   // =============== Methods ===============

   /**
    * @since 1.2
    */
   @Override
   public String getSourceFileName(Clazz clazz)
   {
      return clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".java";
   }

   /**
    * @since 1.2
    */
   @Override
   public void generate(Clazz clazz, FileFragmentMap fragmentMap)
   {
      this.generatePackageDecl(clazz, fragmentMap);
      this.generateImports(clazz, fragmentMap);
      this.generateClassDecl(clazz, fragmentMap);

      this.generateAttributes(clazz, fragmentMap);
      this.generateAssociations(clazz, fragmentMap);
      this.generateMethods(clazz, fragmentMap);
      this.generatePropertyChangeSupport(clazz, fragmentMap);
      this.generateToString(clazz, fragmentMap);
      this.generateRemoveYou(clazz, fragmentMap);

      fragmentMap.add(CLASS + '/' + clazz.getName() + '/' + CLASS_END, "}", CLASS_END_NEWLINES);
      fragmentMap.add(EOF, "", 1);
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

      final STGroup group = this.getImportGroup();
      for (final String qualifiedName : qualifiedNames)
      {
         this.addImport(fragmentMap, group, qualifiedName, false);
      }

      for (final String qualifiedName : staticImports)
      {
         this.addImport(fragmentMap, group, qualifiedName, true);
      }

      if (qualifiedNames.isEmpty() && staticImports.isEmpty())
      {
         // we still need to make an import section in the right place,
         // in case one of the templates requires any
         fragmentMap.add(IMPORT + "/#start", "", 0);
      }
   }

   private void addDefaultImports(Clazz clazz, Set<String> qualifiedNames)
   {
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
      fragmentMap.add(CLASS + '/' + clazz.getName() + '/' + CLASS_DECL, classDecl.render(), CLASS_NEWLINES);
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

      final String signature = method.getSignature();
      if (method.getModified())
      {
         fragmentMap.remove(signature);
      }
      else
      {
         final STGroup group = this.getSTGroup("org/fulib/templates/method.stg");
         final ST method1 = group.getInstanceOf("method");
         method1.add("method", method);
         method1.add("body", body);
         fragmentMap.add(signature, method1.render(), METHOD_NEWLINES);
      }
   }

   // --------------- Additional Fragments ---------------

   private void generatePropertyChangeSupport(Clazz clazz, FileFragmentMap fragmentMap)
   {
      if (clazz.getAttributes().isEmpty() && clazz.getRoles().isEmpty())
      {
         return;
      }

      final STGroup group = this.getSTGroup("org/fulib/templates/propertyChangeSupport.stg");
      this.generateFromSignatures(fragmentMap, group, "propertyChangeSignatures", clazz.getModified(),
                                  st -> st.add("clazz", clazz));
   }

   private void generateToString(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final List<String> nameList = clazz
         .getAttributes()
         .stream()
         .filter(a -> Type.STRING.equals(a.getType()))
         .map(Attribute::getName)
         .collect(Collectors.toList());

      final STGroup group = this.getSTGroup("org/fulib/templates/toString.stg");
      this.generateFromSignatures(fragmentMap, group, "toStringSignatures", nameList.isEmpty() || clazz.getModified(),
                                  st -> st.add("clazz", clazz).add("names", nameList));
   }

   private void generateRemoveYou(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("org/fulib/templates/removeYou.stg");
      final Object[] roles = getRolesForRemoveYou(clazz).toArray();
      final boolean superCall = needsSuperCallForRemoveYou(clazz);
      this.generateFromSignatures(fragmentMap, group, "removeYouSignatures", roles.length == 0 || clazz.getModified(),
                                  st -> st.add("clazz", clazz).add("roles", roles).add("superCall", superCall));
   }

   private Stream<AssocRole> getRolesForRemoveYou(Clazz clazz)
   {
      return clazz.getRoles().stream().filter(r -> r.getName() != null);
   }

   private boolean needsSuperCallForRemoveYou(Clazz clazz)
   {
      Clazz superClazz = clazz;
      while ((superClazz = superClazz.getSuperClass()) != null)
      {
         if (getRolesForRemoveYou(superClazz).findAny().isPresent())
         {
            return true;
         }
      }
      return false;
   }
}
