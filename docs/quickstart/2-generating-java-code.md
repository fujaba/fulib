# Generating Java Code

Once you defined your model, run `gradle generateScenarioSource`.
This will run the code you put in the `GenModel.decorate` method and generate all classes you described.
You can check out the results in the `de.uniks.studyright` package in the `src/main/java` source directory.

Rendered as a class diagram this model looks like this:

![University class diagram](../../test/src/main/java/de/uniks/studyright/classDiagram.png)
