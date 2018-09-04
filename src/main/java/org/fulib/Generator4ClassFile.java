package org.fulib;

import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.stringtemplate.v4.*;

public class Generator4ClassFile
{
   public void doGenerate(Clazz clazz)
   {
      String classFileName = clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".java";
      FileFragmentMap fragmentMap = Parser.parse(classFileName);

      // doGenerate code for class
      generatePackageDecl(clazz, fragmentMap);

      generateClassDecl(clazz, fragmentMap);

      generateAttributes(clazz, fragmentMap);

      // doGenerate code for association

      generatePropertyChangeSupport(clazz, fragmentMap);

      fragmentMap.add(Parser.CLASS_END, "}", 1);

      fragmentMap.writeFile();
   }


   private void generatePackageDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      String result = String.format("package %s;", clazz.getModel().getPackageName());
      fragmentMap.add(Parser.PACKAGE, result, 2);
   }


   private void generateClassDecl(Clazz clazz, FileFragmentMap fragmentMap)
   {
      String result = String.format("public class %s\n{", clazz.getName());
      fragmentMap.add(Parser.CLASS, result, 2);
   }


   private void generatePropertyChangeSupport(Clazz clazz, FileFragmentMap fragmentMap)
   {
      fragmentMap.add(Parser.IMPORT + ":java.beans.PropertyChangeSupport", "import java.beans.PropertyChangeSupport;", 1);
      fragmentMap.add(Parser.IMPORT + ":java.beans.PropertyChangeListener", "import java.beans.PropertyChangeListener;", 1);

      STGroup group = new STGroupDir("templates");
      group.registerRenderer(String.class, new StringRenderer());

      String result = "   protected PropertyChangeSupport listeners = null;";
      fragmentMap.add(Parser.ATTRIBUTE + ":listeners", result, 2);

      ST st = group.getInstanceOf("firePropertyChange");
      result = st.render();
      fragmentMap.add(Parser.METHOD + ":firePropertyChange(String,Object,Object)", result, 2);

      st = group.getInstanceOf("addPropertyChangeListener1");
      result = st.render();
      fragmentMap.add(Parser.METHOD + ":addPropertyChangeListener(PropertyChangeListener)", result, 2);

      st = group.getInstanceOf("addPropertyChangeListener2");
      result = st.render();
      fragmentMap.add(Parser.METHOD + ":addPropertyChangeListener(String,PropertyChangeListener)", result, 2);

      st = group.getInstanceOf("removePropertyChangeListener1");
      result = st.render();
      fragmentMap.add(Parser.METHOD + ":removePropertyChangeListener(PropertyChangeListener)", result, 2);

      st = group.getInstanceOf("removePropertyChangeListener2");
      result = st.render();
      fragmentMap.add(Parser.METHOD + ":removePropertyChangeListener(String,PropertyChangeListener)", result, 2);
   }


   private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap)
   {
      for (Attribute attr : clazz.getAttributes())
      {
         generateAttributeDeclaration(fragmentMap, attr);

         generateGetMethod(fragmentMap, attr);

         generateSetMethod(fragmentMap, attr);
      }
   }


   private void generateAttributeDeclaration(FileFragmentMap fragmentMap, Attribute attr)
   {
      STGroup stg = new STGroupString("" +
            "decl(type, name, value) ::= <<   private <type> <name><init(value)>;>>\n" +
            "init(v) ::= \"<if(v)> = <v><endif>\"\n");
      ST attrTemplate = stg.getInstanceOf("decl");
      attrTemplate.add("type", attr.getType());
      attrTemplate.add("name", attr.getName());
      attrTemplate.add("value", attr.getInitialization());
      String result = attrTemplate.render();

      fragmentMap.add(Parser.ATTRIBUTE + ":" + attr.getName(), result, 2);
   }


   private void generateGetMethod(FileFragmentMap fragmentMap, Attribute attr)
   {
      STGroup group = new STGroupDir("templates");
      group.registerRenderer(String.class, new StringRenderer());
      ST attrTemplate = group.getInstanceOf("attrGet");
      attrTemplate.add("type", attr.getType());
      attrTemplate.add("name", attr.getName());
      String result = attrTemplate.render();

      fragmentMap.add(Parser.METHOD + ":get" + StrUtil.cap(attr.getName()) + "()", result, 2);
   }


   private void generateSetMethod(FileFragmentMap fragmentMap, Attribute attr)
   {
      STGroup group = new STGroupFile("templates/attrSet.stg");
      group.registerRenderer(String.class, new StringRenderer());
      ST attrTemplate = group.getInstanceOf("attrSet");
      attrTemplate.add("class", attr.getClazz().getName());
      attrTemplate.add("type", attr.getType());
      attrTemplate.add("name", attr.getName());
      attrTemplate.add("useEquals", attr.getType().equals("String"));
      String result = attrTemplate.render();

      fragmentMap.add(Parser.METHOD + ":set" + StrUtil.cap(attr.getName()) + "(" + attr.getType() +")", result, 2);

   }

}
