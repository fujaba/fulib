package org.fulib;

import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import org.junit.Test;
import org.sdmlib.models.classes.ClassModel;

public class GenerateClassModel
{
   @Test
   public void testGenerateModel()
   {
      ClassModel model = new ClassModel("org.fulib.classmodel");

      Clazz classModel = model.createClazz("ClassModel")
            .withAttribute("packageName", DataType.STRING)
            .withAttribute("mainJavaDir", DataType.STRING)
            .withAttribute("testJavaDir", DataType.STRING);

      Clazz fuClass = model.createClazz("Clazz")
            .withAttribute("name", DataType.STRING);

      Clazz attribute = model.createClazz("Attribute")
            .withAttribute("name", DataType.STRING)
            .withAttribute("type", DataType.STRING)
            .withAttribute("initialization", DataType.STRING);

      Clazz assocRole = model.createClazz("AssocRole")
            .withAttribute("name", DataType.STRING)
            .withAttribute("cardinality", DataType.INT);

      classModel.withBidirectional(fuClass, "classes", Cardinality.MANY, "model", Cardinality.ONE);

      fuClass.withBidirectional(attribute, "attributes", Cardinality.MANY, "clazz", Cardinality.ONE);

      fuClass.withBidirectional(assocRole, "roles", Cardinality.MANY, "clazz", Cardinality.ONE);

      assocRole.withBidirectional(assocRole, "other", Cardinality.ONE, "other", Cardinality.ONE);

      Clazz fileFragmentMap = model.createClazz("FileFragmentMap")
            .withAttribute("fileName", DataType.STRING);

      Clazz codeFragment = model.createClazz("CodeFragment")
            .withAttribute("key", DataType.STRING)
            .withAttribute("text", DataType.STRING);

      model.generate();
   }
}
