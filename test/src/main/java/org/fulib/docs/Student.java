package org.fulib.docs;

// start_code_fragment: docs.Student
public class Student
{
   public static final String PROPERTY_UNI = "uni";
   private University uni;

   public University getUni()
   {
      return this.uni;
   }

   public Student setUni(University value)
   {
      if (this.uni == value)
      {
         return this;
      }

      final University oldValue = this.uni;
      if (this.uni != null)
      {
         this.uni = null;
         oldValue.withoutStudents(this);
      }
      this.uni = value;
      if (value != null)
      {
         value.withStudents(this);
      }
      return this;
   }

   public void removeYou()
   {
      this.setUni(null);
   }
}
// end_code_fragment:
