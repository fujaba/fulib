package org.fulib.docs.dtos.dto;

// start_code_fragment: docs.dtos.UserDto
public class UserDto
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ADDRESS = "address";
   private String name;
   private String address;

   public String getName()
   {
      return this.name;
   }

   public UserDto setName(String value)
   {
      this.name = value;
      return this;
   }

   public String getAddress()
   {
      return this.address;
   }

   public UserDto setAddress(String value)
   {
      this.address = value;
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getAddress());
      return result.substring(1);
   }
}
// end_code_fragment:
