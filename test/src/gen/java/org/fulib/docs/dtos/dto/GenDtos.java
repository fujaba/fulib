package org.fulib.docs.dtos.dto;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.builder.reflect.DTO;
import org.fulib.docs.dtos.model.GenModel;

// start_code_fragment: docs.dtos.GenDtos
public class GenDtos implements ClassModelDecorator
{
   @DTO(model = GenModel.User.class, omit = { "id" })
   class UserDto
   {}

   @DTO(model = GenModel.Address.class, pick = { "city", "street" })
   class AddressDto
   {}

   @Override
   public void decorate(ClassModelManager m)
   {
      // This omits PropertyChangeListeners etc. from the generated code
      m.getClassModel().setDefaultPropertyStyle(Type.POJO);
      m.haveNestedClasses(GenDtos.class);
   }
}
// end_code_fragment:
