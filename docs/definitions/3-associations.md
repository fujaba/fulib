# Associations

The `@Link` annotation is required to define an association.
All you have to do is pass the name of reverse role in the target class:

`src/gen/java/org/fulib/docs/GenModel.java`:

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

`src/main/java/org/fulib/docs/Student.java`:

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

`src/main/java/org/fulib/docs/University.java`:

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
