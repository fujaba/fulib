package org.fulib.docs;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.builder.reflect.Link;

import java.util.List;

public class GenModel implements ClassModelDecorator
{
   // start_code_fragment: docs.GenModel.Person
   class Person
   {
      String name;
      int age;
   }
   // end_code_fragment:

   // start_code_fragment: docs.GenModel.Page
   class Page
   {
      List<String> lines;
   }
   // end_code_fragment:

   // start_code_fragment: docs.GenModel.Student
   class Student
   {
      @Link("students")
      University uni;
   }
   // end_code_fragment:

   // start_code_fragment: docs.GenModel.University
   class University
   {
      @Link("uni")
      List<Student> students;
   }
   // end_code_fragment:

   @Override
   public void decorate(ClassModelManager cmm)
   {
      cmm.getClassModel().setDefaultPropertyStyle(Type.POJO);
      cmm.haveClasses(GenModel.class);
   }
}
