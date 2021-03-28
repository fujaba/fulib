# Using Generated Code

Now you can use the generated classes from your code (in `src/main/java` and `src/test/java`).
Here's an example for our university model:

`src/test/java/de/uniks/studyright/Test.java`:

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

The code using the model creates the object structure shown in the object diagram below.

`doc/images/studyRightObjects.png`:

![simple object diagram](../../test/doc/images/studyRightObjects.png)
