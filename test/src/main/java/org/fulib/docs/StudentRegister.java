package org.fulib.docs;

import java.util.ArrayList;
import java.util.Collection;

// start_code_fragment: docs.StudentRegister
class StudentRegister extends ArrayList<Student>
{
   public StudentRegister()
   {
   }

   public StudentRegister(Collection<? extends Student> c)
   {
      super(c);
   }
}
// end_code_fragment:
