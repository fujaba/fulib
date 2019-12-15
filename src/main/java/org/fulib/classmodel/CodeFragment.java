package org.fulib.classmodel;

import java.util.ArrayList;

import java.beans.PropertyChangeSupport;

import java.beans.PropertyChangeListener;
import java.util.Objects;

public class CodeFragment  
{

   protected PropertyChangeSupport listeners = null;

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

   public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   public void removeYou()
   {
   }

   public static final String PROPERTY_key = "key";

   private String key;

   public String getKey()
   {
      return this.key;
   }

   public CodeFragment setKey(String value)
   {
      if (Objects.equals(value, this.key))
      {
         return this;
      }

      final String oldValue = this.key;
      this.key = value;
      this.firePropertyChange("key", oldValue, value);
      return this;
   }

   public static final String PROPERTY_text = "text";

   private String text;

   public String getText()
   {
      return this.text;
   }

   public CodeFragment setText(String value)
   {
      if (Objects.equals(value, this.text))
      {
         return this;
      }

      final String oldValue = this.text;
      this.text = value;
      this.firePropertyChange("text", oldValue, value);
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();

      result.append(' ').append(this.getKey());
      result.append(' ').append(this.getText());


      return result.substring(1);
   }

}