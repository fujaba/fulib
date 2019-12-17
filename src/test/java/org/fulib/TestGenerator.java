package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.Type;
import org.fulib.classmodel.*;
import org.fulib.parser.FragmentMapBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;


class TestGenerator {

    private Class<?> uniClass;
    private URLClassLoader classLoader;
    private Object studyRight;
    private Class<?> assignClass;
    private Class<?> studClass;

    @Test
    public void testAttrList() throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, MalformedURLException
    {
        ClassModelBuilder mb = Fulib.classModelBuilder("org.testAttrList", "tmp/src");
        ClassBuilder root = mb.buildClass("Root");
        root.buildAttribute("resultList", Type.INT + Type.__LIST);
        ClassBuilder kid = mb.buildClass("Kid").setSuperClass(root);
        ClassModel model = mb.getClassModel();
        Fulib.generator().generate(model);

        String outFolder = model.getMainJavaDir() + "/../out";
        int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        // Load and instantiate compiled class.
        File classesDir = new File(outFolder);
        URLClassLoader classLoader;
        // Loading the class
        classLoader = URLClassLoader.newInstance(new URL[]{classesDir.toURI().toURL()});

        Class<?> rootClass = Class.forName(model.getPackageName() + ".Root", true, classLoader);

        Object theRoot = rootClass.newInstance();

        Method withMethod = rootClass.getMethod("withResultList", Object[].class);

        Object answer = withMethod.invoke(theRoot, new Object[]{new Object[]{23}});
        assertThat(answer, equalTo(theRoot));

        Method getMethod = rootClass.getMethod("getResultList");
        ArrayList theList = (ArrayList) getMethod.invoke(theRoot);
        assertThat(theList.size(), equalTo(1));

        withMethod.invoke(theRoot, new Object[]{new Object[]{42}});
        withMethod.invoke(theRoot, new Object[]{new Object[]{23}});
        assertThat(theList.size(), equalTo(3));

        Method withoutMethod = rootClass.getMethod("withoutResultList", Object[].class);
        withoutMethod.invoke(theRoot, new Object[]{new Object[]{23}});
        assertThat(theList.size(), equalTo(2));

        // change to simple attribute
        Clazz modelRoot = model.getClazz("Root");
        Attribute modelResultList = modelRoot.getAttribute("resultList");
        modelResultList.setType(Type.INT);

        Fulib.generator().generate(model);

        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        modelResultList.setType(Type.INT + Type.__LIST);

        Fulib.generator().generate(model);

        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        System.out.println();
    }


    @Test
    public void testForbiddenClasses()
    {
        ClassModelBuilder mb = new ClassModelBuilder("org.testFulib");
        genClass(mb, "Object");
        genClass(mb, "String");
        genClass(mb, "Integer");
    }

    private void genClass(ClassModelBuilder mb, String className)
    {
        try {
            ClassBuilder objectClass = mb.buildClass(className);
            fail();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    @Test
    public void testFMethods() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException
    {
        String targetFolder = "tmp";
        String packageName = "party.partyApp";

        Tools.removeDirAndFiles(targetFolder);

        ClassModelManager mm = new ClassModelManager()
              .havePackageName(packageName)
              .haveMainJavaDir(targetFolder);

        Clazz party = mm.haveClass("Party");
        // mm.haveAttribute(party, "name", "String");

        FMethod method = mm.haveMethod(party, "public void hello()", "      System.out.println(\"World!\");\n");

        ClassModel model = mm.getClassModel();
        Fulib.generator().generate(model);

        String outFolder = model.getMainJavaDir() + "/../out";
        int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        // does removal work?
        party.withoutMethods(method);
        Fulib.generator().generate(model);
        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        method = mm.haveMethod(party, "public int theAnswer()", "      return 42;\n");
        Fulib.generator().generate(model);
        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        // add a parameter
        method.readParams().put("question", "int");
        method.setMethodBody("      return question * 2;\n");

        Fulib.generator().generate(model);
        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));



        party.getImportList().add("import org.junit.jupiter.api.Test;");
        party.getImportList().add("import static org.hamcrest.CoreMatchers.*;");
        party.getImportList().add("import static org.hamcrest.MatcherAssert.assertThat;");

        FMethod testMethod = mm.haveMethod(party, "" +
              "@Test\n" +
              "public void testQuestion()", "" +
              "      assertThat(theAnswer(21), equalTo(42));\n");

        Fulib.generator().generate(model);

        mm.haveMethod(party, "public int theAnswer()", "      return 42;\n");
        Fulib.generator().generate(model);

        Fulib.generator().generate(model);

        Fulib.generator().generate(model);


        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        // Load and instantiate compiled class.
        File classesDir = new File(outFolder);
        URLClassLoader classLoader;
        // Loading the class
        classLoader = URLClassLoader.newInstance(new URL[]{classesDir.toURI().toURL()});

        Class<?> partyClass = Class.forName(model.getPackageName() + ".Party", true, classLoader);

        Object theParty = partyClass.newInstance();

        Method answerMethod = partyClass.getMethod("theAnswer", int.class);

        Object answer = answerMethod.invoke(theParty, 23);
        assertThat(answer, equalTo(46));

        Method secondMethod = partyClass.getMethod("testQuestion");
        secondMethod.invoke(theParty);

    }



