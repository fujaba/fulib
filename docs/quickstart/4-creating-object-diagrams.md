# Creating Object Diagrams

To create an object diagram from your object structure, add this line:

`src/test/java/de/uniks/studyright/Test.java`:

<!-- insert_code_fragment: test.UniversityObjectDiagram | fenced:java -->
```java
FulibTools.objectDiagrams().dumpPng("doc/images/studyRightObjects.png", studyRight);
```
<!-- end_code_fragment: -->

This requires adding [fulibTools](https://github.com/fujaba/fulibTools) as a test dependency.
