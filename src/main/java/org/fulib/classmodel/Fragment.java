package org.fulib.classmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/** @since 1.2 */
public class Fragment
{
   // =============== Constants ===============

   public static final String PROPERTY_key = "key";
   public static final String PROPERTY_parent = "parent";

   /** @since 1.4 */
   public static final String PROPERTY_KEY = "key";
   /** @since 1.4 */
   public static final String PROPERTY_PARENT = "parent";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private String key;
   private CompoundFragment parent;

   // =============== Properties ===============

   public String getKey()
   {
      return this.key;
   }

   public Fragment setKey(String value)
   {
      if (Objects.equals(value, this.key))
      {
         return this;
      }

      final String oldValue = this.key;
      this.key = value;
      this.firePropertyChange(PROPERTY_KEY, oldValue, value);
      return this;
   }

   public CompoundFragment getParent()
   {
      return this.parent;
   }

   public Fragment setParent(CompoundFragment value)
   {
      if (this.parent == value)
      {
         return this;
      }

      final CompoundFragment oldValue = this.parent;
      if (this.parent != null)
      {
         this.parent = null;
         oldValue.withoutChildren(this);
      }
      this.parent = value;
      if (value != null)
      {
         value.withChildren(this);
      }
      this.firePropertyChange(PROPERTY_PARENT, oldValue, value);
      return this;
   }

   // =============== Methods ===============

   public void write(Writer writer) throws IOException
   {
      throw new AbstractMethodError();
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

   public void removeYou()
   {
      this.setParent(null);
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getKey());
      return result.substring(1);
   }
}