    @Test
    void testAttributeGenerator() throws Exception {
        String targetFolder = "tmp";
        String packageName = "org.fulib.test.studyright";

        Tools.removeDirAndFiles(targetFolder);

        ClassModel model = getClassModelUniStudWithAttributes(targetFolder, packageName);

        createPreexistingUniFile(packageName, model);

        String uniFileName = model.getPackageSrcFolder() + "/University.java";
        assertThat("University.java exists", Files.exists(Paths.get(uniFileName)));

        String outFolder = model.getMainJavaDir() + "/../out";
        int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        Fulib.generator().generate(model);

        assertThat("University.java exists", Files.exists(Paths.get(uniFileName)));

        String studFileName = model.getPackageSrcFolder() + "/Student.java";
        assertThat("Student.java exists", Files.exists(Paths.get(studFileName)));

        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        runAttributeReadWriteTests(outFolder, model);
    }

    @Test
    void testAssociationGenerator() throws Exception {
        String targetFolder = "tmp";
        String packageName = "org.fulib.test.studyright";

        Tools.removeDirAndFiles(targetFolder);

        ClassModel model = getClassModelWithAssociations(targetFolder, packageName);

        createPreexistingUniFile(packageName, model);

        String uniFileName = model.getPackageSrcFolder() + "/University.java";
        assertThat("University.java exists", Files.exists(Paths.get(uniFileName)));

        String outFolder = model.getMainJavaDir() + "/../out";
        int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        Fulib.generator().generate(model);

        assertThat("University.java exists", Files.exists(Paths.get(uniFileName)));

        String studFileName = model.getPackageSrcFolder() + "/Student.java";
        assertThat("Student.java exists", Files.exists(Paths.get(studFileName)));

        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        runAssociationReadWriteTests(outFolder, model);

    }

