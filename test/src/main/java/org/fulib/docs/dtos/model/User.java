package org.fulib.docs.dtos.model;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

// start_code_fragment: docs.dtos.User
public class User
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_ADDRESS = "address";
   private String id;
   private String name;
   private Address address;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public User setId(String value)
   {
      if (Objects.equals(value, this.id))
      {
         return this;
      }

      final String oldValue = this.id;
      this.id = value;
      this.firePropertyChange(PROPERTY_ID, oldValue, value);
      return this;
   }

   public String getName()
   {
      return this.name;
   }

   public User setName(String value)
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

   public Address getAddress()
   {
      return this.address;
   }

   public User setAddress(Address value)
   {
      if (this.address == value)
      {
         return this;
      }

      final Address oldValue = this.address;
      if (this.address != null)
      {
         this.address = null;
         oldValue.setUser(null);
      }
      this.address = value;
      if (value != null)
      {
         value.setUser(this);
      }
      this.firePropertyChange(PROPERTY_ADDRESS, oldValue, value);
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

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getId());
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setAddress(null);
   }
}
// end_code_fragment:
