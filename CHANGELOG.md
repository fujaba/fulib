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

## Bugfixes

* Fixed problems related to wrapper classes of uncommon primitive types.
* Generated `removeYou()` methods now works correctly with unidirectional associations. #9
* Fixed declaration parsing in the `FMethod.setDeclaration` and `.setParamsString` methods. #29

## Deprecations

* Deprecated the `TypeScript` parser and generator. #27
* Deprecated the `Parser` class and related classes. #17
* Deprecated many methods across the codebase. See their Javadoc for replacements or migration instructions.
