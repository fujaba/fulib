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
         c
            .attribute("defaultCollectionType", "CollectionType")
            .setDescription("the default collection type for to-n roles")
            .setSince("1.2");
         c
            .attribute("defaultPropertyStyle", STRING, "\"POJO\"")
            .setDescription("the default property style for attributes and roles.\n"
                            + "Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.");
      });

      final Clazz Clazz = mb.haveClass("Clazz", c -> {
         c.attribute("name", STRING);
         c
            .attribute("propertyStyle", STRING)
            .setDescription("the default property style for attributes and roles.\n"
                            + "Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.");
         c
            .attribute("modified", BOOLEAN)
            .setDescription("a boolean indicating whether this attribute was modified. For internal use only.");
         c
            .attribute("imports", STRING)
            .setCollectionType(CollectionType.LinkedHashSet)
            .setDescription("the set of imported members.\n"
                            + "Elements can have one of the formats {@code org.example.Foo}, {@code static org.example.Foo.bar},\n"
                            + "{@code import org.example.Foo;} or {@code import static org.example.Foo.bar;}")
            .setSince("1.2");
      });

      final Clazz Attribute = mb.haveClass("Attribute", c -> {
         c.attribute("name", STRING);
         c.attribute("type", STRING);
         c.attribute("collectionType", "CollectionType").setDescription("the collection type").setSince("1.2");
         c.attribute("initialization", STRING);
         c
            .attribute("propertyStyle", STRING)
            .setDescription("the property style.\n"
                            + "Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.");
         c
            .attribute("modified", BOOLEAN)
            .setDescription("a boolean indicating whether this attribute was modified. For internal use only.");
         c
            .attribute("description", STRING)
            .setDescription("the description of this attribute, used for generating JavaDocs")
            .setSince("1.3");
         c
            .attribute("since", STRING)
            .setDescription("the version when this attribute was introduced, used for generating JavaDocs")
            .setSince("1.3");
      });

      final Clazz AssocRole = mb.haveClass("AssocRole", c -> {
         c.attribute("name", STRING);
         c.attribute("cardinality", INT);
         c.attribute("collectionType", "CollectionType").setDescription("the collection type").setSince("1.2");
         c
            .attribute("aggregation", BOOLEAN)
            .setDescription("a boolean indicating whether this role is an aggregation,\n"
                            + "i.e. whether the target objects are {@code removeYou}'d completely when using {@code without*} methods or\n"
                            + "{@code removeYou} on the source object");
         c
            .attribute("propertyStyle", STRING)
            .setDescription("the property style.\n"
                            + "Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.");
         c
            .attribute("description", STRING)
            .setDescription("the description of this role, used for generating JavaDocs")
            .setSince("1.3");
         c
            .attribute("since", STRING)
            .setDescription("the version when this role was introduced, used for generating JavaDocs")
            .setSince("1.3");
         c
            .attribute("modified", BOOLEAN)
            .setDescription("a boolean indicating whether this role was modified. For internal use only.");
      });

      final Clazz FMethod = mb.haveClass("FMethod", c -> {
         c.attribute("methodBody", STRING);
         c
            .attribute("modified", BOOLEAN)
            .setDescription("a boolean indicating whether this method was modified. For internal use only.");
         c
            .attribute("modifiers", STRING, "\"public\"")
            .setDescription("the modifiers. Defaults to \"public\"")
            .setSince("1.2");
         c.attribute("annotations", STRING);
      });

      mb
         .associate(ClassModel, "classes", MANY, Clazz, "model", ONE)
         .setDescription("the classes contained in this model")
         .setSince("1.2");

      mb.associate(Clazz, "attributes", MANY, Attribute, "clazz", ONE).setDescription("the attributes").setSince("1.2");

      mb.associate(Clazz, "roles", MANY, AssocRole, "clazz", ONE).setDescription("the roles").setSince("1.2");

      mb.associate(Clazz, "methods", MANY, FMethod, "clazz", ONE).setDescription("the methods").setSince("1.2");

      mb
         .associate(Clazz, "subClasses", MANY, Clazz, "superClass", ONE)
         .setDescription("the subclasses")
         .setSince("1.2");

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
