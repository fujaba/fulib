package org.fulib.docs.dtos.dto;

// start_code_fragment: docs.dtos.AddressDto
public class AddressDto
{
   public static final String PROPERTY_CITY = "city";
   public static final String PROPERTY_STREET = "street";
   private String city;
   private String street;

   public String getCity()
   {
      return this.city;
   }

   public AddressDto setCity(String value)
   {
      this.city = value;
      return this;
   }

   public String getStreet()
   {
      return this.street;
   }

   public AddressDto setStreet(String value)
   {
      this.street = value;
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getCity());
      result.append(' ').append(this.getStreet());
      return result.substring(1);
   }
}
// end_code_fragment:
