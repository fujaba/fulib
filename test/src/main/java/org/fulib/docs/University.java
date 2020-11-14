package org.fulib.docs;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

// start_code_fragment: docs.University
public class University
{
   public static final String PROPERTY_STUDENTS = "students";
   public static final String PROPERTY_PRESIDENT = "president";
   public static final String PROPERTY_EMPLOYEES = "employees";
   private List<Student> students;
   private Person president;
   private List<Person> employees;

   public List<Student> getStudents()
   {
      return this.students != null ? Collections.unmodifiableList(this.students) : Collections.emptyList();
   }

   public University withStudents(Student value)
   {
      if (this.students == null)
      {
         this.students = new ArrayList<>();
      }
      if (!this.students.contains(value))
      {
         this.students.add(value);
         value.setUni(this);
      }
      return this;
   }

   public University withStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public University withStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public University withoutStudents(Student value)
   {
      if (this.students != null && this.students.remove(value))
      {
         value.setUni(null);
      }
      return this;
   }

   public University withoutStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
   }

   public University withoutStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
   }

   public Person getPresident()
   {
      return this.president;
   }

   public University setPresident(Person value)
   {
      this.president = value;
      return this;
   }

   public List<Person> getEmployees()
   {
      return this.employees != null ? Collections.unmodifiableList(this.employees) : Collections.emptyList();
   }

   public University withEmployees(Person value)
   {
      if (this.employees == null)
      {
         this.employees = new ArrayList<>();
      }
      if (!this.employees.contains(value))
      {
         this.employees.add(value);
      }
      return this;
   }

   public University withEmployees(Person... value)
   {
      for (final Person item : value)
      {
         this.withEmployees(item);
      }
      return this;
   }

   public University withEmployees(Collection<? extends Person> value)
   {
      for (final Person item : value)
      {
         this.withEmployees(item);
      }
      return this;
   }

   public University withoutEmployees(Person value)
   {
      if (this.employees != null)
      {
         this.employees.remove(value);
      }
      return this;
   }

   public University withoutEmployees(Person... value)
   {
      for (final Person item : value)
      {
         this.withoutEmployees(item);
      }
      return this;
   }

   public University withoutEmployees(Collection<? extends Person> value)
   {
      for (final Person item : value)
      {
         this.withoutEmployees(item);
      }
      return this;
   }

   public void removeYou()
   {
      this.withoutStudents(new ArrayList<>(this.getStudents()));
      this.setPresident(null);
      this.withoutEmployees(new ArrayList<>(this.getEmployees()));
   }
}
// end_code_fragment:
