# Defining the Class Model

In the following tutorial, we build a class model for a university.
It uses the package name `de.uniks.studyright`, which you can replace according to your needs.

Create a class `GenModel` in the `de.uniks.studyright` package and **put it in the `src/gen/java` source directory**.
The name `GenModel` is only a convention, you can also use a different one.

<!-- insert_code_fragment: test.GenModel | fenced:java -->
```java
package de.uniks.studyright;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.List;

public class GenModel implements ClassModelDecorator
{
   class University
   {
      String name;

      @Link("uni")
      List<Student> students;

      @Link("uni")
      List<Room> rooms;
   }

   class Student
   {
      String name;
      String studentId;
      int credits;
      double motivation;

      @Link("students")
      University uni;

      @Link("students")
      Room in;
   }

   class Room
   {
      String roomNo;
      String topic;
      int credits;

      @Link("rooms")
      University uni;

      @Link("in")
      List<Student> students;
   }

   @Override
   public void decorate(ClassModelManager mm)
   {
      mm.haveNestedClasses(GenModel.class);
   }
}
```
<!-- end_code_fragment: -->

Within `GenModel`, we define our data model using a familiar yet shortened Java syntax.
Every nested class is translated to a top-level class in the `src/main/java` source directory.
The [detailed docs](../definitions/README.md) describe this with more details and options.
For now, we present a simple example in which we define three classes each with some attributes and associations.

Attributes are defined just like a normal field.
Fulib takes care of generating getters and setters.
It also ensures the correct access modifiers (`private`, `public`) are used in the generated code.
We can simply omit them when defining the model within `GenModel`.

Associations are defined using the `@Link` annotation.
You specify the name of the reverse role as the annotation argument.
This way, fulib can generate setters that ensure referential integrity.
To-many associations are specified by wrapping the target type in a collection, e.g. `List<Student>`.
Instead of a setter, fulib will generate `with` and `without` methods for these associations.

For brevity, we do not show the generated code here.
However, you can [browse the generated files](../../test/src/main/java/de/uniks/studyright).
