package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.ClassModel;
import org.junit.jupiter.api.Test;

class GenerateClassModel
{
   @Test
   void testGenerateModel()
   {
      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.classmodel", "src/main/java");

      ClassBuilder classModel = mb.buildClass("ClassModel")
              .buildAttribute("packageName", ClassModelBuilder.STRING)
              .buildAttribute("mainJavaDir", ClassModelBuilder.STRING)
              .buildAttribute("testJavaDir", ClassModelBuilder.STRING)
              .buildAttribute("defaultRoleType", ClassModelBuilder.STRING)
              .buildAttribute("defaultPropertyStyle", ClassModelBuilder.STRING, "\"POJO\"");

      ClassBuilder fuClass = mb.buildClass("Clazz")
              .buildAttribute("name", ClassModelBuilder.STRING)
              .buildAttribute("propertyStyle", ClassModelBuilder.STRING)
              .buildAttribute("modified", ClassModelBuilder.BOOLEAN, "false");

      ClassBuilder attribute = mb.buildClass("Attribute")
              .buildAttribute("name", ClassModelBuilder.STRING)
              .buildAttribute("type", ClassModelBuilder.STRING)
              .buildAttribute("initialization", ClassModelBuilder.STRING)
              .buildAttribute("propertyStyle", ClassModelBuilder.STRING)
              .buildAttribute("modified", ClassModelBuilder.BOOLEAN, "false");

      ClassBuilder assocRole = mb.buildClass("AssocRole")
              .buildAttribute("name", ClassModelBuilder.STRING)
              .buildAttribute("cardinality", ClassModelBuilder.INT)
              .buildAttribute("roleType", ClassModelBuilder.STRING)
              .buildAttribute("aggregation", ClassModelBuilder.BOOLEAN, "false")
              .buildAttribute("propertyStyle", ClassModelBuilder.STRING)
              .buildAttribute("modified", ClassModelBuilder.BOOLEAN, "false");

      classModel.buildAssociation(fuClass, "classes", ClassModelBuilder.MANY, "model", ClassModelBuilder.ONE);

      fuClass.buildAssociation(attribute, "attributes", ClassModelBuilder.MANY, "clazz", ClassModelBuilder.ONE);

      fuClass.buildAssociation(assocRole, "roles", ClassModelBuilder.MANY, "clazz", ClassModelBuilder.ONE);

      fuClass.buildAssociation(fuClass, "superClass", ClassModelBuilder.ONE, "subClasses", ClassModelBuilder.MANY);

      assocRole.buildAssociation(assocRole, "other", ClassModelBuilder.ONE, "other", ClassModelBuilder.ONE);

      mb.buildClass("FileFragmentMap")
              .buildAttribute("fileName", ClassModelBuilder.STRING);

      mb.buildClass("CodeFragment")
              .buildAttribute("key", ClassModelBuilder.STRING)
              .buildAttribute("text", ClassModelBuilder.STRING);


      // start_code_fragment: Fulib.createGenerator
      ClassModel model = mb.getClassModel();
      Fulib.generator().generate(model);
      // end_code_fragment:


   }
}
