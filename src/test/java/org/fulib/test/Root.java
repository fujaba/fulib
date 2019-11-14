package org.fulib.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class Root
{
   public static final java.util.ArrayList<Integer> EMPTY_resultList = new java.util.ArrayList<Integer>()
   { @Override public boolean add(Integer value){ throw new UnsupportedOperationException("No direct add! Use xy.withStaff(obj)"); }};

   public static final String PROPERTY_staff = "resultList";

   private ArrayList<Integer> resultList;

   public java.util.ArrayList<Integer> getResultList()
   {
      if (this.resultList == null)
      {
         return EMPTY_resultList;
      }

      return this.resultList;
   }

   @Test
   public void testIntListAccess()
   {
      this.withResultList(23)
            .withResultList(23, 42);
      Assertions.assertTrue(this.getResultList().contains(23));
      Assertions.assertEquals(this.getResultList().size(), 3);
   }

   public Root withResultList(Object... value)
   {
      if(value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withResultList(i);
            }
         }
         else if (item instanceof Integer)
         {
            if (this.resultList == null)
            {
               this.resultList = new java.util.ArrayList<Integer>();
            }
            this.resultList.add((Integer) item);
            firePropertyChange("resultList", null, item);
         }
         else throw new IllegalArgumentException();
      }
      return this;
   }


   public Root withoutResultList(Object... value)
   {
      if (this.resultList == null || value==null) return this;
      for (Object item : value)
      {
         if (item == null) continue;
         if (item instanceof java.util.Collection)
         {
            for (Object i : (java.util.Collection) item)
            {
               this.withoutResultList(i);
            }
         }
         else if (item instanceof Integer)
         {
            if (this.resultList.contains(item))
            {
               this.resultList.remove((Integer) item);
               firePropertyChange("resultList", item, null);
            }
         }
      }
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

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (listeners == null)
      {
         listeners = new PropertyChangeSupport(this);
      }
      listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (listeners != null)
      {
         listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }
}
