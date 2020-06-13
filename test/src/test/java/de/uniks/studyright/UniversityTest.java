package de.uniks.studyright;

import org.junit.Test;

public class UniversityTest
{
   @Test
   public void test()
   {
      University studyRight = new University().setName("Study Right");

      Room mathRoom = new Room().setTopic("math room");
      studyRight.withRooms(mathRoom);
      Room modelingRoom = new Room().setTopic("modeling room").setUni(studyRight);
      Student alice = new Student().setName("Alice").setStudentId("A4242").setIn(mathRoom);
      Student bob = new Student().setName("Bob").setStudentId("B2323").setIn(mathRoom);
      studyRight.withStudents(alice, bob);
   }
}
