# Custom Types

The `@Type` annotation can override the type of an attribute or association if it cannot be otherwise named or determined.
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
