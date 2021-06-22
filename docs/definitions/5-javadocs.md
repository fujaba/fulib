# JavaDocs

Fulib can be configured for documenting your model, specifically for generating JavaDocs, using two annotations:

* `@Description` takes a description of your attribute or association which will be inserted in the JavaDocs of getters and setters.
* `@Since` allows you to specify in which version of the software the attribute or assocation was introduced.

Note that `@Since` only has an effect if `@Description` is also present.

`src/gen/java/org/fulib/docs/GenModel.java`:

<!-- insert_code_fragment: docs.GenModel.Description | fenced:java -->
```java
@Description("the full name including first, middle and last names")
String fullName;

@Description("the height in meters")
@Since("1.2")
double height;
```
<!-- end_code_fragment: -->

`src/main/java/org/fulib/docs/Person.java`:

<!-- insert_code_fragment: docs.Description | fenced:java -->
```java
public static final String PROPERTY_FULL_NAME = "fullName";
/** @since 1.2 */
public static final String PROPERTY_HEIGHT = "height";

private String fullName;
private double height;

/**
 * @return the full name including first, middle and last names
 */
public String getFullName()
{
   return this.fullName;
}

/**
 * @param value
 *    the full name including first, middle and last names
 *
 * @return this
 */
public Example setFullName(String value)
{
   this.fullName = value;
   return this;
}

/**
 * @return the height in meters
 *
 * @since 1.2
 */
public double getHeight()
{
   return this.height;
}

/**
 * @param value
 *    the height in meters
 *
 * @return this
 *
 * @since 1.2
 */
public Example setHeight(double value)
{
   this.height = value;
   return this;
}
```
<!-- end_code_fragment: -->
