# fulib - Fujaba library [![Build Status](https://travis-ci.org/fujaba/fulib.svg?branch=master)](https://travis-ci.org/fujaba/fulib)

Fulib is a Java-code generating library.

First you write code that builds up your class model:

<!-- insert_code_fragment: test4FulibReadme -->
      ClassModelBuilder mb = ClassModelBuilder.get("org.fulib.groupaccount", "src/main/java");
      ClassBuilder university = mb.buildClass("University").buildAttribute("name", mb.STRING);
      ClassBuilder student = mb.buildClass("Student").buildAttribute("studentId", mb.STRING);
      university.buildAssociation(student, "students", mb.MANY, "uni", mb.ONE);

      ClassModel model = mb.getClassModel();
<!-- end_code_fragment: -->

Now you may generate Java code that implements the modeled classes:

<!-- insert_code_fragment: test4FulibReadme.generate -->
      Generator.generate(model);
<!-- end_code_fragment: -->

Once your IDE has compiled the generated code you may use it:

      University studyRight = new University().setName("Study Right");
      Student alice = new Student().setStudentId("a424242");
      Student   bob = new Student().setStudentId("b232323");
      studyRight.withStudents(alice, bob);
