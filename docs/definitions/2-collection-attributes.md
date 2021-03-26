# Collection Attributes

Attributes with collection types, i.e. those extending `Collection`, are treated in a special way by fulib.
Instead of a setter, those will generate `with` and `without` methods.
The getter will make sure that the collection cannot be modified from the outside.

<!-- insert_code_fragment: docs.GenModel.Page | fenced:java -->
```java
class Page
{
   List<String> lines;
}
```
<!-- end_code_fragment: -->

<!-- insert_code_fragment: docs.Page | fenced:java -->
```java
public class Page
{
   public static final String PROPERTY_LINES = "lines";
   private List<String> lines;

   public List<String> getLines()
   {
      return this.lines != null ? Collections.unmodifiableList(this.lines) : Collections.emptyList();
   }

   public Page withLines(String value)
   {
      if (this.lines == null)
      {
         this.lines = new ArrayList<>();
      }
      this.lines.add(value);
      return this;
   }

   public Page withLines(String... value)
   {
      this.withLines(Arrays.asList(value));
      return this;
   }

   public Page withLines(Collection<? extends String> value)
   {
      if (this.lines == null)
      {
         this.lines = new ArrayList<>(value);
      }
      else
      {
         this.lines.addAll(value);
      }
      return this;
   }

   public Page withoutLines(String value)
   {
      this.lines.removeAll(Collections.singleton(value));
      return this;
   }

   public Page withoutLines(String... value)
   {
      this.withoutLines(Arrays.asList(value));
      return this;
   }

   public Page withoutLines(Collection<? extends String> value)
   {
      if (this.lines != null)
      {
         this.lines.removeAll(value);
      }
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getLines());
      return result.substring(1);
   }
}
```
<!-- end_code_fragment: -->

The generated code shows that `ArrayList` was automatically chosen as the implementation class for `List`.
Other collection int, like `Set`, have other default implementation types.
You can also specify the implementation type manually if you don't want to use the default -- fulib will automatically infer the most specific interface.
The following tables shows the mapping from interfaces to default implementations.

| Interface      | Default Implementation |
|----------------|------------------------|
| `Collection`   | `LinkedHashSet`        |
| `Set`          | `LinkedHashSet`        |
| `SortedSet`    | `TreeSet`              |
| `NavigableSet` | `TreeSet`              |
| `List`         | `ArrayList`            |

Attributes can be turned into associations and customized using a number of annotations, which are covered in the following sections.
