package org.fulib.docs;

public class Example
{
   public static final String PROPERTY_LABEL = "label";
   public static final String PROPERTY_SCORE = "score";

   // start_code_fragment: docs.InitialValue
   private String label = "P1";
   private int score = 100;
   // end_code_fragment:

   public String getLabel()
   {
      return this.label;
   }

   public Example setLabel(String value)
   {
      this.label = value;
      return this;
   }

   public int getScore()
   {
      return this.score;
   }

   public Example setScore(int value)
   {
      this.score = value;
      return this;
   }

   // start_code_fragment: docs.Description
   public static final String PROPERTY_FULL_NAME = "fullName";
   /** @since 1.2 */
   public static final String PROPERTY_HEIGHT = "height";

   private String fullName;
   private double height;

   /**
    * @return the full name including first, middle and last names
    */
   public String getFullName()
   {
      return this.fullName;
   }

   /**
    * @param value
    *    the full name including first, middle and last names
    *
    * @return this
    */
   public Example setFullName(String value)
   {
      this.fullName = value;
      return this;
   }

   /**
    * @return the height in meters
    *
    * @since 1.2
    */
   public double getHeight()
   {
      return this.height;
   }

   /**
    * @param value
    *    the height in meters
    *
    * @return this
    *
    * @since 1.2
    */
   public Example setHeight(double value)
   {
      this.height = value;
      return this;
   }
   // end_code_fragment:

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getLabel());
      result.append(' ').append(this.getFullName());
      return result.substring(1);
   }
}
