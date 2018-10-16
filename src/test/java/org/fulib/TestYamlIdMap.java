package org.fulib;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.Clazz;
import org.fulib.yaml.YamlIdMap;
import org.fulib.yaml.YamlObject;
import org.fulib.yaml.Yamler;
import org.junit.Test;

import java.util.LinkedHashMap;

public class TestYamlIdMap
{
   @Test
   public void testPlainYaml()
   {
      String yaml = "" +
            "joining: abu \n" +
            "lastChanges: 2018-03-17T14:48:00.000.abu 2018-03-17T14:38:00.000.bob 2018-03-17T14:18:00.000.xia";

      Yamler yamler = new Yamler();

      LinkedHashMap<String, String> map = yamler.decode(yaml);
      assertThat(map.get("joining"), equalTo("abu"));

      yaml = "" +
            "- m: .Map\n" +
            "  joining: abu\n" +
            "  lastChanges: 2018-03-17T14:48:00.000.abu 2018-03-17T14:38:00.000.bob 2018-03-17T14:18:00.000.xia";

      YamlIdMap idMap = new YamlIdMap("");

      YamlObject yamlObj = (YamlObject) idMap.decode(yaml);

      assertThat(yamlObj.getMap().get("joining"), equalTo("abu"));
   }


   @Test
   public void testYamlIdMap()
   {
      // create example data
      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright", "tmp/src");

      ClassBuilder university = mb.buildClass("University")
            .buildAttribute("uniName", mb.STRING);

      ClassBuilder student = mb.buildClass("Student")
            .buildAttribute("studentId", mb.STRING);

      university.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);

      // encode it
      String packageName = ClassModel.class.getPackage().getName();

      YamlIdMap idMap = new YamlIdMap(packageName);

      String yamlString = idMap.encode(mb.getClassModel());

      assertThat(yamlString, containsString("packageName: \torg.fulib.studyright"));
      assertThat(yamlString, containsString("name: \tUniversity"));
      assertThat(yamlString, containsString("name: \tuniName"));
      assertThat(yamlString, containsString("type: \tString"));
      assertThat(yamlString, containsString("name: \tstudents"));
      assertThat(yamlString, containsString("roleType: \t\"java.util.ArrayList<%s>\""));
      assertThat(yamlString, containsString("name: \tstudentId"));

      // decode it
      YamlIdMap readMap = new YamlIdMap(packageName);

      Object result = readMap.decode(yamlString);

      ClassModel readModel = (ClassModel) result;

      assertThat(readModel, notNullValue());
      assertThat(readModel.getClasses().size(), equalTo(2));
      Clazz clazz = readModel.getClasses().get(0);
      assertThat(clazz.getAttributes().size(), not(equalTo(0)));
      assertThat(clazz.getRoles().size(), not(equalTo(0)));
      AssocRole role = clazz.getRoles().get(0);
      assertThat(role.getOther(), notNullValue());
   }
}
