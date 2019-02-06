# Fulib Class Models and Code Generation

Fulib is just a library that provides code generation for UML like
models and some model management functionalities. Fulib does not
provide an editor for class diagrams. Instead Fulib uses an inline
domain specific language to define classes and their properties.
As Fulib has no (grafical) class diagram editor, Fulib names its
meta model for classes the _Class Model_.

To create a class model Fulib provides a dedicated API within its
_ClassModelBuilder_ class.


## ClassModelBuilder

The _ClassModelBuilder_ offers the entry point for the construction of
class models. A ClassModelBuilder is constructed via the Fulib class:

<!-- insert_code_fragment: testClassModel4FulibDocu.classModelBuilder -->
         ClassModelBuilder mb = Fulib.classModelBuilder("de.uks.studyright");
<!-- end_code_fragment: -->

The construction of a ClassModelBuilder requires a package name. All
classes created by this ClassModelBuilder will belong to this package.

By default, Fulib generates code within the folder src/main/java/... .
Optional, we may provide the target directory as a second parameter:

<!-- insert_code_fragment: testClassModel4FulibDocu.classModelBuilderSrc -->
         ClassModelBuilder mb = Fulib.classModelBuilder("de.uks.studyright", "src");
<!-- end_code_fragment: -->

By default Fulib uses the java.util.ArrayList collection to implement to-many associations.
You may change this default as follows:

<!-- insert_code_fragment: testClassModel4FulibDocu.classModelBuildersetDefaultCollectionClass -->
         ClassModelBuilder mb = Fulib.classModelBuilder("de.uks.studyright", "src")
               .setDefaultCollectionClass(LinkedHashSet.class);
<!-- end_code_fragment: -->

By default Fulib generates Java code following the Java Beans style for set and get methods.
If your GUI uses JavaFX, you may switch to JavaFX Properties style:

<!-- insert_code_fragment: testClassModel4FulibDocu.classModelBuildersetJavaFXPropertyStyle -->
         ClassModelBuilder mb = Fulib.classModelBuilder("de.uks.studyright", "src")
               .setJavaFXPropertyStyle();
<!-- end_code_fragment: -->


## ClassBuilder

Calling _buildClass_ on our class model builder creates a class in our class model and
returns a _ClassBuilder_. The ClassBuilder is used to add further properties to our class.

<!-- insert_code_fragment: testClassModel4FulibDocu.buildClass -->
         ClassBuilder studentBuilder = mb.buildClass("Student");
<!-- end_code_fragment: -->

The ClassBuilder mainly provides method _buildAttribute_ to add attributes to classes. One
has to provide a name for the attribute and its type. For attribute types the ClassModelBuilder
provides a set of predefined constants. Although its bad style, it is convenient to access these
static constants via variable mb.

Attributes shall have only simple types
like numbers or strings. Actually, the attribute type is passed as a string that is copied
into the generated code. Thus, you may use your own attribute type string as in the last code
line below.

<!-- insert_code_fragment: testClassModel4FulibDocu.buildAttribute -->
         ClassBuilder studentBuilder = mb.buildClass("Student")
               .buildAttribute("name", mb.STRING)
               .buildAttribute("studentId", mb.INT)
               .buildAttribute("scoreCard", "int[]");
<!-- end_code_fragment: -->

The ClassBuilder also allows to switch to the JavaFX code style (just for this class):


<!-- insert_code_fragment: testClassModel4FulibDocu.studentBuilder.setJavaFXPropertyStyle -->
         studentBuilder.setJavaFXPropertyStyle();
<!-- end_code_fragment: -->


## AssociationBuilder

To build an association we need two class builders. Then we call _buildAssociation_ on one class
builder and pass the other class builder as first parameter. Now we have to provide the role names
and cardinalities for both directions. In our example, _location_ will become an attribute of type _Room_
within class _Student_.

<!-- insert_code_fragment: testClassModel4FulibDocu.buildAssociation -->
         ClassBuilder studentBuilder = mb.buildClass("Student");
         ClassBuilder roomBuilder = mb.buildClass("Room");

         studentBuilder.buildAssociation(roomBuilder, "location", mb.ONE, "students", mb.MANY);
