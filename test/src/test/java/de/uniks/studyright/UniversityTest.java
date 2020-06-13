package de.uniks.studyright;

import org.fulib.FulibTools;
import org.junit.Test;

public class UniversityTest
{
   @Test
   public void test()
   {
      // start_code_fragment: test.UniversityModelUsage
      University studyRight = new University().setName("Study Right");

      Room mathRoom = new Room().setTopic("math room");
      studyRight.withRooms(mathRoom);
      Room modelingRoom = new Room().setTopic("modeling room").setUni(studyRight);
      Student alice = new Student().setName("Alice").setStudentId("A4242").setIn(mathRoom);
      Student bob = new Student().setName("Bob").setStudentId("B2323").setIn(mathRoom);
      studyRight.withStudents(alice, bob);
      // end_code_fragment:

      // start_code_fragment: test.UniversityObjectDiagram
      FulibTools.objectDiagrams().dumpPng("doc/images/studyRightObjects.png", studyRight);
      // end_code_fragment:
   }
}
