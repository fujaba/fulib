# fulib - Fujaba library [![Build Status](https://travis-ci.org/fujaba/fulib.svg?branch=master)](https://travis-ci.org/fujaba/fulib)

Fulib is a Java-code generating library.

First you write code that builds up your class model:

<!-- insert_code_fragment: test4FulibReadme.classmodel -->
      ClassModelBuilder mb = Fulib.classModelBuilder("de.uniks.studyright");
      ClassBuilder uni = mb.buildClass("University")
            .buildAttribute("name", mb.STRING);
      ClassBuilder student = mb.buildClass("Student")
            .buildAttribute("matNo", mb.INT);
      uni.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);
      ClassBuilder room = mb.buildClass("Room")
            .buildAttribute("roomNo", mb.STRING);
      uni.buildAssociation(room, "rooms", mb.MANY, "uni", mb.ONE);

      ClassModel model = mb.getClassModel();
<!-- end_code_fragment: -->

Rendered as a class diagram this model looks like:

![simple class diagram](doc/images/SimpleClassDiagram.png)

From the class model you may generate Java code that implements the modeled classes:

<!-- insert_code_fragment: test4FulibReadme.generate -->
      // Fulib.generator().generate(model);
<!-- end_code_fragment: -->

Once your IDE has compiled the generated code, you may use it like:

<!-- insert_code_fragment: StudyRightUserStories.testSimpleObjectModel -->
      University studyRight = new University();

      Room wa1337 = new Room().setRoomNo("WA1337");
      studyRight.withRooms(wa1337);

      Student alice = new Student().setName("Alice").setStudentId("A4242").setIn(wa1337);
      Student   bob = new Student().setName("Bob")  .setStudentId("B2323").setIn(wa1337);
      studyRight.withStudents(alice, bob);
<!-- end_code_fragment: -->

This application code creates the object structure shown in the object diagram shown below.

To create an object diagram from your object structure use:


<!-- insert_code_fragment: StudyRightUserStories.FulibTools.objectDiagrams -->
      FulibTools.objectDiagrams().dumpPng("../fulib/doc/images/studyRightObjects.png", studyRight);
<!-- end_code_fragment: -->

![simple object diagram](doc/images/studyRightObjects.png)


### Gradle

<pre>
<!-- insert_code_fragment: gradle.repositories -->
repositories {
    mavenCentral()
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}
<!-- end_code_fragment: -->
</pre>

<pre>
<!-- insert_code_fragment: gradle.dependencies -->
dependencies {
    testCompile 'org.fulib:fulib:1.0.+'
<!-- end_code_fragment: -->
</pre>