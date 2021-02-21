# fulib - Fujaba library

[![Java CI](https://github.com/fujaba/fulib/workflows/Java%20CI/badge.svg)](https://github.com/fujaba/fulib/actions)
[![Download](https://api.bintray.com/packages/fujaba/maven/fulib/images/download.svg)](https://bintray.com/fujaba/maven/fulib/_latestVersion "Download")

Fulib is a library that provides code generation for UML like models and some model management functionalities.
Using a domain-specific language provided by Java APIs, it allows you to define classes, attributes and associations with a meta model.
From the meta model definition, Fulib can automatically generate Java code.
The generated code ensures referential integrity and can optionally include support for property change listeners or JavaFX.

Fulib-generated Java files can seamlessly coexist with hand-written code and may even be modified.
Great care is taken that no hand-written code is deleted or changed by code generation.
Fulib is able to read and modify any Java code using language features from up to Java 11,
even if it contains syntax errors or can otherwise not be compiled.

We have an Online Version at www.fulib.org where you can find docs and tutorials for getting started.

## Installation

### Gradle

`build.gradle`:

```groovy
plugins {
    // ...
    // https://plugins.gradle.org/plugin/org.fulib.fulibGradle
    id 'org.fulib.fulibGradle' version '0.4.0'
}

repositories {
    // ...
    mavenCentral()
    jcenter()
}

dependencies {
    // ...

    // https://mvnrepository.com/artifact/org.fulib/fulibScenarios
    fulibScenarios group: 'org.fulib', name: 'fulibScenarios', version: '1.5.0'

    // optional, to override the version of fulib used by fulibScenarios:
    // https://mvnrepository.com/artifact/org.fulib/fulib
    fulibScenarios group: 'org.fulib', name: 'fulib', version: '1.4.1'
}
```

### Maven

Maven is currently not supported.
See [issue #52](https://github.com/fujaba/fulib/issues/52) for more info.

## Usage

### Defining the Class Model

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
[Class Model Definition](doc/ClassModelDefinition.md) in the docs describes this with more details and options.
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
However, you can [browse the generated files](test/src/main/java/de/uniks/studyright).

### Generating Java Code

Now, run `gradle generateScenarioSource`.
This will run the code you put in the `GenModel.decorate` method and generate all classes you described.
You can check out the results in the `de.uniks.studyright` package in the `src/main/java` source directory.

Rendered as a class diagram this model looks like this:

![University class diagram](test/src/main/java/de/uniks/studyright/classDiagram.png)

### Using Generated Code

Now you can use the generated classes from your code (in `src/main/java` and `src/test/java`).
Here's an example for our university model:

<!-- insert_code_fragment: test.UniversityModelUsage | fenced:java -->
```java
University studyRight = new University().setName("Study Right");

Room mathRoom = new Room().setTopic("math room");
studyRight.withRooms(mathRoom);
Room modelingRoom = new Room().setTopic("modeling room").setUni(studyRight);
Student alice = new Student().setName("Alice").setStudentId("A4242").setIn(mathRoom);
Student bob = new Student().setName("Bob").setStudentId("B2323").setIn(mathRoom);
studyRight.withStudents(alice, bob);
```
<!-- end_code_fragment: -->

### Object Diagrams

The code using the model creates the object structure shown in the object diagram below.

![simple object diagram](test/doc/images/studyRightObjects.png)

To create an object diagram from your object structure, add this line:

<!-- insert_code_fragment: test.UniversityObjectDiagram | fenced:java -->
```java
FulibTools.objectDiagrams().dumpPng("doc/images/studyRightObjects.png", studyRight);
```
<!-- end_code_fragment: -->

This requires adding [fulibTools](https://github.com/fujaba/fulibTools) as a dependency.

---

Fulib also provides means for model queries and model transformations, see:
[Fulib Tables](doc/FulibTables.md)

## History

Fulib is the newest tool of the Fujaba Family https://github.com/fujaba .

| Period | Activity |
| --- | --- |
| 1998 - 2008 | We developed the Fujaba (From UML to Java And Back Again) tool as a graphical editor for class diagrams and model transformations. |
| 2008 - 2018 | We moved on to [SDMLib](https://github.com/fujaba/SDMLib) (Story Driven Modeling Library). SDMLib got rid of the graphical editors. |
| 2019 - present | We did a major refactoring of the SDMLib and call it Fulib (Fujaba Library) now. |

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

## License

[MIT](LICENSE.md)
