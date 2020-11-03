# Class Model Definition

Class models for fulib are defined using familiar Java syntax.
We start by declaring a simple class with some attributes.

<!-- insert_code_fragment: docs.GenModel.Person | fenced:java -->
```java
class Person
{
   String name;
   int age;
}
```
<!-- end_code_fragment: -->

> #### ⓘ Hint
>
> Neither the class nor the attributes need to be `public`.
> In fact, access modifiers are ignored and discouraged for brevity.

This will generate a `Person` class with a `private` field and `public` getters and setters.

<!-- insert_code_fragment: docs.Person | fenced:java -->
```java
public class Person
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_AGE = "age";
   private String name;
   private int age;

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
You can also specify the implement directly if you don't want to use the default.
The following tables shows interfaces 

Attributes can be turned into associations and customized using a number of annotations, which are covered in the following sections.

## `@Link`

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
}
```
<!-- end_code_fragment: -->

The `@Link` annotation is intended for *bidirectional* associations.
The generated code will ensure referential integrity when setting a student's university or when adding or removing students to a university.
*Unidirectional* associations behave no different from attributes, so there is no special annotation for them.

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
   private List<Student> students;

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

   public void removeYou()
   {
      this.withoutStudents(new ArrayList<>(this.getStudents()));
   }
}
```
<!-- end_code_fragment: -->

## `@InitialValue`

Attributes (but not associations) can be initialized with a default value.
This is possible using the `@InitialValue` annotation, which takes a string representing a Java expression.

<!-- insert_code_fragment: docs.GenModel.InitialValue | fenced:java -->
```java
@InitialValue("\"P1\"")
String label;

@InitialValue("100")
int score;
```
<!-- end_code_fragment: -->

<!-- insert_code_fragment: docs.InitialValue | fenced:java -->
```java
private String label = "P1";
private int score = 100;
```
<!-- end_code_fragment: -->

## `@Description` and `@Since`

These annotations are for documenting your model, specifically for generating JavaDocs.
`@Description` takes a description of your attribute or association which will be inserted in the JavaDocs of getters and setters.
`@Since` allows you to specify in which version of the software the attribute or assocation was introduced.
Note that `@Since` only has an effect if `@Description` is also present.

<!-- insert_code_fragment: docs.GenModel.Description | fenced:java -->
```java
@Description("the full name including first, middle and last names")
String fullName;

@Description("the height in meters")
@Since("1.2")
double height;
```
<!-- end_code_fragment: -->

<!-- insert_code_fragment: docs.Description | fenced:java -->
```java
public static final String PROPERTY_FULL_NAME = "fullName";
/** @since 1.2 */
public static final String PROPERTY_HEIGHT = "height";

private String fullName;
private double height;

/**
 * @return the full name including first, middle and last names
 */
public String getFullName()
{
   return this.fullName;
}

/**
 * @param value
 *    the full name including first, middle and last names
 *
 * @return this
 */
public Example setFullName(String value)
{
   this.fullName = value;
   return this;
}

/**
 * @return the height in meters
 *
 * @since 1.2
 */
public double getHeight()
{
   return this.height;
}

/**
 * @param value
 *    the height in meters
 *
 * @return this
 *
 * @since 1.2
 */
public Example setHeight(double value)
{
   this.height = value;
   return this;
}
```
<!-- end_code_fragment: -->

## `@Type`

`@Type` can override the type of an attribute or association if it cannot be otherwise named or determined.
This is useful if the type is not available in the gen source set, or when using a collection type that does not have a generic type argument.

<!-- insert_code_fragment: docs.GenModel.Type | fenced:java -->
```java
@Type("Color")
Object color;

@Type("int")
IntArrayList ints; // it.unimi.dsi.fastutil.ints.IntArrayList

@Type("Student")
StudentRegister students;
```
<!-- end_code_fragment: -->

<!-- insert_code_fragment: docs.StudentRegister | fenced:java -->
```java
class StudentRegister extends ArrayList<Student>
{
   public StudentRegister()
   {
   }

   public StudentRegister(Collection<? extends Student> c)
   {
      super(c);
   }
}
```
<!-- end_code_fragment: -->

<!-- insert_code_fragment: docs.Color | fenced:java -->
```java
public enum Color
{
   RED, GREEN, BLUE,
}
```
<!-- end_code_fragment: -->

<!-- insert_code_fragment: docs.Type | fenced:java -->
```java
private Color color;
private List<Integer> ints;
private List<Student> students;

public Color getColor()
{
   return this.color;
}

public Example setColor(Color value)
{
   this.color = value;
   return this;
}

public List<Integer> getInts()
{
   return this.ints != null ? Collections.unmodifiableList(this.ints) : Collections.emptyList();
}

public Example withInts(Integer value)
{
   if (this.ints == null)
   {
      this.ints = new IntArrayList();
   }
   this.ints.add(value);
   return this;
}

public Example withInts(Collection<? extends Integer> value)
{
   if (this.ints == null)
   {
      this.ints = new IntArrayList(value);
   }
   else
   {
      this.ints.addAll(value);
   }
   return this;
}

public List<Student> getStudents()
{
   return this.students != null ? Collections.unmodifiableList(this.students) : Collections.emptyList();
}

public Example withStudents(Student value)
{
   if (this.students == null)
   {
      this.students = new StudentRegister();
   }
   this.students.add(value);
   return this;
}

public Example withStudents(Collection<? extends Student> value)
{
   if (this.students == null)
   {
      this.students = new StudentRegister(value);
   }
   else
   {
      this.students.addAll(value);
   }
   return this;
}
```
<!-- end_code_fragment: -->