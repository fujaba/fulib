# DTOs

Fulib makes it easy to generate DTOs (Data Transfer Objects) from existing model classes without a lot of duplication.
We recommend putting all DTOs into a package and `GenDtos` class that are separate from your model.
Here's an example:

`src/gen/java/org/example/dtos/model/GenModel`:

<!-- insert_code_fragment: docs.dtos.GenModel | fenced:java -->
```java
public class GenModel implements ClassModelDecorator
{
   public class User
   {
      String id;
      String name;
      @Link("user")
      Address address;
   }

   public class Address
   {
      String id;
      String city;
      String street;
      @Link("address")
      User user;
   }

   @Override
   public void decorate(ClassModelManager m)
   {
      m.haveNestedClasses(GenModel.class);
   }
}
```
<!-- end_code_fragment: -->

`src/gen/java/org/example/dtos/dto/GenDtos`:

<!-- insert_code_fragment: docs.dtos.GenDtos | fenced:java -->
```java
public class GenDtos implements ClassModelDecorator
{
   @DTO(model = GenModel.User.class, omit = { "id" })
   class UserDto
   {}

   @DTO(model = GenModel.Address.class, pick = { "city", "street" })
   class AddressDto
   {}

   @Override
   public void decorate(ClassModelManager m)
   {
      // This omits PropertyChangeListeners etc. from the generated code
      m.getClassModel().setDefaultPropertyStyle(Type.POJO);
      m.haveNestedClasses(GenDtos.class);
   }
}
```
<!-- end_code_fragment: -->

The example shows the basic usage of the `@DTO` annotation.

* `model` sets the model class we want to copy fields from.
* `omit` is optional and specifies which fields should **not** be copied to the DTO class.
* `pick` is optional and specifies which fields **should** be copied to the DTO class. If `pick` is provided, all other fields will be ignored.

Let's take a look at the generated code:

<details>
   <summary>
      <code>src/main/java/org/example/dtos/dto/UserDto.java</code>
   </summary>
   <!-- insert_code_fragment: docs.dtos.UserDto | fenced:java -->
   ```java
   public class UserDto
   {
      public static final String PROPERTY_NAME = "name";
      public static final String PROPERTY_ADDRESS = "address";
      private String name;
      private String address;
   
      public String getName()
      {
         return this.name;
      }
   
      public UserDto setName(String value)
      {
         this.name = value;
         return this;
      }
   
      public String getAddress()
      {
         return this.address;
      }
   
      public UserDto setAddress(String value)
      {
         this.address = value;
         return this;
      }
   
      @Override
      public String toString()
      {
         final StringBuilder result = new StringBuilder();
         result.append(' ').append(this.getName());
         result.append(' ').append(this.getAddress());
         return result.substring(1);
      }
   }
   ```
   <!-- end_code_fragment: -->
</details>

<details>
   <summary>
      <code>src/main/java/org/example/dtos/dto/AddressDto.java</code>
   </summary>

   <!-- insert_code_fragment: docs.dtos.AddressDto | fenced:java -->
   ```java
   public class AddressDto
   {
      public static final String PROPERTY_CITY = "city";
      public static final String PROPERTY_STREET = "street";
      private String city;
      private String street;
   
      public String getCity()
      {
         return this.city;
      }
   
      public AddressDto setCity(String value)
      {
         this.city = value;
         return this;
      }
   
      public String getStreet()
      {
         return this.street;
      }
   
      public AddressDto setStreet(String value)
      {
         this.street = value;
         return this;
      }
   
      @Override
      public String toString()
      {
         final StringBuilder result = new StringBuilder();
         result.append(' ').append(this.getCity());
         result.append(' ').append(this.getStreet());
         return result.substring(1);
      }
   }
   ```
   <!-- end_code_fragment: -->
</details>

In the `UserDto` class, we can see that all attributes from `User` were copied over except for `id`, because it was specified in `omit`.
The `address` association in `User` was converted to a `String` field, which can hold the Address ID in our DTO.

The `AddressDto` class contains only the `city` and `street` attributes, thanks to the `pick` array.

For completeness, here is the generated code for the `User` and `Address` model classes.
Note how these use the default `Bean` property style, which generates PropertyChange support.

<details>
   <summary>
      <code>src/main/java/org/example/dtos/model/User.java</code>
   </summary>

   <!-- insert_code_fragment: docs.dtos.User | fenced:java -->
   ```java
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
   ```
   <!-- end_code_fragment: -->
</details>

<details>
   <summary>
      <code>src/main/java/org/example/dtos/model/Address.java</code>
   </summary>

   <!-- insert_code_fragment: docs.dtos.Address | fenced:java -->
   ```java
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
   ```
   <!-- end_code_fragment: -->
</details>
