package org.fulib.docs;

// start_code_fragment: docs.Greeter
public class Greeter
{
   public static final String PROPERTY_NAME = "name";
   private String name;

   public String getName()
   {
      return this.name;
   }

   public Greeter setName(String value)
   {
      this.name = value;
      return this;
   }

   public void greet(String other)
   {
      System.out.println("Hello " + other + "! This is " + this.getName());
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      return result.substring(1);
   }
}
// end_code_fragment:
