
// MIT license

package org.fulib.test.studyright;

import java.lang.reflect.*;

import java.beans.PropertyChangeSupport;

/**
 * Important class
 */
public class University
{
   // important content
   
   private int foundedIn = 2042;

   public int getFoundedIn()
   {
      return foundedIn;
   }

   private String name;

   public String getName()
   {
      return name;
   }

   public University setName(String value)
   {
      this.name = value;
      return this;
   }
   
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

}
