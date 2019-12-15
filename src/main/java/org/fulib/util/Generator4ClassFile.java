package org.fulib.util;

import org.fulib.Generator;
import org.fulib.Parser;
import org.fulib.StrUtil;
import org.fulib.builder.Type;
import org.fulib.classmodel.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

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
      FileFragmentMap fragmentMap = Parser.parse(classFileName);

      // doGenerate code for class
      this.generatePackageDecl(clazz, fragmentMap);

      this.generateClassDecl(clazz, fragmentMap);

      this.generateAttributes(clazz, fragmentMap);

      this.generateAssociations(clazz, fragmentMap);

      this.generateMethods(clazz, fragmentMap);

      this.generatePropertyChangeSupport(clazz, fragmentMap);

      this.generateToString(clazz, fragmentMap);

      this.generateRemoveYou(clazz, fragmentMap);

      fragmentMap.add(Parser.CLASS_END, "}", 1);

      this.generateImports(clazz, fragmentMap);

      if (clazz.getModified() && fragmentMap.classBodyIsEmpty(fragmentMap))
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
         this.compressBlankLines(fragmentMap);
         fragmentMap.writeFile();
      }
   }

   private void compressBlankLines(FileFragmentMap fragmentMap)
   {
      ArrayList<CodeFragment> fragmentList = fragmentMap.getFragmentList();
      int noOfBlankLines = 0;

      for (CodeFragment firstFragment : fragmentList)
      {
         if (!firstFragment.getText().matches("\\s*"))
         {
            noOfBlankLines = 0;
            continue;
         }

         for (int pos = firstFragment.getText().length() - 1; pos >= 0; pos--)
         {
            if (firstFragment.getText().charAt(pos) != '\n')
            {
               continue;
            }

            noOfBlankLines++;
            if (noOfBlankLines == 2)
            {
               firstFragment.setText(firstFragment.getText().substring(pos));
               break;
            }
            if (noOfBlankLines > 2)
            {
               firstFragment.setText(firstFragment.getText().substring(pos + 1));
               break;
            }
         }
      }
   }

   private void generatePackageDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      // TODO template?
      String result = String.format("package %s;", clazz.getModel().getPackageName());
      fragmentMap.add(Parser.PACKAGE, result, 2);
   }

   private void generateImports(Clazz clazz, FileFragmentMap fragmentMap)
   {
      for (String imp : clazz.getImportList())
      {
         // TODO not sure why this is here
         String[] split = imp.split(" ");
         String key = split[split.length - 1];
         key = key.substring(0, key.length() - 1);
         fragmentMap.add(Parser.IMPORT + ":" + key, imp, 1);
      }
   }

   private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      final STGroup group = this.getSTGroup("templates/classDecl.stg");
      final ST classDecl = group.getInstanceOf("classDecl");
      classDecl.add("name", clazz.getName());
      classDecl.add("superClass", clazz.getSuperClass() != null ? clazz.getSuperClass().getName() : null);
      fragmentMap.add(Parser.CLASS, classDecl.render(), 2);
   }

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
         group = this.getSTGroup("templates/JavaFXattributes.stg");
         fragmentMap.add(Parser.IMPORT + ":javafx.beans.property.*", "import javafx.beans.property.*;", 1);
      }
      else
      {
         group = this.getSTGroup("templates/attributes.stg");
      }

      String attrType = attr.getType();
      if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
      {
         if ("int".equals(attrType))
         {
            attrType = "Integer";
         }
         else
         {
            attrType = StrUtil.cap(attrType);
         }
      }

      String baseType = attrType;
      String boxType = baseType;
      if (attrType.endsWith(Type.__LIST))
      {
         baseType = attrType.substring(0, attrType.length() - Type.__LIST.length());
         if ("int".equals(baseType))
         {
            boxType = "Integer";
         }
         else
         {
            boxType = StrUtil.cap(baseType);
         }
         attrType = String.format("java.util.ArrayList<%s>", boxType);
      }

      final String capAttrName = StrUtil.cap(attr.getName());

      final ST propertyDecl = group.getInstanceOf("propertyDecl");
      propertyDecl.add("name", attr.getName());
      fragmentMap.add(Parser.ATTRIBUTE + ":PROPERTY_" + attr.getName(), propertyDecl.render(), 2, attr.getModified());

      final ST attrDecl = group.getInstanceOf("attrDecl");
      attrDecl.add("type", attrType);
      attrDecl.add("name", attr.getName());
      attrDecl.add("value", attr.getInitialization());
      fragmentMap.add(Parser.ATTRIBUTE + ":" + attr.getName(), attrDecl.render(), 2, attr.getModified());

      if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
      {
         final ST initMethod = group.getInstanceOf("initMethod");
         initMethod.add("name", attr.getName());
         initMethod.add("type", attrType);
         fragmentMap
            .add(Parser.METHOD + ":_init" + capAttrName + "()", initMethod.render(), 2, attr.getModified());
      }
      else
      {
         fragmentMap.add(Parser.METHOD + ":_init" + capAttrName + "()", "", 2, true);
      }

      final ST attrGet = group.getInstanceOf("attrGet");
      attrGet.add("type", attrType);
      attrGet.add("name", attr.getName());
      fragmentMap.add(Parser.METHOD + ":get" + capAttrName + "()", attrGet.render(), 2, attr.getModified());

      if (attr.getType().endsWith(Type.__LIST))
      {
         final ST attrWith = group.getInstanceOf("attrWith");
         attrWith.add("class", attr.getClazz().getName());
         attrWith.add("listType", attrType);
         attrWith.add("baseType", boxType);
         attrWith.add("name", attr.getName());
         fragmentMap.add(Parser.METHOD + ":with" + capAttrName + "(Object...)", attrWith.render(), 3,
                         attr.getModified());

         final ST attrWithout = group.getInstanceOf("attrWithout");
         attrWithout.add("class", attr.getClazz().getName());
         attrWithout.add("listType", attrType);
         attrWithout.add("baseType", boxType);
         attrWithout.add("name", attr.getName());
         fragmentMap.add(Parser.METHOD + ":without" + capAttrName + "(Object...)", attrWithout.render(), 3,
                         attr.getModified());
      }
      else // usual attribute
      {
         final ST attrSet = group.getInstanceOf("attrSet");
         attrSet.add("class", attr.getClazz().getName());
         attrSet.add("type", attr.getType());
         attrSet.add("name", attr.getName());
         fragmentMap
            .add(Parser.METHOD + ":set" + capAttrName + "(" + attr.getType() + ")", attrSet.render(), 3,
                 attr.getModified());
      }

      if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
      {
         final ST propertyGet = group.getInstanceOf("propertyGet");
         propertyGet.add("name", attr.getName());
         propertyGet.add("type", attrType);
         fragmentMap.add(Parser.METHOD + ":" + attr.getName() + "Property()", propertyGet.render(), 3, attr.getModified());
      }
      else
      {
         fragmentMap.add(Parser.METHOD + ":" + attr.getName() + "Property()", "", 3, true);
      }
   }

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
         group = this.getSTGroup("templates/JavaFXassociations.stg");
         fragmentMap.add(Parser.IMPORT + ":javafx.beans.property.*", "import javafx.beans.property.*;", 1);
      }
      else
      {
         group = this.getSTGroup("templates/associations.stg");
      }

      String roleType = role.getOther().getClazz().getName();

      // provide empty_set in this class
      if (role.getCardinality() != Type.ONE && !Type.JAVA_FX.equals(role.getPropertyStyle()))
      {
         // add empty set decl
         roleType = String.format(role.getRoleType(), role.getOther().getClazz().getName());

         final ST emptySetDecl = group.getInstanceOf("emptySetDecl");
         emptySetDecl.add("roleName", role.getName());
         emptySetDecl.add("otherClassName", role.getOther().getClazz().getName());
         emptySetDecl.add("roleType", roleType);
         fragmentMap.add(Parser.ATTRIBUTE + ":EMPTY_" + role.getName(), emptySetDecl.render(), 3, role.getModified());
      }

      final ST propertyDecl = group.getInstanceOf("propertyDecl");
      propertyDecl.add("roleName", role.getName());
      fragmentMap.add(Parser.ATTRIBUTE + ":PROPERTY_" + role.getName(), propertyDecl.render(), 2, role.getModified());

      final ST roleAttrDecl = group.getInstanceOf("roleAttrDecl");
      roleAttrDecl.add("roleName", role.getName());
      roleAttrDecl.add("roleType", roleType);
      roleAttrDecl.add("toMany", role.getCardinality() != Type.ONE);
      roleAttrDecl.add("otherClassName", role.getOther().getClazz().getName());
      fragmentMap.add(Parser.ATTRIBUTE + ":" + role.getName(), roleAttrDecl.render(), 2, role.getModified());

      final String capRoleName = StrUtil.cap(role.getName());
      if (Type.JAVA_FX.equals(role.getPropertyStyle()))
      {
         // remove empty set decl
         fragmentMap.add(Parser.ATTRIBUTE + ":EMPTY_" + role.getName(), "", 3, true);

         // add _init method
         final ST initMethod = group.getInstanceOf("initMethod");
         initMethod.add("roleName", role.getName());
         initMethod.add("toMany", role.getCardinality() != Type.ONE);
         initMethod.add("myClassName", clazz.getName());
         initMethod.add("otherClassName", role.getOther().getClazz().getName());
         initMethod.add("otherRoleName", role.getOther().getName());
         initMethod.add("otherToMany", role.getOther().getCardinality() != Type.ONE);
         fragmentMap.add(Parser.METHOD + ":_init" + capRoleName + "()", initMethod.render(), 2, role.getModified());
      }
      else
      {
         // remove _init method
         fragmentMap.add(Parser.METHOD + ":_init" + capRoleName + "()", "", 2, true);
      }

      final ST getMethod = group.getInstanceOf("getMethod");
      getMethod.add("roleName", role.getName());
      getMethod.add("toMany", role.getCardinality() != Type.ONE);
      getMethod.add("otherClassName", role.getOther().getClazz().getName());
      getMethod.add("roleType", roleType);
      fragmentMap.add(Parser.METHOD + ":get" + capRoleName + "()", getMethod.render(), 2, role.getModified());

      final ST setMethod = group.getInstanceOf("setMethod");
      setMethod.add("roleName", role.getName());
      setMethod.add("toMany", role.getCardinality() != Type.ONE);
      setMethod.add("myClassName", clazz.getName());
      setMethod.add("otherClassName", role.getOther().getClazz().getName());
      setMethod.add("otherRoleName", role.getOther().getName());
      setMethod.add("otherToMany", role.getOther().getCardinality() != Type.ONE);
      setMethod.add("roleType", roleType);

      String signature = "set";
      String paramType = role.getOther().getClazz().getName();
      if (role.getCardinality() != Type.ONE)
      {
         signature = "with";
         paramType = "Object...";
      }
      if (Type.JAVA_FX.equals(role.getPropertyStyle()))
      {
         if (role.getCardinality() != Type.ONE)
         {
            // remove withXY(Object...) method
            String oldSignature = "with" + capRoleName + "(" + paramType + ")";
            fragmentMap.add(Parser.METHOD + ":" + oldSignature, "", 3, true);
         }
         paramType = role.getOther().getClazz().getName();
      }
      else
      {
         // remove withXY(OtherClass)
         String oldSignature = "with" + capRoleName + "(" + role.getOther().getClazz().getName() + ")";
         fragmentMap.add(Parser.METHOD + ":" + oldSignature, "", 3, true);
      }

      signature += capRoleName + "(" + paramType + ")";

      fragmentMap.add(Parser.METHOD + ":" + signature, setMethod.render(), 3, role.getModified());

      if (role.getCardinality() != Type.ONE)
      {
         final ST withoutMethod = group.getInstanceOf("withoutMethod");
         withoutMethod.add("roleName", role.getName());
         withoutMethod.add("toMany", role.getCardinality() != Type.ONE);
         withoutMethod.add("myClassName", clazz.getName());
         withoutMethod.add("otherClassName", role.getOther().getClazz().getName());
         withoutMethod.add("otherRoleName", role.getOther().getName());
         withoutMethod.add("otherToMany", role.getOther().getCardinality() != Type.ONE);
         withoutMethod.add("roleType", roleType);

         paramType = "Object...";
         if (Type.JAVA_FX.equals(role.getPropertyStyle()))
         {
            paramType = role.getOther().getClazz().getName();
         }
         fragmentMap.add(Parser.METHOD + ":without" + capRoleName + "(" + paramType + ")", withoutMethod.render(), 3,
                         role.getModified());
      }

      if (Type.JAVA_FX.equals(role.getPropertyStyle()) && role.getCardinality() == Type.ONE)
      {
         final ST propertyMethod = group.getInstanceOf("propertyMethod");
         propertyMethod.add("roleName", role.getName());
         propertyMethod.add("otherClassName", role.getOther().getClazz().getName());
         fragmentMap
            .add(Parser.METHOD + ":" + role.getName() + "Property()", propertyMethod.render(), 3, role.getModified());
      }
      else
      {
         fragmentMap.add(Parser.METHOD + ":" + role.getName() + "Property()", "", 3, true);
      }
   }

   private void generateMethods(Clazz clazz, FileFragmentMap fragmentMap)
   {
      for (FMethod method : clazz.getMethods())
      {
         this.generateMethod(fragmentMap, method);
      }
   }

   private void generateMethod(FileFragmentMap fragmentMap, FMethod method)
   {
      final String signature = method.readSignature();
      String methodBody = method.getMethodBody();
      if (methodBody == null)
      {
         methodBody = "      // hello world\n";
      }
      final String fragment = "   " + method.getDeclaration() + " { \n" + methodBody + "   }";

      fragmentMap.add(signature, fragment, 2, method.getModified());
   }

   private void generatePropertyChangeSupport(Clazz clazz, FileFragmentMap fragmentMap)
   {
      if (clazz.getAttributes().isEmpty() && clazz.getRoles().isEmpty())
      {
         return;
      }

      fragmentMap
         .add(Parser.IMPORT + ":java.beans.PropertyChangeSupport", "import java.beans.PropertyChangeSupport;", 1);
      fragmentMap
         .add(Parser.IMPORT + ":java.beans.PropertyChangeListener", "import java.beans.PropertyChangeListener;", 1);

      final STGroup group = this.getSTGroup("templates/propertyChangeSupport.stg");

      // TODO template
      final String listeners = "   protected PropertyChangeSupport listeners = null;";
      fragmentMap.add(Parser.ATTRIBUTE + ":listeners", listeners, 2, clazz.getModified());

      final ST firePropertyChange = group.getInstanceOf("firePropertyChange");
      fragmentMap.add(Parser.METHOD + ":firePropertyChange(String,Object,Object)", firePropertyChange.render(), 2,
                      clazz.getModified());

      final ST addPCL1 = group.getInstanceOf("addPropertyChangeListener1");
      fragmentMap.add(Parser.METHOD + ":addPropertyChangeListener(PropertyChangeListener)", addPCL1.render(), 2,
                      clazz.getModified());

      final ST addPCL2 = group.getInstanceOf("addPropertyChangeListener2");
      fragmentMap.add(Parser.METHOD + ":addPropertyChangeListener(String,PropertyChangeListener)", addPCL2.render(), 2,
                      clazz.getModified());

      final ST removePCL1 = group.getInstanceOf("removePropertyChangeListener1");
      fragmentMap.add(Parser.METHOD + ":removePropertyChangeListener(PropertyChangeListener)", removePCL1.render(), 2,
                      clazz.getModified());

      final ST removePCL2 = group.getInstanceOf("removePropertyChangeListener2");
      fragmentMap
         .add(Parser.METHOD + ":removePropertyChangeListener(String,PropertyChangeListener)", removePCL2.render(), 2,
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
         final STGroup group = this.getSTGroup("templates/toString.stg");
         final ST toString = group.getInstanceOf("toString");
         toString.add("names", nameList);
         fragment = toString.render();
      }

      fragmentMap.add(Parser.METHOD + ":toString()", fragment, 2, modified);
   }

   private void generateRemoveYou(Clazz clazz, FileFragmentMap fragmentMap)
   {
      ArrayList<String> toOneList = new ArrayList<>();
      ArrayList<String> toManyList = new ArrayList<>();
      ArrayList<String> toOneAggregationList = new ArrayList<>();
      ArrayList<String> toManyAggregationList = new ArrayList<>();
      ArrayList<String> toManyTypes = new ArrayList<>();
      ArrayList<Boolean> javaFXStyles = new ArrayList<>();
      boolean modified = clazz.getModified();

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

      STGroup group = this.getSTGroup("templates/removeYou.stg");
      ST st = group.getInstanceOf("removeYou");
      st.add("toOneNames", toOneList.toArray(new String[0]));
      st.add("toManyNames", toManyList.toArray(new String[0]));
      st.add("toOneAggregations", toOneAggregationList.toArray(new String[0]));
      st.add("toManyAggregations", toManyAggregationList.toArray(new String[0]));
      st.add("toManyTypes", toManyTypes.toArray(new String[0]));
      st.add("javaFXStyles", javaFXStyles.toArray(new Boolean[0]));
      if (clazz.getSuperClass() != null)
      {
         st.add("superClass", "yes");
      }

      fragmentMap.add(Parser.METHOD + ":removeYou()", st.render(), 2, modified);
   }
}