<!-- end_code_fragment: -->

Fulib provides to-one and to-many associations. There are no minimal numbers for neighbors nor
maximal numbers for to-many associations. The ClassModelBuilder provides the constants _mb.ONE_ and
_mb.MANY_.

Fulib allows for so-called self-associations, i.e. an association with the same class at both ends.
As a special case Fulib also allows self association with the same name in both directions:

<!-- insert_code_fragment: testClassModel4FulibDocu.buildSelfAssociation -->
         studentBuilder.buildAssociation(studentBuilder, "friends", mb.MANY, "friends", mb.MANY);
<!-- end_code_fragment: -->

As a result class _Student_ will have exactly one attribute with name _friends_. Still it is a
bidirectional association, i.e. if you call albert.withFriends(karli) karli will be added to the
friends of albert and in addition albert will be added to the friends of karli.

Fulib also allows for uni-directional associations, just use null as second roll name:

<!-- insert_code_fragment: testClassModel4FulibDocu.buildUnidirectionalAssociation -->
            studentBuilder.buildAssociation(roomBuilder, "nextGoal", mb.ONE, null, 0);
<!-- end_code_fragment: -->

Fulib supports aggregations:

<!-- insert_code_fragment: testClassModel4FulibDocu.setAggregation -->
         ClassBuilder universityBuilder = mb.buildClass("University");
         ClassBuilder roomBuilder = mb.buildClass("Room");

         universityBuilder.buildAssociation(roomBuilder, "rooms", mb.MANY, "uni", mb.ONE)
            .setAggregation();
<!-- end_code_fragment: -->

If the parent of an aggragation is removed, its parts are removed, too:

<!-- insert_code_fragment: testClassModel4FulibDocu.removeYou -->
   public void removeYou()
   {
      new java.util.ArrayList< >(this.getRooms()).forEach(x -> x.removeYou());

   }
<!-- end_code_fragment: -->



BuildAssociation also allows to choose individual collections for the implementation of
to-many associations:

<!-- insert_code_fragment: testClassModel4FulibDocu.setSourceRoleCollection -->
         studentBuilder.buildAssociation(roomBuilder, "visited", mb.MANY, "visitors", mb.MANY)
            .setSourceRoleCollection(LinkedHashSet.class)
            .setTargetRoleCollection(TreeSet.class);
<!-- end_code_fragment: -->

BuildAssociation also allows for individual associations to use JavaFXStyle:

<!-- insert_code_fragment: testClassModel4FulibDocu.buildAssociation.JavaFXStyle -->
         studentBuilder.buildAssociation(roomBuilder, "location", mb.ONE, "students", mb.MANY)
            .setJavaFXPropertyStyle();
<!-- end_code_fragment: -->


## Interfaces and Methods and Editing Generated Code

Fulib does not support interface classes in its class models. Fulib class models are inteded for
modeling data and it never occured to us to use interface classes for that purpose.
If you have a convincing example that needs interface classes for data models, let us know.
Meanwhile, you may add interface classes manually within the target directory of the generated
code. If one of your model classes shall implement a certain interface, just add this within the
generated code. The code generator will recognize your editing and keep it.

Similary, Fulib class models to not support methods. First, we believe that data models
should not have complex algorithm methods within them. Second, there is little code that
we could generate from a method declaration in our class model. It is much more convinient
for you to add the method within the generated classes, directly. The code generator recognizes
methods that have been added to the code manually and keeps this manual code untouched.

You may add imports as you like.

If you add e.g. a System.out.println(); statement to one of the (set or get) methods generated
by Fulib, this change will be overriden on the next run of the code generator. To protect a method
from beeing overridden add a "// no fulib" comment at the end of the method declaration:

<!-- insert_code_fragment: testClassModel4FulibDocu.noFulib -->
    public Student setStudentId(int value) // no fulib
    {
       if (value != this.studentId)
       {
          System.out.println("Hello Fulib!");
          int oldValue = this.studentId;
          this.studentId = value;
          firePropertyChange("studentId", oldValue, value);
       }
       return this;
   }
<!-- end_code_fragment: -->


