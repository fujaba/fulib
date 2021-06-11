package org.fulib.builder;

import org.fulib.builder.event.GenEvents;
import org.fulib.builder.model.GenModel;
import org.fulib.builder.reflect.*;
import org.fulib.classmodel.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("unused")
public class ReflectiveClassBuilderTest
{
   class Person
   {
      @Description("the full name")
      String name;

      @Link("friends")
      List<Person> friends;

      Date dateOfBirth;
   }

   class Student extends Person
   {
      @InitialValue("1")
      int studId;

      @Since("1.2")
      LinkedList<String> notes;

      @Link("students")
      University uni;
   }

   class Employee extends Person
   {
      @Link("subordinates")
      Employee manager;

      @Link("manager")
      List<Employee> subordinates;

      Predicate<?> predicate;
   }

   class University
   {
      String name;

      @org.fulib.builder.reflect.Type("SomeEnum")
      Object type;

      @Link("uni")
      List<Student> students;

      @Link()
      Person president;

      @Link()
      List<Person> employees;
   }

   @Test
   public void test()
   {
      final ClassModelManager cmm = new ClassModelManager();
      ReflectiveClassBuilder.load(Person.class, cmm);
      ReflectiveClassBuilder.load(Student.class, cmm);
      ReflectiveClassBuilder.load(Employee.class, cmm);
      ReflectiveClassBuilder.load(University.class, cmm);

      final ClassModel model = cmm.getClassModel();

      final Clazz person = model.getClazz("Person");
      final Attribute personName = person.getAttribute("name");
      assertThat(personName.getType(), equalTo("String"));
      assertThat(personName.getCollectionType(), nullValue());
      assertThat(personName.getDescription(), equalTo("the full name"));

      final Attribute dateOfBirth = person.getAttribute("dateOfBirth");
      assertThat(dateOfBirth.getType(), equalTo("import(java.util.Date)"));

      final AssocRole personFriends = person.getRole("friends");
      assertThat(personFriends.getOther(), is(personFriends));
      assertThat(personFriends.getCardinality(), equalTo(Type.MANY));

      final Clazz student = model.getClazz("Student");
      assertThat(student.getSuperClass(), is(person));

      final Attribute studentId = student.getAttribute("studId");
      assertThat(studentId.getType(), equalTo("int"));
      assertThat(studentId.getInitialization(), equalTo("1"));
      assertThat(studentId.getCollectionType(), nullValue());

      final Attribute studentNotes = student.getAttribute("notes");
      assertThat(studentNotes.getType(), equalTo("String"));
      assertThat(studentNotes.getSince(), equalTo("1.2"));

      final CollectionType notesCollectionType = studentNotes.getCollectionType();
      assertThat(notesCollectionType.getImplClass(), is(LinkedList.class));
      assertThat(notesCollectionType.getItf(), is(CollectionInterface.List));

      final Clazz employee = model.getClazz("Employee");

      final Attribute predicate = employee.getAttribute("predicate");
      assertThat(predicate.getType(), equalTo("import(java.util.function.Predicate)<?>"));

      final AssocRole manager = employee.getRole("manager");
      final AssocRole subordinates = employee.getRole("subordinates");

      assertThat(manager.getOther(), is(subordinates));
      assertThat(manager.getCardinality(), equalTo(Type.ONE));
      assertThat(manager.getCollectionType(), nullValue());

      assertThat(subordinates.getOther(), is(manager));
      assertThat(subordinates.getCardinality(), equalTo(Type.MANY));
      assertThat(subordinates.getCollectionType(), is(CollectionType.ArrayList));

      final Clazz university = model.getClazz("University");

      final Attribute universityType = university.getAttribute("type");
      assertThat(universityType.getType(), equalTo("SomeEnum"));

      final AssocRole students = university.getRole("students");
      final AssocRole uni = student.getRole("uni");

      assertThat(students.getOther(), is(uni));
      assertThat(students.getCardinality(), is(Type.MANY));
      assertThat(students.getCollectionType(), is(CollectionType.ArrayList));

      assertThat(uni.getOther(), is(students));
      assertThat(uni.getCardinality(), is(Type.ONE));
      assertThat(uni.getCollectionType(), nullValue());

      final AssocRole president = university.getRole("president");
      assertThat(president.getCardinality(), is(Type.ONE));
      assertThat(president.getOther().getName(), nullValue());
      assertThat(president.getOther().getClazz(), is(person));

      final AssocRole employees = university.getRole("employees");
      assertThat(employees.getCardinality(), is(Type.MANY));
      assertThat(employees.getOther().getName(), nullValue());
      assertThat(employees.getOther().getClazz(), is(person));
   }

