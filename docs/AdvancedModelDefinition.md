# Advanced Model Definition

You can customize how you define and generate your model with a variety of annotations, each of which is described below.

## Initial Values using `@InitialValue`

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

## JavaDocs with `@Description` and `@Since`

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

## Custom types using `@Type`

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
