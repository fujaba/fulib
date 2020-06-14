package org.fulib.classmodel;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;

import static org.fulib.builder.Type.*;

public class GenModel implements ClassModelDecorator
{
   @Override
   public void decorate(ClassModelManager mb)
   {
      // Classes
      final Clazz ClassModel = mb.haveClass("ClassModel", c -> {
         c.attribute("packageName", STRING);
         c.attribute("mainJavaDir", STRING);
         c.attribute("defaultCollectionType", "CollectionType");
         c.attribute("defaultPropertyStyle", STRING, "\"POJO\"");
      });

      final Clazz Clazz = mb.haveClass("Clazz", c -> {
         c.attribute("name", STRING);
         c.attribute("propertyStyle", STRING);
         c.attribute("modified", BOOLEAN);
         c.attribute("imports", STRING).setCollectionType(CollectionType.LinkedHashSet);
      });

      final Clazz Attribute = mb.haveClass("Attribute", c -> {
         c.attribute("name", STRING);
         c.attribute("type", STRING);
         c.attribute("collectionType", "CollectionType");
         c.attribute("initialization", STRING);
         c.attribute("propertyStyle", STRING);
         c.attribute("modified", BOOLEAN);
      });

      final Clazz AssocRole = mb.haveClass("AssocRole", c -> {
         c.attribute("name", STRING);
         c.attribute("cardinality", INT);
         c.attribute("collectionType", "CollectionType");
         c.attribute("aggregation", BOOLEAN);
         c.attribute("propertyStyle", STRING);
         c.attribute("modified", BOOLEAN);
      });

      final Clazz FMethod = mb.haveClass("FMethod", c -> {
         c.attribute("methodBody", STRING);
         c.attribute("modified", BOOLEAN);
         c.attribute("modifiers", STRING, "\"public\"");
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

      final Clazz Fragment = mb.haveClass("Fragment", c -> {
         c.attribute("key", STRING);
      });

      mb.haveClass("CodeFragment", c -> {
         c.extend(Fragment);
         c.attribute("text", STRING);
      });

      final Clazz CompoundFragment = mb.haveClass("CompoundFragment", c -> {
         c.extend(Fragment);
      });

      mb.associate(CompoundFragment, "children", MANY, Fragment, "parent", ONE);
   }
}