   class StringList extends ArrayList<String>
   {}

   class StringListObj
   {
      StringList list;
   }

   @Test
   public void unknownAttributeElementType()
   {
      final ClassModelManager cmm = new ClassModelManager();

      final InvalidClassModelException ex = assertThrows(InvalidClassModelException.class,
                                                         () -> ReflectiveClassBuilder.load(StringListObj.class, cmm));
      assertThat(ex.getMessage(), equalTo("StringListObj.list: cannot determine element type of StringList"));
   }

   class StudentList extends ArrayList<Student>
   {}

   class StudentListObj
   {
      @Link("f")
      StudentList students;
   }

   @Test
   public void unknownAssocElementType()
   {
      final ClassModelManager cmm = new ClassModelManager();

      final InvalidClassModelException ex = assertThrows(InvalidClassModelException.class,
                                                         () -> ReflectiveClassBuilder.load(StudentListObj.class, cmm));
      assertThat(ex.getMessage(), equalTo("StudentListObj.students: cannot determine element type of StudentList"));
   }

   class InvalidLinkTargetType
   {
      @org.fulib.builder.reflect.Type("Foo")
      @Link("f")
      StudentList students;
   }

   @Test
   public void invalidLinkTargetType()
   {
      final ClassModelManager cmm = new ClassModelManager();

      final InvalidClassModelException ex = assertThrows(InvalidClassModelException.class,
                                                         () -> ReflectiveClassBuilder.load(InvalidLinkTargetType.class,
                                                                                           cmm));

      assertThat(ex.getMessage(), equalTo("InvalidLinkTargetType.students: invalid link target: class Foo not found"));
      assertThat(ex.getCause(), instanceOf(ClassNotFoundException.class));
      assertThat(ex.getCause().getMessage(), equalTo("org.fulib.builder.ReflectiveClassBuilderTest$Foo"));
   }

   class InvalidLinkTargetField
   {
      @Link("foo")
      Student student;
   }

   @Test
   public void invalidLinkTargetField()
   {
      final ClassModelManager cmm = new ClassModelManager();

      final InvalidClassModelException ex = assertThrows(InvalidClassModelException.class,
                                                         () -> ReflectiveClassBuilder.load(InvalidLinkTargetField.class,
                                                                                           cmm));

      assertThat(ex.getMessage(),
                 equalTo("InvalidLinkTargetField.student: invalid link target: field Student.foo not found"));
   }

   class InvalidLinkTargetAnnotation
   {
      @Link("studId")
      Student student;
   }

   @Test
   public void invalidLinkTargetAnnotation()
   {
      final ClassModelManager cmm = new ClassModelManager();

      final InvalidClassModelException ex = assertThrows(InvalidClassModelException.class,
                                                         () -> ReflectiveClassBuilder.load(
                                                            InvalidLinkTargetAnnotation.class, cmm));

      assertThat(ex.getMessage(), equalTo(
         "InvalidLinkTargetAnnotation.student: invalid link target: field Student.studId is not annotated with @Link"));
   }

   class InvalidLinkTargetName
   {
      @Link("uni")
      Student student;
   }

   @Test
   public void invalidLinkTargetName()
   {
      final ClassModelManager cmm = new ClassModelManager();

      final InvalidClassModelException ex = assertThrows(InvalidClassModelException.class,
                                                         () -> ReflectiveClassBuilder.load(InvalidLinkTargetName.class,
                                                                                           cmm));

      assertThat(ex.getMessage(), equalTo(
         "InvalidLinkTargetName.student: invalid link target: field Student.uni is annotated as @Link(\"students\") instead of @Link(\"student\")"));
   }

   class InvalidLinkTargetClass
   {
      @Link("uni")
      Student students;
   }

   @Test
   public void invalidLinkTargetClass()
   {
      final ClassModelManager cmm = new ClassModelManager();

      final InvalidClassModelException ex = assertThrows(InvalidClassModelException.class,
                                                         () -> ReflectiveClassBuilder.load(InvalidLinkTargetClass.class,
                                                                                           cmm));

      assertThat(ex.getMessage(), equalTo(
         "InvalidLinkTargetClass.students: invalid link target: field Student.uni has target type University instead of InvalidLinkTargetClass"));
   }

   @Test
   public void crossGenModelReference()
   {
      final ClassModelManager cmmEvents = new ClassModelManager();
      cmmEvents.haveNestedClasses(GenEvents.class);

      final ClassModel classModelEvents = cmmEvents.getClassModel();
      final Clazz studentEvent = classModelEvents.getClazz("StudentEvent");
      final Attribute studentEventStudent = studentEvent.getAttribute("student");
      assertThat(studentEventStudent.getType(), is("import(org.fulib.builder.model.Student)"));
   }

   class InvalidLinkClassPackage
   {
      @Link
      GenModel.Student student;
   }

   @Test
   public void invalidLinkClassPackage()
   {
      final ClassModelManager cmm = new ClassModelManager();

      final InvalidClassModelException ex = assertThrows(InvalidClassModelException.class,
                                                         () -> ReflectiveClassBuilder.load(
                                                            InvalidLinkClassPackage.class, cmm));

      assertThat(ex.getMessage(), equalTo(
         "InvalidLinkClassPackage.student: invalid link: target class Student (org.fulib.builder.model) must be in the same package (org.fulib.builder)"));
   }

   @DTO(model = Person.class)
   class PersonDto
   {}

   @Test
   public void dto()
   {
      final ClassModelManager cmm = new ClassModelManager();
      final Clazz personDto = ReflectiveClassBuilder.load(PersonDto.class, cmm);

      assertThat(personDto.getRole("links"), nullValue());

      final Attribute name = personDto.getAttribute("name");
      assertThat(name.getType(), is(Type.STRING));

      final Attribute friends = personDto.getAttribute("friends");
      assertThat(friends.getType(), is(Type.STRING));
      assertThat(friends.getCollectionType(), is(CollectionType.ArrayList));

      final Attribute dateOfBirth = personDto.getAttribute("dateOfBirth");
      assertThat(dateOfBirth.getType(), is("import(java.util.Date)"));
   }

   @DTO(model = Person.class, pick = { "friends" })
   class PersonLinksDto
   {}

   @DTO(model = Person.class, omit = { "friends" })
   class PersonAttributesDto
   {}

   @Test
   public void dtoPickOmit()
   {
      final ClassModelManager cmm = new ClassModelManager();
      final Clazz personLinksDto = ReflectiveClassBuilder.load(PersonLinksDto.class, cmm);

      assertThat(personLinksDto.getAttribute("name"), nullValue());
      assertThat(personLinksDto.getAttribute("dateOfBirth"), nullValue());
      assertThat(personLinksDto.getRole("links"), nullValue());

      final Attribute friends = personLinksDto.getAttribute("friends");
      assertThat(friends.getType(), is(Type.STRING));
      assertThat(friends.getCollectionType(), is(CollectionType.ArrayList));

      final Clazz personAttributesDto = ReflectiveClassBuilder.load(PersonAttributesDto.class, cmm);

      final Attribute name = personAttributesDto.getAttribute("name");
      assertThat(name.getType(), is(Type.STRING));

      final Attribute dateOfBirth = personAttributesDto.getAttribute("dateOfBirth");
      assertThat(dateOfBirth.getType(), is("import(java.util.Date)"));

      assertThat(personAttributesDto.getAttribute("links"), nullValue());
      assertThat(personAttributesDto.getRole("links"), nullValue());
   }
}
