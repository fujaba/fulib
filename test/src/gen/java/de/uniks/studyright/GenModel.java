// start_code_fragment: test.GenModel
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
      mm.haveClass(University.class);
      mm.haveClass(Student.class);
      mm.haveClass(Room.class);
   }
}
// end_code_fragment:
