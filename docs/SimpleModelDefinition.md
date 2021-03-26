# Class Model Definition

Class models for fulib are defined using familiar Java syntax.
We start by declaring a simple class with some attributes.

<!-- insert_code_fragment: docs.GenModel.Person | fenced:java -->
```java
class Person
{
   String name;
   int age;
   boolean vip;
}
```
<!-- end_code_fragment: -->

> #### ⓘ Hint
>
> Neither the class nor the attributes need to be `public`.
> In fact, access modifiers are ignored and discouraged for brevity.

This will generate a `Person` class with a `private` field and `public` getters and setters.

For attributes with the type `boolean`, the getter starts with `is`<sup>since v1.5</sup>.
All other types use `get`.

<!-- insert_code_fragment: docs.Person | fenced:java -->
```java
public class Person
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_AGE = "age";
   public static final String PROPERTY_VIP = "vip";
   private String name;
   private int age;
   private boolean vip;

   public String getName()
   {
      return this.name;
   }

   public Person setName(String value)
   {
      this.name = value;
      return this;
   }

   public int getAge()
   {
      return this.age;
   }

   public Person setAge(int value)
   {
      this.age = value;
      return this;
   }

   public boolean isVip()
   {
      return this.vip;
   }

   public Person setVip(boolean value)
   {
      this.vip = value;
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      return result.substring(1);
   }
}
```
<!-- end_code_fragment: -->

Attributes with collection types, i.e. those extending `Collection`, are treated in a special way by fulib.
Instead of a setter, those will generate `with` and `without` methods.
The getter will make sure that the collection cannot be modified from the outside.

<!-- insert_code_fragment: docs.GenModel.Page | fenced:java -->
```java
class Page
{
   List<String> lines;
}
```
<!-- end_code_fragment: -->

<!-- insert_code_fragment: docs.Page | fenced:java -->
```java
public class Page
{
   public static final String PROPERTY_LINES = "lines";
   private List<String> lines;

   public List<String> getLines()
   {
      return this.lines != null ? Collections.unmodifiableList(this.lines) : Collections.emptyList();
   }

   public Page withLines(String value)
   {
      if (this.lines == null)
      {
         this.lines = new ArrayList<>();
      }
      this.lines.add(value);
      return this;
   }

   public Page withLines(String... value)
   {
      this.withLines(Arrays.asList(value));
      return this;
   }

   public Page withLines(Collection<? extends String> value)
   {
      if (this.lines == null)
      {
         this.lines = new ArrayList<>(value);
      }
      else
      {
         this.lines.addAll(value);
      }
      return this;
   }

   public Page withoutLines(String value)
   {
      this.lines.removeAll(Collections.singleton(value));
      return this;
   }

   public Page withoutLines(String... value)
   {
      this.withoutLines(Arrays.asList(value));
      return this;
   }

   public Page withoutLines(Collection<? extends String> value)
   {
      if (this.lines != null)
      {
         this.lines.removeAll(value);
      }
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getLines());
      return result.substring(1);
   }
}
```
<!-- end_code_fragment: -->

The generated code shows that `ArrayList` was automatically chosen as the implementation class for `List`.
Other collection int, like `Set`, have other default implementation types.
You can also specify the implementation type manually if you don't want to use the default -- fulib will automatically infer the most specific interface.
The following tables shows the mapping from interfaces to default implementations.

| Interface      | Default Implementation |
|----------------|------------------------|
| `Collection`   | `LinkedHashSet`        |
| `Set`          | `LinkedHashSet`        |
| `SortedSet`    | `TreeSet`              |
| `NavigableSet` | `TreeSet`              |
| `List`         | `ArrayList`            |

Attributes can be turned into associations and customized using a number of annotations, which are covered in the following sections.

## Associations using `@Link`

This annotation is required to define an association.
All you have to do is pass the name of reverse role in the target class:

<!-- insert_code_fragment: docs.GenModel.Student | fenced:java -->
```java
class Student
{
   @Link("students")
   University uni;
}
```
<!-- end_code_fragment: -->

<!-- insert_code_fragment: docs.GenModel.University | fenced:java -->
```java
class University
{
   @Link("uni")
   List<Student> students;

   @Link
   Person president;

   @Link
   List<Person> employees;
}
```
<!-- end_code_fragment: -->

The `@Link` annotation is primarily intended for *bidirectional* associations.
The generated code will ensure referential integrity when setting a student's university or when adding or removing students to a university.
In case you want a *unidirectional* association, you can simply omit the annotation argument, as shown with `president` and `employees` in the `University` example.

<!-- insert_code_fragment: docs.Student | fenced:java -->
```java
public class Student
{
   public static final String PROPERTY_UNI = "uni";
   private University uni;

   public University getUni()
   {
      return this.uni;
   }

   public Student setUni(University value)
   {
      if (this.uni == value)
      {
         return this;
      }

      final University oldValue = this.uni;
      if (this.uni != null)
      {
         this.uni = null;
         oldValue.withoutStudents(this);
      }
      this.uni = value;
      if (value != null)
      {
         value.withStudents(this);
      }
      return this;
   }

   public void removeYou()
   {
      this.setUni(null);
   }
}
```
<!-- end_code_fragment: -->

<!-- insert_code_fragment: docs.University | fenced:java -->
```java
public class University
{
   public static final String PROPERTY_STUDENTS = "students";
   public static final String PROPERTY_PRESIDENT = "president";
   public static final String PROPERTY_EMPLOYEES = "employees";
   private List<Student> students;
   private Person president;
   private List<Person> employees;

   public List<Student> getStudents()
   {
      return this.students != null ? Collections.unmodifiableList(this.students) : Collections.emptyList();
   }

   public University withStudents(Student value)
   {
      if (this.students == null)
      {
         this.students = new ArrayList<>();
      }
      if (!this.students.contains(value))
      {
         this.students.add(value);
         value.setUni(this);
      }
      return this;
   }

   public University withStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public University withStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public University withoutStudents(Student value)
   {
      if (this.students != null && this.students.remove(value))
      {
         value.setUni(null);
      }
      return this;
   }

   public University withoutStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
   }

   public University withoutStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
   }

   public Person getPresident()
   {
      return this.president;
   }

   public University setPresident(Person value)
   {
      this.president = value;
      return this;
   }

   public List<Person> getEmployees()
   {
      return this.employees != null ? Collections.unmodifiableList(this.employees) : Collections.emptyList();
   }

   public University withEmployees(Person value)
   {
      if (this.employees == null)
      {
         this.employees = new ArrayList<>();
      }
      if (!this.employees.contains(value))
      {
         this.employees.add(value);
      }
      return this;
   }

   public University withEmployees(Person... value)
   {
      for (final Person item : value)
      {
         this.withEmployees(item);
      }
      return this;
   }

   public University withEmployees(Collection<? extends Person> value)
   {
      for (final Person item : value)
      {
         this.withEmployees(item);
      }
      return this;
   }

   public University withoutEmployees(Person value)
   {
      if (this.employees != null)
      {
         this.employees.remove(value);
      }
      return this;
   }

   public University withoutEmployees(Person... value)
   {
      for (final Person item : value)
      {
         this.withoutEmployees(item);
      }
      return this;
   }

   public University withoutEmployees(Collection<? extends Person> value)
   {
      for (final Person item : value)
      {
         this.withoutEmployees(item);
      }
      return this;
   }

   public void removeYou()
   {
      this.withoutStudents(new ArrayList<>(this.getStudents()));
      this.setPresident(null);
      this.withoutEmployees(new ArrayList<>(this.getEmployees()));
   }
}
```
<!-- end_code_fragment: -->
