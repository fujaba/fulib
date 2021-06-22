# Inheritance

You can easily make model classes extend other model classes, again using familiar Java syntax:

`src/gen/java/org/fulib/docs/GenModel.java`:

```java
class Person
{
   String name;
}

class Student extends Person
{
   int studentId;
}
```

While you cannot add interfaces to a class within `GenModel`, you can simply add an `implements` clause in the generated code.
Fulib will keep that even when regenerating the code.
