# Default Attribute Values

Attributes (but not associations) can be initialized with a default value.
This is possible using the `@InitialValue` annotation, which takes a string representing a Java expression.

`src/gen/java/org/fulib/docs/GenModel.java`:

<!-- insert_code_fragment: docs.GenModel.InitialValue | fenced:java -->
```java
@InitialValue("\"P1\"")
String label;

@InitialValue("100")
int score;
```
<!-- end_code_fragment: -->

`src/main/java/org/fulib/docs/Player.java`:

<!-- insert_code_fragment: docs.InitialValue | fenced:java -->
```java
private String label = "P1";
private int score = 100;
```
<!-- end_code_fragment: -->