    @Test
    void testUnidirectionalAssociations() throws Exception {
        String targetFolder = "tmp";
        String packageName = "org.fulib.test.studyright";

        Tools.removeDirAndFiles(targetFolder);

        ClassModelBuilder mb = Fulib.classModelBuilder(packageName, targetFolder);

        ClassBuilder university = mb.buildClass("University");
        ClassBuilder prof = mb.buildClass("Prof");

        university.buildAssociation(prof, "head", Type.ONE, null, 0);
        university.buildAssociation(prof, "staff", Type.MANY, null, 0);

        ClassModel model = mb.getClassModel();
        Fulib.generator().generate(model);

        String outFolder = model.getMainJavaDir() + "/../out";
        int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        ArrayList<AssocRole> clone = (ArrayList<AssocRole>) university.getClazz().getRoles().clone();
        for (AssocRole r : clone) {
            r.setClazz(null);
        }
        clone = (ArrayList<AssocRole>) prof.getClazz().getRoles().clone();
        for (AssocRole r : clone) {
            r.setClazz(null);
        }


        university.buildAssociation(prof, "head", Type.ONE, "uni", Type.ONE);
        university.buildAssociation(prof, "staff", Type.MANY, "employer", Type.ONE);

        Fulib.generator().generate(model);

        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));


        clone = (ArrayList<AssocRole>) university.getClazz().getRoles().clone();
        for (AssocRole r : clone) {
            r.setClazz(null);
        }
        clone = (ArrayList<AssocRole>) prof.getClazz().getRoles().clone();
        for (AssocRole r : clone) {
            r.setClazz(null);
        }

        university.buildAssociation(prof, "head", Type.ONE, null, 0);
        university.buildAssociation(prof, "staff", Type.MANY, null, 0);

        Fulib.generator().generate(model);

        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));
    }

    @Test
    void testExtendsGenerator() throws Exception {
        String targetFolder = "tmp";
        String packageName = "org.fulib.test.studyright";

        Tools.removeDirAndFiles(targetFolder);

        ClassModel model = getClassModelWithExtends(targetFolder, packageName);

        createPreexistingUniFile(packageName, model);

        Fulib.generator().generate(model);

        // add implements clause to TeachingAssistant
        FileFragmentMap fragmentMap = FragmentMapBuilder.parse(model.getPackageSrcFolder() + "/TeachingAssistent.java");
        CodeFragment fragment = fragmentMap.getFragment(FileFragmentMap.CLASS);
        fragment.setText("@Deprecated \npublic class TeachingAssistent extends Student implements java.io.Serializable \n{");
        fragmentMap.writeFile();
        fragmentMap.add(FileFragmentMap.CLASS, "public class TeachingAssistent extends Student \n{", 1);
        assertThat(fragment.getText(), containsString("@Deprecated"));
        assertThat(fragment.getText(), containsString("implements java.io.Serializable"));
        assertThat(fragment.getText(), containsString("{"));

        Fulib.generator().generate(model);

        String uniFileName = model.getPackageSrcFolder() + "/University.java";
        assertThat("University.java exists", Files.exists(Paths.get(uniFileName)));

        String studFileName = model.getPackageSrcFolder() + "/Student.java";
        assertThat("Student.java exists", Files.exists(Paths.get(studFileName)));

        String outFolder = model.getMainJavaDir() + "/../out";
        int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));

        runExtendsReadWriteTests(outFolder, model);

    }

    @Test
    void testTables() throws Exception {
        String targetFolder = "tmp";

        Tools.removeDirAndFiles(targetFolder);

        ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright", "tmp/src");

        ClassBuilder uni = mb.buildClass("University")
                .buildAttribute("name", Type.STRING);

        ClassBuilder student = mb.buildClass("Student")
                .buildAttribute("name", Type.STRING)
                .buildAttribute("studentId", Type.STRING)
                .buildAttribute("credits", Type.INT);

        student.buildAssociation(student, "friends", Type.MANY, "friends", Type.MANY);
        uni.buildAssociation(student, "students", Type.MANY, "uni", Type.ONE);

        ClassBuilder ta = mb.buildClass("Tutor").setSuperClass(student);

        ClassBuilder room = mb.buildClass("Room")
                .buildAttribute("roomNo", Type.STRING)
                .buildAttribute("topic", Type.STRING);

        uni.buildAssociation(room, "rooms", Type.MANY, "uni", Type.ONE);
        student.buildAssociation(room, "in", Type.ONE, "students", Type.MANY);

        ClassBuilder assignment = mb.buildClass("Assignment")
                .buildAttribute("topic", Type.STRING)
                .buildAttribute("points", Type.INT);

        room.buildAssociation(assignment, "assignments", Type.MANY, "room", Type.ONE);
        student.buildAssociation(assignment, "done", Type.MANY, "students", Type.MANY);

        ClassModel model = mb.getClassModel();

        Fulib.generator().generate(model);

        Fulib.tablesGenerator().generate(model);

        // generate again to test recognition of existing fragments
        Fulib.generator().generate(model);

        Fulib.tablesGenerator().generate(model);

        String uniFileName = model.getPackageSrcFolder() + "/tables/UniversityTable.java";
        assertThat("UniversityTable.java exists", Files.exists(Paths.get(uniFileName)));

        String outFolder = model.getMainJavaDir() + "/../out";
        int returnCode = Tools.javac(outFolder, model.getPackageSrcFolder());
        assertThat("compiler return code: ", returnCode, is(0));
        returnCode = Tools.javac(outFolder, model.getPackageSrcFolder() + "/tables");
        assertThat("compiler return code: ", returnCode, is(0));

        runTableTests(outFolder, model);
    }

    @ParameterizedTest
    @ValueSource (strings = {
            "org.extends.tools", // keyword
            "org.fulib.",
            ".org.fulib",
            "org fulib",
            // "org$fulib", // valid Java identifier
    })
    void testValidPackageNames(String packageName) {
        assertThrows(IllegalArgumentException.class, () -> Fulib.classModelBuilder(packageName));
    }

    @Test
    void testValidClassNames() {

        ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");

        assertThrows(IllegalArgumentException.class, () -> mb.buildClass(null));
        assertThrows(IllegalArgumentException.class, () -> mb.buildClass(""));
    }

    @Test
    void testValidIdentifiers() {

        ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
        ClassBuilder c1 = mb.buildClass("C1");

        assertThrows(IllegalArgumentException.class, () -> mb.buildClass("C1"));

        assertThrows(IllegalArgumentException.class, () -> c1.buildAttribute("42", Type.STRING));

        c1.buildAttribute("a42", Type.STRING);
        assertThrows(IllegalArgumentException.class, () -> c1.buildAttribute("a42", Type.STRING));

        Function<String,Boolean> f;
        c1.buildAttribute("myFunction", "java.util.function.Function<String,Boolean>");

        ClassBuilder c2 = mb.buildClass("C2");
        assertThrows(IllegalArgumentException.class, () -> c1.buildAssociation(c2, "a42", Type.MANY,
                "b", Type.MANY));

        assertThrows(IllegalArgumentException.class, () -> c1.buildAssociation(c1, "x", Type.MANY,
                "x", Type.ONE));

        c1.buildAssociation(c1, "x", Type.MANY, "x", Type.MANY);
    }

    @Test
    void testModelEvolution() throws IOException {
        Tools.removeDirAndFiles("tmp");


        // first simple model
        ClassModelBuilder mb = Fulib.classModelBuilder("org.evolve", "tmp/src");
        ClassBuilder uni = mb.buildClass("University")
                .buildAttribute("uniName", Type.STRING);
        ClassBuilder stud = mb.buildClass("Student")
                .buildAttribute("matNo", Type.STRING)
                .buildAttribute("startYear", Type.INT);
        uni.buildAssociation(stud, "students", Type.MANY, "uni", Type.ONE);
        ClassBuilder room = mb.buildClass("Room")
                .buildAttribute("roomNo", Type.STRING);

        ClassModel firstModel = mb.getClassModel();

        createPreexistingUniFile("org.evolve", firstModel);

        Fulib.generator().generate(firstModel);

        int compileResult = Tools.javac("tmp/out", firstModel.getPackageSrcFolder());
        assertThat(compileResult, equalTo(0));
        assertThat(Files.exists(Paths.get(firstModel.getPackageSrcFolder() + "/University.java")), is(true));

        // rename an attribute
        uni.getClazz().setName("Institute");
        room.getClazz().setName("LectureHall");
        stud.getClazz().getAttribute("matNo").setName("studentId");
        stud.getClazz().getAttribute("startYear").setType(Type.STRING);

        // prepare logger
        Logger logger = Logger.getLogger(Generator.class.getName());
        final ArrayList<LogRecord> logRecordList = new ArrayList<>();
        Handler handler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                logRecordList.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };
        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
        logger.setLevel(Level.INFO);

        Fulib.generator().generate(firstModel);
        assertThat(logRecordList.size(), not(equalTo(0)));

        compileResult = Tools.javac("tmp/out", firstModel.getPackageSrcFolder());
        assertThat(compileResult, equalTo(0));

        // rename a class
        uni.getClazz().setName("Institute");

        // rename an association

    }

    @Test
    void testCustomTemplates() throws IOException {
        Tools.removeDirAndFiles("tmp");

        ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright", "tmp/src");
        mb.buildClass("University").buildAttribute("name", Type.STRING);
        mb.buildClass("Student")
                .buildAttribute("name", Type.STRING, "\"Karli\"")
                .buildAttribute("matrNo", Type.LONG, "0");

        ClassModel model = mb.getClassModel();

        // generate normal
        Fulib.generator().generate(model);

        byte[] bytes = Files.readAllBytes(Paths.get(model.getPackageSrcFolder() + "/Student.java"));
        String content = new String(bytes);
        assertThat(content, not(containsString("/* custom attribute comment */")));

        // generate custom
        // start_code_fragment: testCustomTemplates
        Fulib.generator()
                .setCustomTemplatesFile("templates/custom.stg")
                .generate(model);
        // end_code_fragment:

        bytes = Files.readAllBytes(Paths.get(model.getPackageSrcFolder() + "/Student.java"));
        content = new String(bytes);
        assertThat(content, containsString("/* custom attribute comment */"));
    }

    private void createPreexistingUniFile(String packageName, ClassModel model) throws IOException {
        // create pre existing University class with extra elements
        STGroup group = new STGroupFile("templates/university.stg");
        ST uniTemplate = group.getInstanceOf("university");
        uniTemplate.add("packageName", packageName);
        String uniText = uniTemplate.render();

        Files.createDirectories(Paths.get(model.getPackageSrcFolder()));
        Files.write(Paths.get(model.getPackageSrcFolder() + "/University.java"), uniText.getBytes());
    }

    final ClassModel getClassModelUniStudWithAttributes(ClassModelBuilder mb) {
        mb.buildClass("University").buildAttribute("name", Type.STRING);

        mb.buildClass("Student")
                .buildAttribute("name", Type.STRING, "\"Karli\"")
                .buildAttribute("matrNo", Type.LONG, "0");

        return mb.getClassModel();
    }

    ClassModel getClassModelUniStudWithAttributes(String targetFolder, String packageName) {
        return getClassModelUniStudWithAttributes(Fulib.classModelBuilder(packageName, targetFolder + "/src"));
    }

    final ClassModel getClassModelWithAssociations(String targetFolder, ClassModelBuilder mb) {
        ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
        // end_code_fragment:

        mb.getClassModel().setMainJavaDir(targetFolder + "/src");

        ClassBuilder studi = mb.buildClass("Student")
                .buildAttribute("name", Type.STRING, "\"Karli\"");

        universitiy.buildAssociation(studi, "students", Type.MANY, "uni", Type.ONE);

        ClassBuilder room = mb.buildClass("Room")
                .buildAttribute("no", Type.STRING);

        universitiy.buildAssociation(room, "rooms", Type.MANY, "uni", Type.ONE)
                .setSourceRoleCollection(LinkedHashSet.class)
                .setAggregation();

        studi.buildAssociation(room, "condo", Type.ONE, "owner", Type.ONE);

        studi.buildAssociation(room, "in", Type.MANY, "students", Type.MANY);

        ClassBuilder assignment = mb.buildClass("Assignment").buildAttribute("topic", Type.STRING);
        studi.buildAssociation(assignment, "done", Type.MANY, "students", Type.MANY);

        return mb.getClassModel();
    }

    ClassModel getClassModelWithAssociations(String targetFolder, String packageName) {
        return getClassModelWithAssociations(targetFolder, Fulib.classModelBuilder(packageName, "src/main/java"));
    }

    private ClassModel getClassModelWithExtends(String targetFolder, String packageName) {
        // start_code_fragment: ClassModelBuilder
        ClassModelBuilder mb = Fulib.classModelBuilder(packageName);

        ClassBuilder universitiy = mb.buildClass("University").buildAttribute("name", Type.STRING);
        // end_code_fragment:

        mb.getClassModel().setMainJavaDir(targetFolder + "/src");

        // start_code_fragment: ClassBuilder.buildAttribute_init
        ClassBuilder student = mb.buildClass("Student")
                .buildAttribute("name", Type.STRING, "\"Karli\"");
        // end_code_fragment:

        // start_code_fragment: ClassBuilder.buildAssociation
        universitiy.buildAssociation(student, "students", Type.MANY, "uni", Type.ONE);
        // end_code_fragment:

        ClassBuilder room = mb.buildClass("Room")
                .buildAttribute("no", Type.STRING);

        universitiy.buildAssociation(room, "rooms", Type.MANY, "uni", Type.ONE)
                .setSourceRoleCollection(LinkedHashSet.class);

        student.buildAssociation(room, "condo", Type.ONE, "owner", Type.ONE);

        student.buildAssociation(room, "in", Type.MANY, "students", Type.MANY);

        ClassBuilder ta = mb.buildClass("TeachingAssistent")
                .setSuperClass(student)
                .buildAttribute("level", Type.STRING);

        return mb.getClassModel();
    }

    private void runAttributeReadWriteTests(String outFolder, ClassModel model) throws Exception {
        final ArrayList<PropertyChangeEvent> eventList = new ArrayList<>();

        // run self test
        File classesDir = new File(outFolder);

        // Load and instantiate compiled class.
        URLClassLoader classLoader;
        // Loading the class
        classLoader = URLClassLoader.newInstance(new URL[]{classesDir.toURI().toURL()});

        Class<?> uniClass = Class.forName(model.getPackageName() + ".University", true, classLoader);

        Object studyRight = uniClass.newInstance();

        Method addPropertyChangeListener = uniClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);

        PropertyChangeListener listener = eventList::add;
        addPropertyChangeListener.invoke(studyRight, listener);

        assertThat(studyRight, hasProperty("name", nullValue()));

        Method setName = uniClass.getMethod("setName", String.class);

        Object setNameReturn = setName.invoke(studyRight, "StudyRight");
        assertThat("setName returned this", setNameReturn, is(sameInstance(studyRight)));
        assertThat("got property change", eventList.size() > 0);

        PropertyChangeEvent evt = eventList.get(0);
        assertThat(evt.getPropertyName(), is(equalTo("name")));
        assertThat("event new value", evt.getNewValue(), is(equalTo("StudyRight")));

        // set name with same value again --> no propertyChange
        setName.invoke(studyRight, "StudyRight");
        assertThat("no property change", eventList.size() == 1);
        assertThat(studyRight, hasProperty("name", equalTo("StudyRight")));

        // change name
        setName.invoke(studyRight, "StudyFuture");
        assertThat(studyRight, hasProperty("name", equalTo("StudyFuture")));
        assertThat("got property change", eventList.size() == 2);
        evt = eventList.get(1);
        assertThat("event property", evt.getPropertyName(), is(equalTo("name")));
        assertThat("event new value", evt.getNewValue(), is(equalTo("StudyFuture")));

        // testing int attr
        eventList.clear();

        Class<?> studClass = Class.forName(model.getPackageName() + ".Student", true, classLoader);

        Object karli = studClass.newInstance();
        Method setStudentName = studClass.getMethod("setName", String.class);
        setStudentName.invoke(karli, "Karli");

        Method setMatrNo = studClass.getMethod("setMatrNo", long.class);
        addPropertyChangeListener = studClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
        addPropertyChangeListener.invoke(karli, listener);

        assertThat(karli, hasProperty("matrNo", equalTo(0L)));

        Object setMatrNoReturn = setMatrNo.invoke(karli, 42);

        assertThat("set method returned this", setMatrNoReturn, is(sameInstance(karli)));
        assertThat(karli, hasProperty("matrNo", equalTo(42L)));
        assertThat("got property change", eventList.size() == 1);
        evt = eventList.get(0);
        assertThat("event property", evt.getPropertyName(), is(equalTo("matrNo")));
        assertThat("event new value", evt.getNewValue(), is(42L));

        setMatrNoReturn = setMatrNo.invoke(karli, 42);

        assertThat("set method returned this", setMatrNoReturn, is(sameInstance(karli)));
        assertThat("no property change", eventList.size() == 1);

        setMatrNo.invoke(karli, 23);
        assertThat("got property change", eventList.size() == 2);

        // test toString()
        Method toString = studClass.getMethod("toString");
        Object txt = toString.invoke(karli);
        assertThat("toString", txt, is(equalTo("Karli")));

        toString = uniClass.getMethod("toString");
        txt = toString.invoke(studyRight);
        assertThat("toString", txt, is(equalTo("Hello")));
    }

    void runAssociationReadWriteTests(String outFolder, ClassModel model) throws Exception {
        final ArrayList<PropertyChangeEvent> eventList = new ArrayList<>();
        PropertyChangeListener listener = eventList::add;

        // run self test
        File classesDir = new File(outFolder);

        // Load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classesDir.toURI().toURL()});

        Class<?> uniClass = Class.forName(model.getPackageName() + ".University", true, classLoader);
        Class<?> studClass = Class.forName(model.getPackageName() + ".Student", true, classLoader);
        Class<?> roomClass = Class.forName(model.getPackageName() + ".Room", true, classLoader);

        Object studyRight = uniClass.newInstance();
        Object studyFuture = uniClass.newInstance();

        Method setName = uniClass.getMethod("setName", String.class);
        Method addPropertyChangeListener = uniClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
        setName.invoke(studyRight, "Study Right");
        setName.invoke(studyFuture, "Study Future");
        addPropertyChangeListener.invoke(studyRight, listener);
        addPropertyChangeListener.invoke(studyFuture, listener);

        Object karli = studClass.newInstance();
        Object lee = studClass.newInstance();

        setName = studClass.getMethod("setName", String.class);
        addPropertyChangeListener = studClass.getMethod("addPropertyChangeListener", PropertyChangeListener.class);
        setName.invoke(karli, "Karli");
        setName.invoke(lee, "Lee");
        addPropertyChangeListener.invoke(karli, listener);
        addPropertyChangeListener.invoke(lee, listener);

        // do not add to students directly
        Method getStudents = uniClass.getMethod("getStudents");
        Collection studentSet = (Collection) getStudents.invoke(studyRight);
        assertThrows(UnsupportedOperationException.class, () -> studentSet.add(karli),
                "should not be possible to add an object to a role set directly");

        // ok, create a link
        assertThat(studyRight, hasProperty("students", is(empty())));
        assertThat(karli, hasProperty("uni", nullValue()));

        Method withStudents = uniClass.getMethod("withStudents", Object[].class);
        Object withResult = withStudents.invoke(studyRight, new Object[]{new Object[]{karli}});
        assertThat(withResult, is(equalTo(studyRight)));
        assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
        assertThat(karli, hasProperty("uni", equalTo(studyRight)));

        Method setUni = studClass.getMethod("setUni", uniClass);
        Object setUniResult = setUni.invoke(karli, studyFuture);
        assertThat(setUniResult, is(equalTo(karli)));
        assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
        assertThat(studyRight, hasProperty("students", is(empty())));
        assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli)));

        setUni.invoke(karli, new Object[]{null});
        assertThat(karli, hasProperty("uni", nullValue()));
        assertThat(studyFuture, hasProperty("students", is(empty())));

        withStudents.invoke(studyRight, new Object[]{new Object[]{karli, lee}});
        assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli, lee)));
        assertThat(karli, hasProperty("uni", equalTo(studyRight)));
        assertThat(lee, hasProperty("uni", equalTo(studyRight)));

        assertThrows(Exception.class, () -> withStudents.invoke(studyFuture, new Object[]{new Object[]{karli, lee, studyRight}}));

        assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli, lee)));
        assertThat(studyFuture, hasProperty("students", not(containsInAnyOrder(studyRight))));
        assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
        assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

        Method withoutStudents = uniClass.getMethod("withoutStudents", Object[].class);
        withoutStudents.invoke(studyFuture, new Object[]{new Object[]{karli, lee, studyRight}});
        assertThat(studyFuture, hasProperty("students", is(empty())));
        assertThat(karli, hasProperty("uni", nullValue()));
        assertThat(lee, hasProperty("uni", nullValue()));

        withStudents.invoke(studyRight, new Object[]{new Object[]{karli, lee}});
        withStudents.invoke(studyFuture, new Object[]{new Object[]{lee}});
        assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
        assertThat(studyFuture, hasProperty("students", containsInAnyOrder(lee)));
        assertThat(karli, hasProperty("uni", equalTo(studyRight)));
        assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

        // test LinkedHashSet role
        Object wa1337 = roomClass.newInstance();
        Object wa1342 = roomClass.newInstance();

        Method withRooms = uniClass.getMethod("withRooms", Object[].class);
        Method setUni4Room = roomClass.getMethod("setUni", uniClass);

        Object withRoomsResult = withRooms.invoke(studyRight, new Object[]{new Object[]{wa1337, wa1342}});
        assertThat(withRoomsResult, equalTo(studyRight));
        assertThat(studyRight, hasProperty("rooms", containsInAnyOrder(wa1337, wa1342)));
        assertThat(wa1337, hasProperty("uni", equalTo(studyRight)));
        assertThat(wa1342, hasProperty("uni", equalTo(studyRight)));

        withRooms.invoke(studyFuture, new Object[]{new Object[]{wa1342}});
        assertThat(studyRight, hasProperty("rooms", not(containsInAnyOrder(wa1342))));
        assertThat(studyFuture, hasProperty("rooms", containsInAnyOrder(wa1342)));
        assertThat(wa1342, hasProperty("uni", equalTo(studyFuture)));

        // test 1 to 1
        Method setCondo = studClass.getMethod("setCondo", roomClass);
        Object setCondoResult = setCondo.invoke(karli, wa1337);
        assertThat(setCondoResult, equalTo(karli));
        assertThat(karli, hasProperty("condo", equalTo(wa1337)));
        assertThat(wa1337, hasProperty("owner", equalTo(karli)));

        setCondo.invoke(lee, wa1337);
        assertThat(karli, hasProperty("condo", nullValue()));
        assertThat(lee, hasProperty("condo", equalTo(wa1337)));
        assertThat(wa1337, hasProperty("owner", equalTo(lee)));

        // test n to m
        Method withIn = studClass.getMethod("withIn", Object[].class);

        Object withInResult = withIn.invoke(karli, new Object[]{new Object[]{wa1337, wa1342}});
        withIn.invoke(lee, new Object[]{new Object[]{wa1337, wa1342}});
        assertThat(withInResult, equalTo(karli));
        assertThat(karli, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
        assertThat(lee, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
        assertThat(wa1337, hasProperty("students", containsInAnyOrder(karli, lee)));
        assertThat(wa1342, hasProperty("students", containsInAnyOrder(karli, lee)));

        Method withoutStudents4Room = roomClass.getMethod("withoutStudents", Object[].class);
        withoutStudents4Room.invoke(wa1337, new Object[]{new Object[]{lee}});
        assertThat(wa1337, hasProperty("students", not(containsInAnyOrder(lee))));
        assertThat(lee, hasProperty("in", not(containsInAnyOrder(wa1337))));

        Method removeYou = uniClass.getMethod("removeYou");
        removeYou.invoke(studyRight);
        assertThat(karli, hasProperty("uni", nullValue()));
        assertThat(wa1337, hasProperty("uni", nullValue()));
        assertThat(wa1337, hasProperty("students", not(containsInAnyOrder(karli))));
    }

    void runExtendsReadWriteTests(String outFolder, ClassModel model) throws Exception {
        // run self test
        File classesDir = new File(outFolder);

        // Load and instantiate compiled class.
        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classesDir.toURI().toURL()});

        Class<?> uniClass = Class.forName(model.getPackageName() + ".University", true, classLoader);
        Class<?> taClass = Class.forName(model.getPackageName() + ".TeachingAssistent", true, classLoader);

        Object studyRight = uniClass.newInstance();
        Object studyFuture = uniClass.newInstance();

        Method setName = uniClass.getMethod("setName", String.class);
        setName.invoke(studyRight, "Study Right");
        setName.invoke(studyRight, "Study Future");

        Object karli = taClass.newInstance();

        setName = taClass.getMethod("setName", String.class);
        setName.invoke(karli, "Karli");


        // ok, create a link
        assertThat(karli, hasProperty("uni", nullValue()));

        Method withStudents = uniClass.getMethod("withStudents", Object[].class);
        Object withResult = withStudents.invoke(studyRight, new Object[]{new Object[]{karli}});
        assertThat(withResult, is(equalTo(studyRight)));
        assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
        assertThat(karli, hasProperty("uni", equalTo(studyRight)));

        Method setUni = taClass.getMethod("setUni", uniClass);
        Object setUniResult = setUni.invoke(karli, studyFuture);
        assertThat(setUniResult, is(equalTo(karli)));
        assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
        assertThat(studyRight, hasProperty("students", is(empty())));
        assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli)));


        Method setLevel = taClass.getMethod("setLevel", String.class);
        setLevel.invoke(karli, "master");
        assertThat(karli, hasProperty("level", equalTo("master")));

    }

    void runTableTests(String outFolder, ClassModel model) throws Exception {
        getTableExampleObjects(outFolder, model);

        // simple table
        Class<?> uniTableClass = Class.forName(model.getPackageName() + ".tables.UniversityTable", true, classLoader);
        Class<?> roomsTableClass = Class.forName(model.getPackageName() + ".tables.RoomTable", true, classLoader);
        Class<?> studentsTableClass = Class.forName(model.getPackageName() + ".tables.StudentTable", true, classLoader);
        Class<?> assignmentsTableClass = Class.forName(model.getPackageName() + ".tables.AssignmentTable", true, classLoader);
        Class<?> intTableClass = Class.forName(model.getPackageName() + ".tables.intTable", true, classLoader);

        Constructor<?> declaredConstructors = uniTableClass.getDeclaredConstructors()[0];
        Method uniExpandRooms = uniTableClass.getMethod("expandRooms", String[].class);
        Method uniExpandStudents = uniTableClass.getMethod("expandStudents", String[].class);
        Method roomsExpandAssignments = roomsTableClass.getMethod("expandAssignments", String[].class);
        Method roomsExpandStudents = roomsTableClass.getMethod("expandStudents", String[].class);
        Method studentTablehasDone = studentsTableClass.getMethod("hasDone", assignmentsTableClass);
        Method studentTableSelectColumns = studentsTableClass.getMethod("selectColumns", String[].class);
        Method studentTableDropColumns = studentsTableClass.getMethod("dropColumns", String[].class);
        Method studentTableAddColumn = studentsTableClass.getMethod("addColumn", String.class, Function.class);
        Method assignmentsToSet = assignmentsTableClass.getMethod("toSet");
        Method assignmentsExpandPoints = assignmentsTableClass.getMethod("expandPoints", String[].class);
        Method assignmentsFilter = assignmentsTableClass.getMethod("filter", Predicate.class);
        Method assignmentsFilterRow = assignmentsTableClass.getMethod("filterRow", Predicate.class);
        Method intTableToList = intTableClass.getMethod("toList");
        Method intTableSum = intTableClass.getMethod("sum");
        final Method assignmentGetPoints = assignClass.getMethod("getPoints");
        final Method studentGetDone = studClass.getMethod("getDone");

        Object uniArray = Array.newInstance(uniClass, 1);
        Array.set(uniArray, 0, studyRight);

        Object uniTable = declaredConstructors.newInstance(uniArray);
        assertThat(uniTable, notNullValue());

        Object roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
        assertThat(roomsTable.toString(), containsString("wa1337"));
        assertThat(roomsTable.toString(), containsString("wa1338"));
        assertThat(roomsTable.toString(), containsString("wa1339"));

        Object assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});
        assertThat(assignmentsTable.toString(), containsString("integrals"));

        Set assignmentsSet = (Set) assignmentsToSet.invoke(assignmentsTable);
        assertThat(assignmentsSet.size(), equalTo(4));

        Object pointsTable = assignmentsExpandPoints.invoke(assignmentsTable, new Object[]{new String[]{"Points"}});
        List pointsList = (List) intTableToList.invoke(pointsTable);
        assertThat(pointsList.size(), equalTo(4));

        Object sum = intTableSum.invoke(pointsTable);
        assertThat(sum, equalTo(89));

        Object studentsTable = roomsExpandStudents.invoke(roomsTable, new Object[]{new String[]{"Students"}});
        assertThat(studentsTable.toString(), containsString("Alice"));

        Predicate<Object> predicate = o -> {
            try {
                int points = (Integer) assignmentGetPoints.invoke(o);
                return points <= 20;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        };

        assignmentsFilter.invoke(assignmentsTable, predicate);
        assertThat(assignmentsTable.toString(), not(containsString("integrals")));
        assertThat(assignmentsTable.toString(), containsString("sculptures"));

        // filter row
        uniTable = declaredConstructors.newInstance(uniArray);
        uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});
        roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
        assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});


        Predicate<Object> rowPredicate = o -> {
            try {
                LinkedHashMap<String, Object> row = (LinkedHashMap<String, Object>) o;
                Object stud = row.get("Students");
                Object assign = row.get("Assignments");
                Collection doneSet = (Collection) studentGetDone.invoke(stud);
                return !doneSet.contains(assign);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        };

        assignmentsFilterRow.invoke(assignmentsTable, rowPredicate);
        assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tintegrals")));
        assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math \tmatrices"));


        // has done
        uniTable = declaredConstructors.newInstance(uniArray);
        studentsTable = uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});
        roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
        assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});

        studentTablehasDone.invoke(studentsTable, assignmentsTable);
        assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math \tintegrals"));
        assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tmatrices")));

        // select columns
        uniTable = declaredConstructors.newInstance(uniArray);
        studentsTable = uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});
        roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
        assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});

        studentTableSelectColumns.invoke(studentsTable, new Object[]{new String[]{"Students", "Rooms"}});
        assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math"));
        assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tintegrals")));

        // drop columns
        uniTable = declaredConstructors.newInstance(uniArray);
        studentsTable = uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});
        roomsTable = uniExpandRooms.invoke(uniTable, new Object[]{new String[]{"Rooms"}});
        assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[]{new String[]{"Assignments"}});

        studentTableDropColumns.invoke(studentsTable, new Object[]{new String[]{"Assignments"}});
        assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math"));
        assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tintegrals")));

        // add column
        uniTable = declaredConstructors.newInstance(uniArray);
        studentsTable = uniExpandStudents.invoke(uniTable, new Object[]{new String[]{"Students"}});

        Function<LinkedHashMap<String, Object>, Object> function = row -> {
            Object student = row.get("Students");
            return 42;
        };

        studentTableAddColumn.invoke(studentsTable, "Credits", function);
        assertThat(studentsTable.toString(), containsString("Credits"));
        assertThat(studentsTable.toString(), containsString("42"));
    }

    private void getTableExampleObjects(String outFolder, ClassModel model)
            throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, InstantiationException,
            IllegalAccessException, InvocationTargetException {

        // create example objects
        File classesDir = new File(outFolder);

        // Load and instantiate compiled class.
        classLoader = URLClassLoader.newInstance(new URL[]{classesDir.toURI().toURL()});

        uniClass = Class.forName(model.getPackageName() + ".University", true, classLoader);
        studClass = Class.forName(model.getPackageName() + ".Student", true, classLoader);
        Class<?> roomClass = Class.forName(model.getPackageName() + ".Room", true, classLoader);
        assignClass = Class.forName(model.getPackageName() + ".Assignment", true, classLoader);

        Method uniSetName = uniClass.getMethod("setName", String.class);
        Method roomSetRoomNo = roomClass.getMethod("setRoomNo", String.class);
        Method roomSetTopic = roomClass.getMethod("setTopic", String.class);
        Method roomSetUni = roomClass.getMethod("setUni", uniClass);
        Method assignmentSetTopic = assignClass.getMethod("setTopic", String.class);
        Method assignmentSetPoints = assignClass.getMethod("setPoints", int.class);
        Method assignmentSetRoom = assignClass.getMethod("setRoom", roomClass);
        Method studStudentId = studClass.getMethod("setStudentId", String.class);
        Method studSetName = studClass.getMethod("setName", String.class);
        Method studSetUni = studClass.getMethod("setUni", uniClass);
        Method studSetIn = studClass.getMethod("setIn", roomClass);
        Method studWithDone = studClass.getMethod("withDone", Object[].class);


        studyRight = uniClass.newInstance();
        uniSetName.invoke(studyRight, "Study Right");

        Object mathRoom = roomClass.newInstance();
        roomSetRoomNo.invoke(mathRoom, "wa1337");
        roomSetTopic.invoke(mathRoom, "Math");
        roomSetUni.invoke(mathRoom, studyRight);

        Object artsRoom = roomClass.newInstance();
        roomSetRoomNo.invoke(artsRoom, "wa1338");
        roomSetTopic.invoke(artsRoom, "Arts");
        roomSetUni.invoke(artsRoom, studyRight);

        Object sportsRoom = roomClass.newInstance();
        roomSetRoomNo.invoke(sportsRoom, "wa1339");
        roomSetTopic.invoke(sportsRoom, "Football");
        roomSetUni.invoke(sportsRoom, studyRight);

        Object integrals = assignClass.newInstance();
        assignmentSetTopic.invoke(integrals, "integrals");
        assignmentSetPoints.invoke(integrals, 42);
        assignmentSetRoom.invoke(integrals, mathRoom);

        Object matrix = assignClass.newInstance();
        assignmentSetTopic.invoke(matrix, "matrices");
        assignmentSetPoints.invoke(matrix, 23);
        assignmentSetRoom.invoke(matrix, mathRoom);

        Object drawings = assignClass.newInstance();
        assignmentSetTopic.invoke(drawings, "drawings");
        assignmentSetPoints.invoke(drawings, 12);
        assignmentSetRoom.invoke(drawings, artsRoom);

        Object sculptures = assignClass.newInstance();
        assignmentSetTopic.invoke(sculptures, "sculptures");
        assignmentSetPoints.invoke(sculptures, 12);
        assignmentSetRoom.invoke(sculptures, artsRoom);

        Object alice = studClass.newInstance();
        studStudentId.invoke(alice, "m4242");
        studSetName.invoke(alice, "Alice");
        studSetUni.invoke(alice, studyRight);
        studSetIn.invoke(alice, artsRoom);
        studWithDone.invoke(alice, new Object[]{new Object[]{integrals}});

        Object bob = studClass.newInstance();
        studStudentId.invoke(bob, "m2323");
        studSetName.invoke(bob, "Bobby");
        studSetUni.invoke(bob, studyRight);
        studSetIn.invoke(bob, artsRoom);

        Object carli = studClass.newInstance();
        studStudentId.invoke(carli, "m2323");
        studSetName.invoke(carli, "Carli");
        studSetUni.invoke(carli, studyRight);
        studSetIn.invoke(carli, mathRoom);
    }
}
