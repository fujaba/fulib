package org.fulib;

import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.FileFragmentMap;
import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.STMessage;

import java.util.logging.Logger;

public class Generator
{
   public static void generate(ClassModel model)
   {
      new Generator()
            .doGenerate(model);
   }

   public void doGenerate(ClassModel model)
   {
      // loop through all classes
      for (Clazz clazz : model.getClasses())
      {
         doGenerate(clazz);
      }
   }


   private void doGenerate(Clazz clazz)
   {
      generateModelClass(clazz);
   }

   private void generateModelClass(Clazz clazz)
   {
      String classFileName = clazz.getModel().getPackageSrcFolder() + "/" + clazz.getName() + ".java";
      FileFragmentMap fragmentMap = Parser.parse(classFileName);

      // doGenerate code for class
      String result = String.format("package %s;", clazz.getModel().getPackageName());
      fragmentMap.add(Parser.PACKAGE, result, 2);

      result = String.format("public class %s\n{", clazz.getName());
      fragmentMap.add(Parser.CLASS, result, 2);

      // doGenerate code for attributes
      generateAttributes(clazz, fragmentMap);

      // doGenerate code for association

      fragmentMap.add(Parser.CLASS_END, "}", 1);

      fragmentMap.writeFile();
   }

   private void generateAttributes(Clazz clazz, FileFragmentMap fragmentMap)
   {
      for (Attribute attr : clazz.getAttributes())
      {
         generateAttributeDeclaration(fragmentMap, attr);

         generateGetMethod(fragmentMap, attr);
      }
   }

   private void generateGetMethod(FileFragmentMap fragmentMap, Attribute attr)
   {
      STGroup group = new STGroupDir("templates");
      group.registerRenderer(String.class, new StringRenderer());
      ST attrTemplate = group.getInstanceOf("attrGet");
      attrTemplate.add("type", attr.getType());
      attrTemplate.add("name", attr.getName());
      String result = attrTemplate.render();

      fragmentMap.add(Parser.METHOD + ":" + attr.getName() + "()", result, 2);

   }

   private void handleMsg(Object msg)
   {

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
}
