package org.fulib;

import org.fulib.builder.ClassBuilder;
import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;
import org.fulib.classmodel.*;
import org.junit.jupiter.api.Test;
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

public class TestGenerator
{

   private Class<?>       uniClass;
   private URLClassLoader classLoader;
   private Object         studyRight;
   private Class<?>       assignClass;
   private Class<?>       studClass;



   @Test
   void testTables() throws Exception
   {
      String targetFolder = "tmp";

      Tools.removeDirAndFiles(targetFolder);

      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright", "tmp/src");

      ClassBuilder uni = mb.buildClass("University").buildAttribute("name", Type.STRING);

      ClassBuilder student = mb.buildClass("Student").buildAttribute("name", Type.STRING)
                               .buildAttribute("studentId", Type.STRING).buildAttribute("credits", Type.INT);

      student.buildAssociation(student, "friends", Type.MANY, "friends", Type.MANY);
      uni.buildAssociation(student, "students", Type.MANY, "uni", Type.ONE);

      ClassBuilder ta = mb.buildClass("Tutor").setSuperClass(student);

      ClassBuilder room = mb.buildClass("Room").buildAttribute("roomNo", Type.STRING)
                            .buildAttribute("topic", Type.STRING);

      uni.buildAssociation(room, "rooms", Type.MANY, "uni", Type.ONE);
      student.buildAssociation(room, "in", Type.ONE, "students", Type.MANY);

      ClassBuilder assignment = mb.buildClass("Assignment").buildAttribute("topic", Type.STRING)
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

      this.runTableTests(outFolder, model);
   }

