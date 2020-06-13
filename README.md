# fulib - Fujaba library

[![Build Status](https://travis-ci.org/fujaba/fulib.svg?branch=master)](https://travis-ci.org/fujaba/fulib)
[![Download](https://api.bintray.com/packages/fujaba/maven/fulib/images/download.svg)](https://bintray.com/fujaba/maven/fulib/_latestVersion "Download")

Fulib is a Java-code generating library.

## Installation

`build.gradle`:

```groovy
plugins {
    id 'org.fulib.fulibGradle' version '0.4.0'
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    // https://mvnrepository.com/artifact/org.fulib/fulibScenarios
    fulibScenarios group: 'org.fulib', name: 'fulibScenarios', version: '1.3.0'
}
```

## Usage

In the following tutorial, we build a class model for a university.
It uses the package name `de.uniks.studyright`, which you can replace according to your needs.

Create a class `GenModel` in the `de.uniks.studyright` package and **put it in the `src/gen/java` source directory**.
The name `GenModel` is only a convention, you can also use a different one.

<!-- insert_code_fragment: test.GenModel | fenced -->
<!-- end_code_fragment: -->

Now, run `gradle generateScenarioSource`.
This will run the code you put in the `GenModel.decorate` method and generate all classes you described.
You can check out the results in the `de.uniks.studyright` package in the `src/main/java` source directory.

Rendered as a class diagram this model looks like this:

![University class diagram](test/src/main/java/de/uniks/studyright/classDiagram.png)

Now you can use the generated classes from your code (in `src/main/java` and `src/test/java`).
Here's an example for our university model:

<!-- insert_code_fragment: test.UniversityModelUsage -->
<!-- end_code_fragment: -->

This creates the object structure shown in the object diagram below.

![simple object diagram](test/doc/images/studyRightObjects.png)

To create an object diagram from your object structure, add this line:

<!-- insert_code_fragment: test.UniversityObjectDiagram -->
      FulibTools.objectDiagrams().dumpPng("doc/images/studyRightObjects.png", studyRight);
<!-- end_code_fragment: -->

This requires adding [fulibTools](https://github.com/fujaba/fulibTools) as a dependency.

---

For more details on class models and code generation see: [Fulib Class Models](doc/FulibClassModels.md)

Fulib also provides means for model queries and model transformations, see:
[Fulib Tables](doc/FulibTables.md)
