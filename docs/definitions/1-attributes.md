# Attributes

Class models for fulib are defined using familiar Java syntax.
We start by declaring a simple class with some attributes.

`src/gen/java/org/fulib/docs/GenModel.java`:

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

`src/main/java/org/fulib/docs/Person.java`:

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
