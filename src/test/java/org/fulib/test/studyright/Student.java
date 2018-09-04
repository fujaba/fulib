package org.fulib.test.studyright;

import java.beans.PropertyChangeSupport;

public class Student
{

   protected PropertyChangeSupport listeners = null;

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (listeners != null)
      {
         listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   private String name = "Karli";

   public String getName()
   {
      return name;
   }

   public Student setName(String value)
   {
      this.name = value;
      return this;
   }


}