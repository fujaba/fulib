package org.fulib.docs;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.List;
import org.fulib.docs.StudentRegister;
import java.util.Collections;
import java.util.Arrays;
import java.util.Collection;

public class Example
{
   public static final String PROPERTY_LABEL = "label";
   public static final String PROPERTY_SCORE = "score";
   public static final String PROPERTY_COLOR = "color";
   public static final String PROPERTY_INTS = "ints";
   public static final String PROPERTY_STUDENTS = "students";

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

   // start_code_fragment: docs.Type
   private Color color;
   private List<Integer> ints;
   private List<Student> students;

   public Color getColor()
   {
      return this.color;
   }

   public Example setColor(Color value)
   {
      this.color = value;
      return this;
   }

   public List<Integer> getInts()
   {
      return this.ints != null ? Collections.unmodifiableList(this.ints) : Collections.emptyList();
   }

   public Example withInts(Integer value)
   {
      if (this.ints == null)
      {
         this.ints = new IntArrayList();
      }
      this.ints.add(value);
      return this;
   }

   public Example withInts(Collection<? extends Integer> value)
   {
      if (this.ints == null)
      {
         this.ints = new IntArrayList(value);
      }
      else
      {
         this.ints.addAll(value);
      }
      return this;
   }

   public List<Student> getStudents()
   {
      return this.students != null ? Collections.unmodifiableList(this.students) : Collections.emptyList();
   }

   public Example withStudents(Student value)
   {
      if (this.students == null)
      {
         this.students = new StudentRegister();
      }
      this.students.add(value);
      return this;
   }

   public Example withStudents(Collection<? extends Student> value)
   {
      if (this.students == null)
      {
         this.students = new StudentRegister(value);
      }
      else
      {
         this.students.addAll(value);
      }
      return this;
   }
   // end_code_fragment:

   public Example withInts(Integer... value)
   {
      this.withInts(Arrays.asList(value));
      return this;
   }

   public Example withoutInts(Integer value)
   {
      this.ints.removeAll(Collections.singleton(value));
      return this;
   }

   public Example withoutInts(Integer... value)
   {
      this.withoutInts(Arrays.asList(value));
      return this;
   }

   public Example withoutInts(Collection<? extends Integer> value)
   {
      if (this.ints != null)
      {
         this.ints.removeAll(value);
      }
      return this;
   }

   public Example withStudents(Student... value)
   {
      this.withStudents(Arrays.asList(value));
      return this;
   }

   public Example withoutStudents(Student value)
   {
      this.students.removeAll(Collections.singleton(value));
      return this;
   }

   public Example withoutStudents(Student... value)
   {
      this.withoutStudents(Arrays.asList(value));
      return this;
   }

   public Example withoutStudents(Collection<? extends Student> value)
   {
      if (this.students != null)
      {
         this.students.removeAll(value);
      }
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
