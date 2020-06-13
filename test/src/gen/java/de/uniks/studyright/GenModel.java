// start_code_fragment: test.GenModel
package de.uniks.studyright;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.Clazz;

public class GenModel implements ClassModelDecorator
{
   @Override
   public void decorate(ClassModelManager mm)
   {
      final Clazz university = mm.haveClass("University", c -> {
         c.attribute("name", Type.STRING);
      });

      final Clazz student = mm.haveClass("Student", c -> {
         c.attribute("name", Type.STRING);
         c.attribute("studentId", Type.STRING);
         c.attribute("credits", Type.INT);
         c.attribute("motivation", Type.DOUBLE);
      });

      final Clazz room = mm.haveClass("Room", c -> {
         c.attribute("roomNo", Type.STRING);
         c.attribute("topic", Type.STRING);
         c.attribute("credits", Type.INT);
      });

      // a university has many students, students have one uni
      mm.associate(university, "students", Type.MANY, student, "uni", Type.ONE);

      // a university has many rooms, a room has one uni
      mm.associate(university, "rooms", Type.MANY, room, "uni", Type.ONE);

      // a room has many students, a student is in one room
      mm.associate(room, "students", Type.MANY, student, "in", Type.ONE);
   }
}
// end_code_fragment:
