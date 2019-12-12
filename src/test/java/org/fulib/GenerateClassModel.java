package org.fulib;

import org.fulib.builder.ClassModelManager;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.junit.jupiter.api.Test;

import static org.fulib.builder.Type.*;

class GenerateClassModel
{
   @Test
   void generateModel()
   {
      final ClassModelManager mb = new ClassModelManager();
      mb.setSourceFolder("src/main/java");
      mb.setPackageName("org.fulib.classmodel");

      // Classes
      final Clazz ClassModel = mb.haveClass("ClassModel", c -> {
         c.attribute("packageName", STRING);
         c.attribute("mainJavaDir", STRING);
         c.attribute("defaultRoleType", STRING);
         c.attribute("defaultPropertyStyle", STRING, "\"POJO\"");
      });

      final Clazz Clazz = mb.haveClass("Clazz", c -> {
         c.attribute("name", STRING);
         c.attribute("propertyStyle", STRING);
         c.attribute("modified", BOOLEAN);
         c.attribute("importList", "java.util.LinkedHashSet<String>", "new java.util.LinkedHashSet<>()");
      });

      final Clazz Attribute = mb.haveClass("Attribute", c -> {
         c.attribute("name", STRING);
         c.attribute("type", STRING);
         c.attribute("initialization", STRING);
         c.attribute("propertyStyle", STRING);
         c.attribute("modified", BOOLEAN);
      });

      final Clazz AssocRole = mb.haveClass("AssocRole", c -> {
         c.attribute("name", STRING);
         c.attribute("cardinality", INT);
         c.attribute("roleType", STRING);
         c.attribute("aggregation", BOOLEAN);
         c.attribute("propertyStyle", STRING);
         c.attribute("modified", BOOLEAN);
      });

      final Clazz FMethod = mb.haveClass("FMethod", c -> {
         c.attribute("declaration", STRING);
         c.attribute("methodBody", STRING);
         c.attribute("modified", BOOLEAN);
         c.attribute("annotations", STRING);
      });

      mb.associate(ClassModel, "classes", MANY, Clazz, "model", ONE);

      mb.associate(Clazz, "attributes", MANY, Attribute, "clazz", ONE);

      mb.associate(Clazz, "roles", MANY, AssocRole, "clazz", ONE);

      mb.associate(Clazz, "methods", MANY, FMethod, "clazz", ONE);

      mb.associate(Clazz, "superClass", ONE, Clazz, "subClasses", MANY);

      mb.associate(AssocRole, "other", ONE, AssocRole, "other", ONE);

      mb.haveClass("FileFragmentMap", c -> {
         c.attribute("fileName", STRING);
      });

      mb.haveClass("CodeFragment", c -> {
         c.attribute("key", STRING);
         c.attribute("text", STRING);
      });

      // start_code_fragment: Fulib.createGenerator
      ClassModel model = mb.getClassModel();
      Fulib.generator().generate(model);
      // end_code_fragment:
   }
}
