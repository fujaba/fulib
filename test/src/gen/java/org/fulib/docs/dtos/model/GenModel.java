package org.fulib.docs.dtos.model;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

// start_code_fragment: docs.dtos.GenModel
public class GenModel implements ClassModelDecorator
{
   public class User
   {
      String id;
      String name;
      @Link("user")
      Address address;
   }

   public class Address
   {
      String id;
      String city;
      String street;
      @Link("address")
      User user;
   }

   @Override
   public void decorate(ClassModelManager m)
   {
      m.haveNestedClasses(GenModel.class);
   }
}
// end_code_fragment:
