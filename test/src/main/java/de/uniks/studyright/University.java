package de.uniks.studyright;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
public class University
{

   public static final String PROPERTY_name = "name";

   private String name;

   public static final String PROPERTY_students = "students";

   private List<Student> students;

   public static final String PROPERTY_rooms = "rooms";

   private List<Room> rooms;

   protected PropertyChangeSupport listeners;
public static final String PROPERTY_NAME = "name";

   public String getName()
   {
      return this.name;
   }

   public University setName(String value)
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

   public List<Student> getStudents()
   {
      return this.students != null ? Collections.unmodifiableList(this.students) : Collections.emptyList();
   }

   public University withStudents(Student value)
   {
      if (this.students == null)
      {
         this.students = new ArrayList<>();
      }
      if (!this.students.contains(value))
      {
         this.students.add(value);
         value.setUni(this);
         this.firePropertyChange(PROPERTY_students, null, value);
      }
      return this;
   }

   public University withStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public University withStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withStudents(item);
      }
      return this;
   }

   public University withoutStudents(Student value)
   {
      if (this.students != null && this.students.remove(value))
      {
         value.setUni(null);
         this.firePropertyChange(PROPERTY_students, value, null);
      }
      return this;
   }

   public University withoutStudents(Student... value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
   }

   public University withoutStudents(Collection<? extends Student> value)
   {
      for (final Student item : value)
      {
         this.withoutStudents(item);
      }
      return this;
   }

   public List<Room> getRooms()
   {
      return this.rooms != null ? Collections.unmodifiableList(this.rooms) : Collections.emptyList();
   }

   public University withRooms(Room value)
   {
      if (this.rooms == null)
      {
         this.rooms = new ArrayList<>();
      }
      if (!this.rooms.contains(value))
      {
         this.rooms.add(value);
         value.setUni(this);
         this.firePropertyChange(PROPERTY_rooms, null, value);
      }
      return this;
   }

   public University withRooms(Room... value)
   {
      for (final Room item : value)
      {
         this.withRooms(item);
      }
      return this;
   }

   public University withRooms(Collection<? extends Room> value)
   {
      for (final Room item : value)
      {
         this.withRooms(item);
      }
      return this;
   }

   public University withoutRooms(Room value)
   {
      if (this.rooms != null && this.rooms.remove(value))
      {
         value.setUni(null);
         this.firePropertyChange(PROPERTY_rooms, value, null);
      }
      return this;
   }

   public University withoutRooms(Room... value)
   {
      for (final Room item : value)
      {
         this.withoutRooms(item);
      }
      return this;
   }

   public University withoutRooms(Collection<? extends Room> value)
   {
      for (final Room item : value)
      {
         this.withoutRooms(item);
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
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.withoutStudents(new ArrayList<>(this.getStudents()));
      this.withoutRooms(new ArrayList<>(this.getRooms()));
   }

}
