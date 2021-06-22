package org.fulib.docs.dtos.model;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

// start_code_fragment: docs.dtos.Address
public class Address
{
   public static final String PROPERTY_ID = "id";
   public static final String PROPERTY_CITY = "city";
   public static final String PROPERTY_STREET = "street";
   public static final String PROPERTY_USER = "user";
   private String id;
   private String city;
   private String street;
   private User user;
   protected PropertyChangeSupport listeners;

   public String getId()
   {
      return this.id;
   }

   public Address setId(String value)
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

   public String getCity()
   {
      return this.city;
   }

   public Address setCity(String value)
   {
      if (Objects.equals(value, this.city))
      {
         return this;
      }

      final String oldValue = this.city;
      this.city = value;
      this.firePropertyChange(PROPERTY_CITY, oldValue, value);
      return this;
   }

   public String getStreet()
   {
      return this.street;
   }

   public Address setStreet(String value)
   {
      if (Objects.equals(value, this.street))
      {
         return this;
      }

      final String oldValue = this.street;
      this.street = value;
      this.firePropertyChange(PROPERTY_STREET, oldValue, value);
      return this;
   }

   public User getUser()
   {
      return this.user;
   }

   public Address setUser(User value)
   {
      if (this.user == value)
      {
         return this;
      }

      final User oldValue = this.user;
      if (this.user != null)
      {
         this.user = null;
         oldValue.setAddress(null);
      }
      this.user = value;
      if (value != null)
      {
         value.setAddress(this);
      }
      this.firePropertyChange(PROPERTY_USER, oldValue, value);
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
      result.append(' ').append(this.getCity());
      result.append(' ').append(this.getStreet());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setUser(null);
   }
}
// end_code_fragment:
