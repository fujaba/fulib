package org.fulib.classmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public class CodeFragment extends Fragment
{
   // =============== Constants ===============

   /** @deprecated since 1.2; use {@link Fragment#PROPERTY_key} instead */
   @Deprecated
   public static final String PROPERTY_key = "key";
   public static final String PROPERTY_text = "text";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private String text;

   // =============== Properties ===============

   @Override
   public CodeFragment setKey(String value)
   {
      super.setKey(value);
      return this;
   }

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
      this.firePropertyChange(PROPERTY_text, oldValue, value);
      return this;
   }

   // =============== Methods ===============

   @Override
   public void write(Writer writer) throws IOException
   {
      writer.write(this.getText());
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
   public void removeYou()
   {
      super.removeYou();
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getText());
      return result.toString();
   }
}
