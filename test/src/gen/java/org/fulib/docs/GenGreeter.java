package org.fulib.docs;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.Clazz;

// start_code_fragment: docs.GenGreeter
public class GenGreeter implements ClassModelDecorator
{
   class Greeter
   {
      String name;
   }

   @Override
   public void decorate(ClassModelManager m)
   {
      m.getClassModel().setDefaultPropertyStyle(Type.POJO);

      Clazz greeter = m.haveClass(Greeter.class);
      m.haveMethod(greeter,
         /* declaration: */ "public void greet(String other)",
         /* body: */ "System.out.println(\"Hello \" + other + \"! This is \" + this.getName());");
   }
}
// end_code_fragment:
