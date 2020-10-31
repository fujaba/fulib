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

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getLabel());
      return result.substring(1);
   }
}
