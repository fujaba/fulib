# Methods

Usually, you can freely add methods within generated code.
In some situations you may wish to let fulib generate methods for you.
This is possible using the method `ClassModelManager.haveMethod` within the `GenModel.decorate` body.
Here's an example:

`src/gen/java/org/fulib/docs/GenGreeter.java`:

<!-- insert_code_fragment: docs.GenGreeter | fenced:java -->
```java
public class GenGreeter implements ClassModelDecorator
{
   class Greeter
   {
      String name;
   }

   @Override
   public void decorate(ClassModelManager m)
   {
      m.getClassModel().setDefaultPropertyStyle(Type.POJO);

      Clazz greeter = m.haveClass(Greeter.class);
      m.haveMethod(greeter,
         /* declaration: */ "public void greet(String other)",
         /* body: */ "System.out.println(\"Hello \" + other + \"! This is \" + this.getName());");
   }
}
```
<!-- end_code_fragment: -->

`src/main/java/org/fulib/docs/Greeter.java`:

<!-- insert_code_fragment: docs.Greeter | fenced:java -->
```java
public class Greeter
{
   public static final String PROPERTY_NAME = "name";
   private String name;

   public String getName()
   {
      return this.name;
   }

   public Greeter setName(String value)
   {
      this.name = value;
      return this;
   }

   public void greet(String other)
   {
      System.out.println("Hello " + other + "! This is " + this.getName());
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      return result.substring(1);
   }
}
```
<!-- end_code_fragment: -->

`haveMethod` takes a Java method declaration, which may include annotations, modifiers, generics, return type and parameters.
The body is automatically generated with braces, indentation and a trailing newline.
Keep in mind that there are no checks for correctness of your code during definition.
It is possible to generate invalid Java code this way, but any errors will be caught when you compile the generated code.
