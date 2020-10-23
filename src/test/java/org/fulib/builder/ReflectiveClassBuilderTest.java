package org.fulib.builder;

import org.fulib.classmodel.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class ReflectiveClassBuilderTest
{
   class Person
   {
      String name;
   }

   class Student extends Person
   {
      int studId;

      LinkedList<String> notes;

      @Link("students")
      University uni;
   }

   class University
   {
      String name;
      String address;

      @Link("uni")
      List<Student> students;
   }

   @Test
   public void test()
   {
      final ClassModelManager cmm = new ClassModelManager();
      ReflectiveClassBuilder.load(Person.class, cmm);
      ReflectiveClassBuilder.load(Student.class, cmm);
      ReflectiveClassBuilder.load(University.class, cmm);

      final ClassModel model = cmm.getClassModel();

      final Clazz person = model.getClazz("Person");
      final Attribute personName = person.getAttribute("name");
      assertThat(personName.getType(), equalTo("String"));
      assertThat(personName.getCollectionType(), nullValue());

      final Clazz student = model.getClazz("Student");
      assertThat(student.getSuperClass(), is(person));

      final Attribute studentId = student.getAttribute("studId");
      assertThat(studentId.getType(), equalTo("int"));
      assertThat(studentId.getCollectionType(), nullValue());

      final Attribute studentNotes = student.getAttribute("notes");
      assertThat(studentNotes.getType(), equalTo("String"));

      final CollectionType notesCollectionType = studentNotes.getCollectionType();
      assertThat(notesCollectionType.getImplClass(), is(LinkedList.class));
      assertThat(notesCollectionType.getItf(), is(CollectionInterface.List));

      final Clazz university = model.getClazz("University");

      final AssocRole students = university.getRole("students");
      final AssocRole uni = student.getRole("uni");

      assertThat(students.getOther(), is(uni));
      assertThat(students.getCardinality(), is(Type.MANY));
      assertThat(students.getCollectionType(), is(CollectionType.ArrayList));

      assertThat(uni.getOther(), is(students));
      assertThat(uni.getCardinality(), is(Type.ONE));
      assertThat(uni.getCollectionType(), nullValue());
   }

   class StringList extends ArrayList<String>
   {}

   class StringListObj
   {
      StringList list;
   }

   @Test
   public void stringList()
   {
      final ClassModelManager cmm = new ClassModelManager();

      try
      {
         ReflectiveClassBuilder.load(StringListObj.class, cmm);
         fail("did not throw exception");
      }
      catch (InvalidClassModelException ex)
      {
         assertThat(ex.getMessage(), equalTo(
            "cannot determine element type for org.fulib.builder.ReflectiveClassBuilderTest$StringList org.fulib.builder.ReflectiveClassBuilderTest$StringListObj.list"));
      }
   }

   class StudentList extends ArrayList<Student>
   {}

   class StudentListObj
   {
      @Link("f")
      StudentList students;
   }

   @Test
   public void studentList()
   {
      final ClassModelManager cmm = new ClassModelManager();

      try
      {
         ReflectiveClassBuilder.load(StudentListObj.class, cmm);
         fail("did not throw exception");
      }
      catch (InvalidClassModelException ex)
      {
         assertThat(ex.getMessage(), equalTo(
            "cannot determine element type for org.fulib.builder.ReflectiveClassBuilderTest$StudentList org.fulib.builder.ReflectiveClassBuilderTest$StudentListObj.students"));
      }
   }
}
