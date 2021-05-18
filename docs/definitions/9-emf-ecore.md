# EMF and Ecore files

Fulib can import Eclipse Modeling Framework / `.ecore` files.
By putting the `.ecore` file into `src/gen/resource`, you can load it with a `ClassModelManager`:

`src/gen/java/org/fulib/docs/GenModel.java`:

```java
cmm.haveEcore(getClass().getResource("example.ecore").toString());
```

Fulib can parse the following components from EMF:

- Classes (`eClassifiers`), including:
   - Superclass (`eSuperTypes`).
- Attributes (`eAttribute`), including:
   - Cardinality (`upperBound`), which maps to collection attributes.
- Associations (`eReference`), including:
   - Containment (`containment`), the reverse role will be named `parent`.
   - Unidirectional (no `eOpposite`).
   - Bidirectional (`eOpposite`).

All declarations are merged with the existing class model, allowing you to mix fulibScenarios, GenModel and EMF freely.