   @Test
   void testModelEvolution() throws IOException
   {
      Tools.removeDirAndFiles("tmp");

      // first simple model
      ClassModelBuilder mb = Fulib.classModelBuilder("org.evolve", "tmp/src");
      ClassBuilder uni = mb.buildClass("University").buildAttribute("uniName", Type.STRING);
      ClassBuilder stud = mb.buildClass("Student").buildAttribute("matNo", Type.STRING)
                            .buildAttribute("startYear", Type.INT);
      uni.buildAssociation(stud, "students", Type.MANY, "uni", Type.ONE);
      ClassBuilder room = mb.buildClass("Room").buildAttribute("roomNo", Type.STRING);

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
      Handler handler = new Handler()
      {
         @Override
         public void publish(LogRecord record)
         {
            logRecordList.add(record);
         }

         @Override
         public void flush()
         {
         }

         @Override
         public void close() throws SecurityException
         {
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
   void testCustomTemplates() throws IOException
   {
      Tools.removeDirAndFiles("tmp");

      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib.studyright", "tmp/src");
      mb.buildClass("University").buildAttribute("name", Type.STRING);
      mb.buildClass("Student").buildAttribute("name", Type.STRING, "\"Karli\"")
        .buildAttribute("matrNo", Type.LONG, "0");

      ClassModel model = mb.getClassModel();

      // generate normal
      Fulib.generator().generate(model);

      byte[] bytes = Files.readAllBytes(Paths.get(model.getPackageSrcFolder() + "/Student.java"));
      String content = new String(bytes);
      assertThat(content, not(containsString("/* custom attribute comment */")));

      // generate custom
      // start_code_fragment: testCustomTemplates
      Fulib.generator().setCustomTemplatesFile("templates/custom.stg").generate(model);
      // end_code_fragment:

      bytes = Files.readAllBytes(Paths.get(model.getPackageSrcFolder() + "/Student.java"));
      content = new String(bytes);
      assertThat(content, containsString("/* custom attribute comment */"));
   }

   public static void createPreexistingUniFile(String packageName, ClassModel model) throws IOException
   {
      // create pre existing University class with extra elements
      STGroup group = new STGroupFile("templates/university.stg");
      ST uniTemplate = group.getInstanceOf("university");
      uniTemplate.add("packageName", packageName);
      String uniText = uniTemplate.render();

      Files.createDirectories(Paths.get(model.getPackageSrcFolder()));
      Files.write(Paths.get(model.getPackageSrcFolder() + "/University.java"), uniText.getBytes());
   }

   void runTableTests(String outFolder, ClassModel model) throws Exception
   {
      this.getTableExampleObjects(outFolder, model);

      // simple table
      Class<?> uniTableClass = Class
         .forName(model.getPackageName() + ".tables.UniversityTable", true, this.classLoader);
      Class<?> roomsTableClass = Class.forName(model.getPackageName() + ".tables.RoomTable", true, this.classLoader);
      Class<?> studentsTableClass = Class
         .forName(model.getPackageName() + ".tables.StudentTable", true, this.classLoader);
      Class<?> assignmentsTableClass = Class
         .forName(model.getPackageName() + ".tables.AssignmentTable", true, this.classLoader);
      Class<?> intTableClass = Class.forName(model.getPackageName() + ".tables.intTable", true, this.classLoader);

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
      final Method assignmentGetPoints = this.assignClass.getMethod("getPoints");
      final Method studentGetDone = this.studClass.getMethod("getDone");

      Object uniArray = Array.newInstance(this.uniClass, 1);
      Array.set(uniArray, 0, this.studyRight);

      Object uniTable = declaredConstructors.newInstance(uniArray);
      assertThat(uniTable, notNullValue());

      Object roomsTable = uniExpandRooms.invoke(uniTable, new Object[] { new String[] { "Rooms" } });
      assertThat(roomsTable.toString(), containsString("wa1337"));
      assertThat(roomsTable.toString(), containsString("wa1338"));
      assertThat(roomsTable.toString(), containsString("wa1339"));

      Object assignmentsTable = roomsExpandAssignments
         .invoke(roomsTable, new Object[] { new String[] { "Assignments" } });
      assertThat(assignmentsTable.toString(), containsString("integrals"));

      Set assignmentsSet = (Set) assignmentsToSet.invoke(assignmentsTable);
      assertThat(assignmentsSet.size(), equalTo(4));

      Object pointsTable = assignmentsExpandPoints.invoke(assignmentsTable, new Object[] { new String[] { "Points" } });
      List pointsList = (List) intTableToList.invoke(pointsTable);
      assertThat(pointsList.size(), equalTo(4));

      Object sum = intTableSum.invoke(pointsTable);
      assertThat(sum, equalTo(89));

      Object studentsTable = roomsExpandStudents.invoke(roomsTable, new Object[] { new String[] { "Students" } });
      assertThat(studentsTable.toString(), containsString("Alice"));

      Predicate<Object> predicate = o -> {
         try
         {
            int points = (Integer) assignmentGetPoints.invoke(o);
            return points <= 20;
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         return false;
      };

      assignmentsFilter.invoke(assignmentsTable, predicate);
      assertThat(assignmentsTable.toString(), not(containsString("integrals")));
      assertThat(assignmentsTable.toString(), containsString("sculptures"));

      // filter row
      uniTable = declaredConstructors.newInstance(uniArray);
      uniExpandStudents.invoke(uniTable, new Object[] { new String[] { "Students" } });
      roomsTable = uniExpandRooms.invoke(uniTable, new Object[] { new String[] { "Rooms" } });
      assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[] { new String[] { "Assignments" } });

      Predicate<Object> rowPredicate = o -> {
         try
         {
            LinkedHashMap<String, Object> row = (LinkedHashMap<String, Object>) o;
            Object stud = row.get("Students");
            Object assign = row.get("Assignments");
            Collection doneSet = (Collection) studentGetDone.invoke(stud);
            return !doneSet.contains(assign);
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
         return false;
      };

      assignmentsFilterRow.invoke(assignmentsTable, rowPredicate);
      assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tintegrals")));
      assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math \tmatrices"));

      // has done
      uniTable = declaredConstructors.newInstance(uniArray);
      studentsTable = uniExpandStudents.invoke(uniTable, new Object[] { new String[] { "Students" } });
      roomsTable = uniExpandRooms.invoke(uniTable, new Object[] { new String[] { "Rooms" } });
      assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[] { new String[] { "Assignments" } });

      studentTablehasDone.invoke(studentsTable, assignmentsTable);
      assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math \tintegrals"));
      assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tmatrices")));

      // select columns
      uniTable = declaredConstructors.newInstance(uniArray);
      studentsTable = uniExpandStudents.invoke(uniTable, new Object[] { new String[] { "Students" } });
      roomsTable = uniExpandRooms.invoke(uniTable, new Object[] { new String[] { "Rooms" } });
      assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[] { new String[] { "Assignments" } });

      studentTableSelectColumns.invoke(studentsTable, new Object[] { new String[] { "Students", "Rooms" } });
      assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math"));
      assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tintegrals")));

      // drop columns
      uniTable = declaredConstructors.newInstance(uniArray);
      studentsTable = uniExpandStudents.invoke(uniTable, new Object[] { new String[] { "Students" } });
      roomsTable = uniExpandRooms.invoke(uniTable, new Object[] { new String[] { "Rooms" } });
      assignmentsTable = roomsExpandAssignments.invoke(roomsTable, new Object[] { new String[] { "Assignments" } });

      studentTableDropColumns.invoke(studentsTable, new Object[] { new String[] { "Assignments" } });
      assertThat(assignmentsTable.toString(), containsString("Alice m4242 \twa1337 Math"));
      assertThat(assignmentsTable.toString(), not(containsString("Alice m4242 \twa1337 Math \tintegrals")));

      // add column
      uniTable = declaredConstructors.newInstance(uniArray);
      studentsTable = uniExpandStudents.invoke(uniTable, new Object[] { new String[] { "Students" } });

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
      IllegalAccessException, InvocationTargetException
   {

      // create example objects
      File classesDir = new File(outFolder);

      // Load and instantiate compiled class.
      this.classLoader = URLClassLoader.newInstance(new URL[] { classesDir.toURI().toURL() });

      this.uniClass = Class.forName(model.getPackageName() + ".University", true, this.classLoader);
      this.studClass = Class.forName(model.getPackageName() + ".Student", true, this.classLoader);
      Class<?> roomClass = Class.forName(model.getPackageName() + ".Room", true, this.classLoader);
      this.assignClass = Class.forName(model.getPackageName() + ".Assignment", true, this.classLoader);

      Method uniSetName = this.uniClass.getMethod("setName", String.class);
      Method roomSetRoomNo = roomClass.getMethod("setRoomNo", String.class);
      Method roomSetTopic = roomClass.getMethod("setTopic", String.class);
      Method roomSetUni = roomClass.getMethod("setUni", this.uniClass);
      Method assignmentSetTopic = this.assignClass.getMethod("setTopic", String.class);
      Method assignmentSetPoints = this.assignClass.getMethod("setPoints", int.class);
      Method assignmentSetRoom = this.assignClass.getMethod("setRoom", roomClass);
      Method studStudentId = this.studClass.getMethod("setStudentId", String.class);
      Method studSetName = this.studClass.getMethod("setName", String.class);
      Method studSetUni = this.studClass.getMethod("setUni", this.uniClass);
      Method studSetIn = this.studClass.getMethod("setIn", roomClass);
      Method studWithDone = this.studClass.getMethod("withDone", Object[].class);

      this.studyRight = this.uniClass.newInstance();
      uniSetName.invoke(this.studyRight, "Study Right");

      Object mathRoom = roomClass.newInstance();
      roomSetRoomNo.invoke(mathRoom, "wa1337");
      roomSetTopic.invoke(mathRoom, "Math");
      roomSetUni.invoke(mathRoom, this.studyRight);

      Object artsRoom = roomClass.newInstance();
      roomSetRoomNo.invoke(artsRoom, "wa1338");
      roomSetTopic.invoke(artsRoom, "Arts");
      roomSetUni.invoke(artsRoom, this.studyRight);

      Object sportsRoom = roomClass.newInstance();
      roomSetRoomNo.invoke(sportsRoom, "wa1339");
      roomSetTopic.invoke(sportsRoom, "Football");
      roomSetUni.invoke(sportsRoom, this.studyRight);

      Object integrals = this.assignClass.newInstance();
      assignmentSetTopic.invoke(integrals, "integrals");
      assignmentSetPoints.invoke(integrals, 42);
      assignmentSetRoom.invoke(integrals, mathRoom);

      Object matrix = this.assignClass.newInstance();
      assignmentSetTopic.invoke(matrix, "matrices");
      assignmentSetPoints.invoke(matrix, 23);
      assignmentSetRoom.invoke(matrix, mathRoom);

      Object drawings = this.assignClass.newInstance();
      assignmentSetTopic.invoke(drawings, "drawings");
      assignmentSetPoints.invoke(drawings, 12);
      assignmentSetRoom.invoke(drawings, artsRoom);

      Object sculptures = this.assignClass.newInstance();
      assignmentSetTopic.invoke(sculptures, "sculptures");
      assignmentSetPoints.invoke(sculptures, 12);
      assignmentSetRoom.invoke(sculptures, artsRoom);

      Object alice = this.studClass.newInstance();
      studStudentId.invoke(alice, "m4242");
      studSetName.invoke(alice, "Alice");
      studSetUni.invoke(alice, this.studyRight);
      studSetIn.invoke(alice, artsRoom);
      studWithDone.invoke(alice, new Object[] { new Object[] { integrals } });

      Object bob = this.studClass.newInstance();
      studStudentId.invoke(bob, "m2323");
      studSetName.invoke(bob, "Bobby");
      studSetUni.invoke(bob, this.studyRight);
      studSetIn.invoke(bob, artsRoom);

      Object carli = this.studClass.newInstance();
      studStudentId.invoke(carli, "m2323");
      studSetName.invoke(carli, "Carli");
      studSetUni.invoke(carli, this.studyRight);
      studSetIn.invoke(carli, mathRoom);
   }
}
