package de.uniks.studyright;

import java.beans.PropertyChangeSupport;
import java.util.Objects;
public class Student
{
   public static final String PROPERTY_UNI = "uni";
   public static final String PROPERTY_IN = "in";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_STUDENTID = "studentId";
   public static final String PROPERTY_CREDITS = "credits";
   public static final String PROPERTY_MOTIVATION = "motivation";

   public static final String PROPERTY_name = "name";
   public static final String PROPERTY_STUDENT_ID = "studentId";

   private String name;

   public static final String PROPERTY_studentId = "studentId";

   private String studentId;

   public static final String PROPERTY_credits = "credits";

   private int credits;

   public static final String PROPERTY_motivation = "motivation";

   private double motivation;

   public static final String PROPERTY_uni = "uni";

   private University uni;

   public static final String PROPERTY_in = "in";

   private Room in;

   protected PropertyChangeSupport listeners;

   public String getName()
   {
      return this.name;
   }

   public Student setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public String getStudentId()
   {
      return this.studentId;
   }

   public Student setStudentId(String value)
   {
      if (Objects.equals(value, this.studentId))
      {
         return this;
      }

      final String oldValue = this.studentId;
      this.studentId = value;
      this.firePropertyChange(PROPERTY_STUDENT_ID, oldValue, value);
      return this;
   }

   public int getCredits()
   {
      return this.credits;
   }

   public Student setCredits(int value)
   {
      if (value == this.credits)
      {
         return this;
      }

      final int oldValue = this.credits;
      this.credits = value;
      this.firePropertyChange(PROPERTY_CREDITS, oldValue, value);
      return this;
   }

   public double getMotivation()
   {
      return this.motivation;
   }

   public Student setMotivation(double value)
   {
      if (value == this.motivation)
      {
         return this;
      }

      final double oldValue = this.motivation;
      this.motivation = value;
      this.firePropertyChange(PROPERTY_MOTIVATION, oldValue, value);
      return this;
   }

   public University getUni()
   {
      return this.uni;
   }

   public Student setUni(University value)
   {
      if (this.uni == value)
      {
         return this;
      }

      final University oldValue = this.uni;
      if (this.uni != null)
      {
         this.uni = null;
         oldValue.withoutStudents(this);
      }
      this.uni = value;
      if (value != null)
      {
         value.withStudents(this);
      }
      this.firePropertyChange(PROPERTY_UNI, oldValue, value);
      return this;
   }

   public Room getIn()
   {
      return this.in;
   }

   public Student setIn(Room value)
   {
      if (this.in == value)
      {
         return this;
      }

      final Room oldValue = this.in;
      if (this.in != null)
      {
         this.in = null;
         oldValue.withoutStudents(this);
      }
      this.in = value;
      if (value != null)
      {
         value.withStudents(this);
      }
      this.firePropertyChange(PROPERTY_IN, oldValue, value);
      return this;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      result.append(' ').append(this.getStudentId());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setIn(null);
      this.setUni(null);
   }

}
