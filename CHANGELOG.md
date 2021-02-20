# fulib v1.0.0

# fulib v1.0.1

# fulib v1.0.2

# fulib v1.0.3

# fulib v1.0.4

# fulib v1.0.5

# fulib v1.0.6

# fulib v1.1.0

* Bumped version number.

# fulib v1.2.0

## New Features

+ Added the decorator mechanism. #26
+ Attributes can now be multi-valued. #23
+ Added the typesafe `CollectionType` API for to-n associations and collection attributes.
+ Added the `Bean` property style as the new default and changed the `POJO` property style to omit property change support.
+ `FMethod`s can now have custom modifiers.

## Improvements

* Updated to fulibYaml v1.3.0.
* Improved Javadoc across all public APIs. #13
* Improved the `ClassModelManager` API and DSL. #18 #28
* Improved the `ClassBuilder` API.
* Improved the rules for what is allowed as an identifier.
* A new parser now understands more and newer Java syntax. #17
* Improved merging of existing and new declarations.
* Greatly improved parsing and code generation performance.
* To-n associations no longer generate static `EMPTY_*` fields.
* To-n associations no longer expose the modifiable backing list through the getter. #10
* Adjusted templates to generate cleaner code, including:
  * use of `Objects.equals`
  * early return
  * `this.` for field and method access
  * imports instead of qualified names for commonly used classes
  * no more raw types
  * braces for control statements
  * `with` and `without` methods are now overloaded and typesafe for: single element, varargs elements, element collection #20
  * fields and methods are now grouped. #22
* Improved code generation for table classes:
  * generally cleaner code on par with the changes to regular class generation
  * replaced excessive use of `ArrayList`, `LinkedHashSet` and `LinkedHashMap` with their respective interfaces
  * expand methods now use overloads instead of varargs. #25
* Improved code generation for classes with superclasses:
  * `toString` now prepends the result of `super.toString()` #24
  * `removeYou` now calls `super.removeYou()` #15
* Changed the way replacement of declarations with auto-generated code can be suppressed. #30
* The code generator no longer now aborts when it encounters syntactically malformed Java source files.

## Bugfixes

* Fixed problems related to wrapper classes of uncommon primitive types.
* Generated `removeYou()` methods now works correctly with unidirectional associations. #9
* Fixed declaration parsing in the `FMethod.setDeclaration` and `.setParamsString` methods. #29

## Deprecations

* Deprecated the `TypeScript` parser and generator. #27
* Deprecated the `Parser` class and related classes. #17
* Deprecated many methods across the codebase. See their Javadoc for replacements or migration instructions.

# fulib v1.2.1

## Bugfixes

* All new members now correctly generate leading whitespace. #35 #36
* `toString` methods are no longer generated in classes without suitable attributes. #39 #41
* `removeYou` methods are no longer generated in classes without associations. #40 #42

# fulib v1.2.2

## Improvements

* Improved JavaDocs in `FileFragmentMap`. #50
* Improved the merging of class and attribute declarations from original source and newly generated code. #60
  * Class declarations are now mostly kept intact, only the type in the extends clause is updated.
  * Attribute declarations are now mostly kept intact, only the type and initializer are updated.

## Bugfixes

* The code generator no longer generates duplicate members when an attribute has a generic or annotated types. #43 #59
* The code generator no longer generates duplicate methods with parameters of generic or annotated types. #43 #59
* The `ClassModelManager.haveMethod` method now correctly supports two methods with the same declaration in different classes. #44
* The code generator now treats the types `void` and `boolean` as primitives. #46
* The code generator no longer removes newlines at the end of existing files. #47
* The code generator now correctly determines when classes are empty and removes them if necessary. #49
* The `FMethod.setDeclaration` method no longer ignores varargs `...`. #54 #55
* The Java parser now supports C-style arrays for parameters. #56
* The `FMethod.setDeclaration` method now supports C-style arrays for parameters. #56
* The code generator no longer generates duplicate members when mixing attributes with methods. #58
* The code generator now intelligently merges the original source with new class and attribute declarations again. #60

# fulib v1.2.3

## Improvements

* `FMethods` now support `import(...)` syntax. #62
* The parser now supports `import(...)` syntax in type uses and annotations. #62
* `import(...)` syntax now supports `static` imports via `import(static ...)`. #62

## Bugfixes

* The code generator now correctly determines when PropertyChange members are needed, depending on attributes, associations, potential super classes and the use of `POJO` property style. #21 #63
* The code generator now properly removes PropertyChange members when they are not needed. #63

# fulib v1.3.0

## New Features

+ Attributes and roles can now specify a description and a since version that is copied to their JavaDocs. #32 #64
+ Added `ClassModelManager.haveRole` methods as aliases for `associate`. #61
+ Static fields are now grouped together in newly generated classes. #65
+ Property accessors are now grouped together in newly generated classes. #65

## Improvements

* Updated to fulibYaml v1.4.0.
* Generated table `toString` methods now produce Markdown. #67
* Generated property constants are now fully uppercase. #68
* The `FMethod.setDeclaration` method now throws an `IllegalArgumentException` if the new value has syntax errors. #72

## Bugfixes

* The code generator now correctly indents new members. #69

# fulib v1.4.0

## New Features

+ Added the `ClassModelManager.haveClass(String, Clazz)` method. #76
+ Added the new `ClassModelManager.haveClass(Class)` and `haveNestedClasses(Class)` methods for defining classes with reflection. #77 #78
+ JavaDocs for association methods now contain a `@see` link to the reverse role. #79

## Improvements

* Improved JavaDocs in `ClassModelManager`. #76

# fulib v1.4.1

## Improvements

* `@Link` without an argument or with empty string now creates a unidirectional association. #81
* Improved POJO templates for unidirectional associations. #81
* `ClassModelManager` now properly handles and documents unidirectional associations. #81

## Bugfixes

* The reflective class model builder now properly replaces fully qualified class names with imports. #80
* Fixed JavaFX templates generating invalid code for unidirectional associations. #81
