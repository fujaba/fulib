package de.uniks.studyright;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public class Room
{

   public static final String PROPERTY_roomNo = "roomNo";

   private String roomNo;

   public static final String PROPERTY_topic = "topic";

   private String topic;

   public static final String PROPERTY_credits = "credits";

   private int credits;

   public static final String PROPERTY_uni = "uni";

   private University uni;

   public static final String PROPERTY_students = "students";

   private List<Student> students;

   protected PropertyChangeSupport listeners;
public static final String PROPERTY_ROOMNO = "roomNo";
public static final String PROPERTY_TOPIC = "topic";
public static final String PROPERTY_CREDITS = "credits";

   public String getRoomNo()
   {
      return this.roomNo;
   }

   public Room setRoomNo(String value)
   {
      if (Objects.equals(value, this.roomNo))
      {
         return this;
      }

      final String oldValue = this.roomNo;
      this.roomNo = value;
      this.firePropertyChange(PROPERTY_ROOMNO, oldValue, value);
      return this;
   }

   public String getTopic()
   {
      return this.topic;
   }

   public Room setTopic(String value)
   {
      if (Objects.equals(value, this.topic))
      {
         return this;
      }

      final String oldValue = this.topic;
      this.topic = value;
      this.firePropertyChange(PROPERTY_TOPIC, oldValue, value);
      return this;
   }

   public int getCredits()
   {
      return this.credits;
   }

   public Room setCredits(int value)
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

   public University getUni()
   {
      return this.uni;
   }

   public Room setUni(University value)
   {
      if (this.uni == value)
      {
         return this;
      }

      final University oldValue = this.uni;
      if (this.uni != null)
      {
         this.uni = null;
         oldValue.withoutRooms(this);
      }
      this.uni = value;
      if (value != null)
      {
         value.withRooms(this);
      }
      this.firePropertyChange(PROPERTY_uni, oldValue, value);
      return this;
   }

   public List<Student> getStudents()
   {
      return this.students != null ? Collections.unmodifiableList(this.students) : Collections.emptyList();
   }

   public Room withStudents(Student value)
   {
      if (this.students == null)
      {
         this.students = new ArrayList<>();
      }
      if (!this.students.contains(value))
      {
         this.students.add(value);
         value.setIn(this);
         this.firePropertyChange(PROPERTY_students, null, value);
      }
      return this;
   }

   public Room withStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public Room withStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public Room withoutStudents(Student value)
   {
      if (this.students != null && this.students.remove(value))
      {
         value.setIn(null);
         this.firePropertyChange(PROPERTY_students, value, null);
      }
      return this;
   }

   public Room withoutStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
   }

   public Room withoutStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
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

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getRoomNo());
      result.append(' ').append(this.getTopic());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setUni(null);
      this.withoutStudents(new ArrayList<>(this.getStudents()));
   }

}
