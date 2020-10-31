package org.fulib.docs;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Type;
import org.fulib.builder.reflect.Description;
import org.fulib.builder.reflect.InitialValue;
import org.fulib.builder.reflect.Link;
import org.fulib.builder.reflect.Since;

import java.util.ArrayList;
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

   class Example
   {
      // start_code_fragment: docs.GenModel.InitialValue
      @InitialValue("\"P1\"")
      String label;

      @InitialValue("100")
      int score;
      // end_code_fragment:

      // start_code_fragment: docs.GenModel.Description
      @Description("the full name including first, middle and last names")
      String fullName;

      @Description("the height in meters")
      @Since("1.2")
      double height;
      // end_code_fragment:

      // start_code_fragment: docs.GenModel.Type
      @Type("Color")
      Object color;

      @Type("int")
      IntArrayList ints; // it.unimi.dsi.fastutil.ints.IntArrayList

      @Type("Student")
      StudentRegister students;
      // end_code_fragment:
   }

   @Override
   public void decorate(ClassModelManager cmm)
   {
      cmm.getClassModel().setDefaultPropertyStyle(org.fulib.builder.Type.POJO);
      cmm.haveClasses(GenModel.class);
   }
}

class StudentRegister extends ArrayList<GenModel.Student>
{}
