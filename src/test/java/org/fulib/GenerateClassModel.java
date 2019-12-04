package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;

import org.fulib.builder.Type;
import org.fulib.classmodel.ClassModel;
import org.junit.jupiter.api.Test;

class GenerateClassModel
{
   @Test
   void testGenerateModel()
   {
      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.classmodel", "src/main/java");

      ClassBuilder classModel = mb.buildClass("ClassModel")
              .buildAttribute("packageName", Type.STRING)
              .buildAttribute("mainJavaDir", Type.STRING)
              .buildAttribute("defaultRoleType", Type.STRING)
              .buildAttribute("defaultPropertyStyle", Type.STRING, "\"POJO\"");

      ClassBuilder fuClass = mb.buildClass("Clazz")
              .buildAttribute("name", Type.STRING)
              .buildAttribute("propertyStyle", Type.STRING)
              .buildAttribute("modified", Type.BOOLEAN, "false")
              .buildAttribute("importList", "java.util.LinkedHashSet<String>", "new java.util.LinkedHashSet<>()");

      ClassBuilder attribute = mb.buildClass("Attribute")
              .buildAttribute("name", Type.STRING)
              .buildAttribute("type", Type.STRING)
              .buildAttribute("initialization", Type.STRING)
              .buildAttribute("propertyStyle", Type.STRING)
              .buildAttribute("modified", Type.BOOLEAN, "false");

      ClassBuilder assocRole = mb.buildClass("AssocRole")
              .buildAttribute("name", Type.STRING)
              .buildAttribute("cardinality", Type.INT)
              .buildAttribute("roleType", Type.STRING)
              .buildAttribute("aggregation", Type.BOOLEAN, "false")
              .buildAttribute("propertyStyle", Type.STRING)
              .buildAttribute("modified", Type.BOOLEAN, "false");

      ClassBuilder fmethod = mb.buildClass("FMethod")
            .buildAttribute("declaration", Type.STRING)
            .buildAttribute("methodBody", Type.STRING)
            .buildAttribute("modified", Type.BOOLEAN, "false")
            .buildAttribute("annotations", Type.STRING);

      classModel.buildAssociation(fuClass, "classes", Type.MANY, "model", Type.ONE);

      fuClass.buildAssociation(attribute, "attributes", Type.MANY, "clazz", Type.ONE);

      fuClass.buildAssociation(assocRole, "roles", Type.MANY, "clazz", Type.ONE);

      fuClass.buildAssociation(fmethod, "methods", Type.MANY, "clazz", Type.ONE);

      fuClass.buildAssociation(fuClass, "superClass", Type.ONE, "subClasses", Type.MANY);

      assocRole.buildAssociation(assocRole, "other", Type.ONE, "other", Type.ONE);

      mb.buildClass("FileFragmentMap")
              .buildAttribute("fileName", Type.STRING);

      mb.buildClass("CodeFragment")
              .buildAttribute("key", Type.STRING)
              .buildAttribute("text", Type.STRING);


      // start_code_fragment: Fulib.createGenerator
      ClassModel model = mb.getClassModel();
      Fulib.generator().generate(model);
      // end_code_fragment:
   }
}
