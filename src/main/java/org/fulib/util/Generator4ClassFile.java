package org.fulib.util;

import org.fulib.Generator;
import org.fulib.Parser;
import org.fulib.StrUtil;
import org.fulib.builder.Type;
import org.fulib.classmodel.*;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

public class Generator4ClassFile
{

   private String customTemplatesFile;

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
         if (firstFragment.getText().matches("\\s*"))
         {

            String newText = "";
            for (int pos = firstFragment.getText().length() - 1; pos >= 0; pos--)
            {
               if (firstFragment.getText().charAt(pos) == '\n')
               {
                  noOfBlankLines++;
                  if (noOfBlankLines == 2)
                  {
                     newText = firstFragment.getText().substring(pos);
                     firstFragment.setText(newText);
                     break;
                  }
                  if (noOfBlankLines > 2)
                  {
                     newText = firstFragment.getText().substring(pos + 1);
                     firstFragment.setText(newText);
                     break;
                  }
               }
            }
         }
         else
         {
            noOfBlankLines = 0;
         }
      }
   }

   private void generatePackageDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      String result = String.format("package %s;", clazz.getModel().getPackageName());
      fragmentMap.add(Parser.PACKAGE, result, 2);
   }

   private void generateImports(Clazz clazz, FileFragmentMap fragmentMap)
   {
      for (String imp : clazz.getImportList())
      {
         String[] split = imp.split(" ");
         String key = split[split.length - 1];
         key = key.substring(0, key.length() - 1);
         fragmentMap.add(Parser.IMPORT + ":" + key, imp, 1);
      }
   }

   private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      STGroup group = this.createSTGroup("templates/classDecl.stg");
      ST st = group.getInstanceOf("classDecl");
      st.add("name", clazz.getName());
      st.add("superClass", clazz.getSuperClass() != null ? clazz.getSuperClass().getName() : null);
      String result = st.render();
      fragmentMap.add(Parser.CLASS, result, 2);
   }

   private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap)
   {
      STGroup group;
      ST attrTemplate;
      String result;

      for (Attribute attr : clazz.getAttributes())
      {
         if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
         {
            group = this.createSTGroup("templates/JavaFXattributes.stg");
            fragmentMap.add(Parser.IMPORT + ":javafx.beans.property.*", "import javafx.beans.property.*;", 1);
         }
         else
         {
            group = this.createSTGroup("templates/attributes.stg");
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
         attrTemplate = group.getInstanceOf("propertyDecl");
         attrTemplate.add("name", attr.getName());
         result = attrTemplate.render();
         fragmentMap.add(Parser.ATTRIBUTE + ":PROPERTY_" + attr.getName(), result, 2, attr.getModified());

         attrTemplate = group.getInstanceOf("attrDecl");
         attrTemplate.add("type", attrType);
         attrTemplate.add("name", attr.getName());
         attrTemplate.add("value", attr.getInitialization());
         result = attrTemplate.render();

         fragmentMap.add(Parser.ATTRIBUTE + ":" + attr.getName(), result, 2, attr.getModified());

         if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
         {
            attrTemplate = group.getInstanceOf("initMethod");
            attrTemplate.add("name", attr.getName());
            attrTemplate.add("type", attrType);
            result = attrTemplate.render();

            fragmentMap
               .add(Parser.METHOD + ":_init" + StrUtil.cap(attr.getName()) + "()", result, 2, attr.getModified());
         }
         else
         {
            fragmentMap.add(Parser.METHOD + ":_init" + StrUtil.cap(attr.getName()) + "()", "", 2, true);
         }

         attrTemplate = group.getInstanceOf("attrGet");
         attrTemplate.add("type", attrType);
         attrTemplate.add("name", attr.getName());
         result = attrTemplate.render();

         fragmentMap.add(Parser.METHOD + ":get" + StrUtil.cap(attr.getName()) + "()", result, 2, attr.getModified());

         if (attr.getType().endsWith(Type.__LIST))
         {
            // attrWith(class, name, listType, baseType)
            attrTemplate = group.getInstanceOf("attrWith");
            attrTemplate.add("class", attr.getClazz().getName());
            attrTemplate.add("listType", attrType);
            attrTemplate.add("baseType", boxType);
            attrTemplate.add("name", attr.getName());
            result = attrTemplate.render();

            fragmentMap.add(Parser.METHOD + ":with" + StrUtil.cap(attr.getName()) + "(Object...)", result, 3,
                            attr.getModified());
            // attrWith(class, name, listType, baseType)
            attrTemplate = group.getInstanceOf("attrWithout");
            attrTemplate.add("class", attr.getClazz().getName());
            attrTemplate.add("listType", attrType);
            attrTemplate.add("baseType", boxType);
            attrTemplate.add("name", attr.getName());
            result = attrTemplate.render();

            fragmentMap.add(Parser.METHOD + ":without" + StrUtil.cap(attr.getName()) + "(Object...)", result, 3,
                            attr.getModified());
         }
         else
         { // ususal attribute
            attrTemplate = group.getInstanceOf("attrSet");
            attrTemplate.add("class", attr.getClazz().getName());
            attrTemplate.add("type", attr.getType());
            attrTemplate.add("name", attr.getName());
            result = attrTemplate.render();

            fragmentMap
               .add(Parser.METHOD + ":set" + StrUtil.cap(attr.getName()) + "(" + attr.getType() + ")", result, 3,
                    attr.getModified());
         }

         if (Type.JAVA_FX.equals(attr.getPropertyStyle()))
         {
            attrTemplate = group.getInstanceOf("propertyGet");
            attrTemplate.add("name", attr.getName());
            attrTemplate.add("type", attrType);
            result = attrTemplate.render();

            fragmentMap.add(Parser.METHOD + ":" + attr.getName() + "Property()", result, 3, attr.getModified());
         }
         else
         {
            fragmentMap.add(Parser.METHOD + ":" + attr.getName() + "Property()", "", 3, true);
         }
      }
   }

   private void generateAssociations(Clazz clazz, FileFragmentMap fragmentMap)
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

         if (Type.JAVA_FX.equals(role.getPropertyStyle()))
         {
            group = this.createSTGroup("templates/JavaFXassociations.stg");
            fragmentMap.add(Parser.IMPORT + ":javafx.beans.property.*", "import javafx.beans.property.*;", 1);
         }
         else
         {
            group = this.createSTGroup("templates/associations.stg");
         }

         String roleType = role.getOther().getClazz().getName();

         // provide empty_set in this class
         if (role.getCardinality() != Type.ONE && !Type.JAVA_FX.equals(role.getPropertyStyle()))
         {
            // add empty set decl
            roleType = String.format(role.getRoleType(), role.getOther().getClazz().getName());

            st = group.getInstanceOf("emptySetDecl");
            st.add("roleName", role.getName());
            st.add("otherClassName", role.getOther().getClazz().getName());
            st.add("roleType", roleType);
            result = st.render();

            fragmentMap.add(Parser.ATTRIBUTE + ":EMPTY_" + role.getName(), result, 3, role.getModified());
         }

         st = group.getInstanceOf("propertyDecl");
         st.add("roleName", role.getName());
         result = st.render();
         fragmentMap.add(Parser.ATTRIBUTE + ":PROPERTY_" + role.getName(), result, 2, role.getModified());

         st = group.getInstanceOf("roleAttrDecl");
         st.add("roleName", role.getName());
         st.add("roleType", roleType);
         st.add("toMany", role.getCardinality() != Type.ONE);
         st.add("otherClassName", role.getOther().getClazz().getName());
         result = st.render();

         fragmentMap.add(Parser.ATTRIBUTE + ":" + role.getName(), result, 2, role.getModified());

         if (Type.JAVA_FX.equals(role.getPropertyStyle()))
         {
            // remove empty set decl
            result = "";
            fragmentMap.add(Parser.ATTRIBUTE + ":EMPTY_" + role.getName(), result, 3, true);

            // add _init method
            st = group.getInstanceOf("initMethod");
            st.add("roleName", role.getName());
            st.add("toMany", role.getCardinality() != Type.ONE);
            st.add("myClassName", clazz.getName());
            st.add("otherClassName", role.getOther().getClazz().getName());
            st.add("otherRoleName", role.getOther().getName());
            st.add("otherToMany", role.getOther().getCardinality() != Type.ONE);
            result = st.render();

            fragmentMap
               .add(Parser.METHOD + ":_init" + StrUtil.cap(role.getName()) + "()", result, 2, role.getModified());
         }
         else
         {
            // remove _init method
            fragmentMap.add(Parser.METHOD + ":_init" + StrUtil.cap(role.getName()) + "()", "", 2, true);
         }

         st = group.getInstanceOf("getMethod");

         st.add("roleName", role.getName());
         st.add("toMany", role.getCardinality() != Type.ONE);
         st.add("otherClassName", role.getOther().getClazz().getName());
         st.add("roleType", roleType);
         result = st.render();

         fragmentMap.add(Parser.METHOD + ":get" + StrUtil.cap(role.getName()) + "()", result, 2, role.getModified());

         st = group.getInstanceOf("setMethod");
         st.add("roleName", role.getName());
         st.add("toMany", role.getCardinality() != Type.ONE);
         st.add("myClassName", clazz.getName());
         st.add("otherClassName", role.getOther().getClazz().getName());
         st.add("otherRoleName", role.getOther().getName());
         st.add("otherToMany", role.getOther().getCardinality() != Type.ONE);
         st.add("roleType", roleType);
         result = st.render();

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
               String oldSignature = "with" + StrUtil.cap(role.getName()) + "(" + paramType + ")";
               fragmentMap.add(Parser.METHOD + ":" + oldSignature, "", 3, true);
            }
            paramType = role.getOther().getClazz().getName();
         }
         else
         {
            // remove withXY(OtherClass)
            String oldSignature =
               "with" + StrUtil.cap(role.getName()) + "(" + role.getOther().getClazz().getName() + ")";
            fragmentMap.add(Parser.METHOD + ":" + oldSignature, "", 3, true);
         }

         signature += StrUtil.cap(role.getName()) + "(" + paramType + ")";

         fragmentMap.add(Parser.METHOD + ":" + signature, result, 3, role.getModified());

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

            paramType = "Object...";
            if (Type.JAVA_FX.equals(role.getPropertyStyle()))
            {
               paramType = role.getOther().getClazz().getName();
            }
            fragmentMap.add(Parser.METHOD + ":without" + StrUtil.cap(role.getName()) + "(" + paramType + ")", result, 3,
                            role.getModified());
         }

         if (Type.JAVA_FX.equals(role.getPropertyStyle()) && role.getCardinality() == Type.ONE)
         {
            st = group.getInstanceOf("propertyMethod");
            st.add("roleName", role.getName());
            st.add("otherClassName", role.getOther().getClazz().getName());
            result = st.render();

            fragmentMap.add(Parser.METHOD + ":" + role.getName() + "Property()", result, 3, role.getModified());
         }
         else
         {
            fragmentMap.add(Parser.METHOD + ":" + role.getName() + "Property()", "", 3, true);
         }
      }
   }

   private void generateMethods(Clazz clazz, FileFragmentMap fragmentMap)
   {
      for (FMethod method : clazz.getMethods())
      {
         String signature = method.readSignature();
         String methodBody = method.getMethodBody();
         if (methodBody == null)
         {
            methodBody = "      // hello world\n";
         }
         String newText = "   " + method.getDeclaration() + " { \n" + methodBody + "   }";

         fragmentMap.add(signature, newText, 2, method.getModified());
      }
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

      STGroup group = this.createSTGroup("templates/propertyChangeSupport.stg");

      String result = "   protected PropertyChangeSupport listeners = null;";
      fragmentMap.add(Parser.ATTRIBUTE + ":listeners", result, 2, clazz.getModified());

      ST st = group.getInstanceOf("firePropertyChange");
      result = st.render();
      fragmentMap.add(Parser.METHOD + ":firePropertyChange(String,Object,Object)", result, 2, clazz.getModified());

      st = group.getInstanceOf("addPropertyChangeListener1");
      result = st.render();
      fragmentMap
         .add(Parser.METHOD + ":addPropertyChangeListener(PropertyChangeListener)", result, 2, clazz.getModified());

      st = group.getInstanceOf("addPropertyChangeListener2");
      result = st.render();
      fragmentMap.add(Parser.METHOD + ":addPropertyChangeListener(String,PropertyChangeListener)", result, 2,
                      clazz.getModified());

      st = group.getInstanceOf("removePropertyChangeListener1");
      result = st.render();
      fragmentMap
         .add(Parser.METHOD + ":removePropertyChangeListener(PropertyChangeListener)", result, 2, clazz.getModified());

      st = group.getInstanceOf("removePropertyChangeListener2");
      result = st.render();
      fragmentMap.add(Parser.METHOD + ":removePropertyChangeListener(String,PropertyChangeListener)", result, 2,
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

         if (attr.getModified())
         {
            modified = true;
         }
      }

      String result = "";
      if (!nameList.isEmpty())
      {
         STGroup group = this.createSTGroup("templates/toString.stg");
         ST st = group.getInstanceOf("toString");
         st.add("names", nameList.toArray(new String[0]));
         result = st.render();
      }

      fragmentMap.add(Parser.METHOD + ":toString()", result, 2, modified);
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

      STGroup group = this.createSTGroup("templates/removeYou.stg");
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

   public String getCustomTemplatesFile()
   {
      return this.customTemplatesFile;
   }

   public Generator4ClassFile setCustomTemplatesFile(String customTemplateFile)
   {
      this.customTemplatesFile = customTemplateFile;
      return this;
   }

   private STGroup createSTGroup(String origFileName)
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
