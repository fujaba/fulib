package org.fulib.generator;

import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

public class JavaFXAssociationTest extends BeanAssociationTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/javafx/associations";
   }

   @Override
   protected void configureModel(ClassModelBuilder mb)
   {
      mb.setDefaultPropertyStyle(Type.JAVA_FX);
   }

   @Override
   protected void runDataTests(ClassLoader classLoader, String packageName) throws Exception
   {
      final ArrayList<PropertyChangeEvent> eventList = new ArrayList<>();
      PropertyChangeListener listener = eventList::add;

      Class<?> uniClass = Class.forName(packageName + ".University", true, classLoader);
      Class<?> studClass = Class.forName(packageName + ".Student", true, classLoader);
      Class<?> roomClass = Class.forName(packageName + ".Room", true, classLoader);

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

      // ok, create a link
      assertThat(studyRight, hasProperty("students", is(empty())));
      assertThat(karli, hasProperty("uni", nullValue()));

      Method withStudents = uniClass.getMethod("withStudents", studClass);
      Object withResult = withStudents.invoke(studyRight, karli);
      assertThat(withResult, is(equalTo(studyRight)));
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));

      Method setUni = studClass.getMethod("setUni", uniClass);
      Object setUniResult = setUni.invoke(karli, studyFuture);
      assertThat(setUniResult, is(equalTo(karli)));
      assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      assertThat(studyRight, hasProperty("students", is(empty())));
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli)));

      setUni.invoke(karli, new Object[] { null });
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(studyFuture, hasProperty("students", is(empty())));

      withStudents.invoke(studyRight, karli);
      withStudents.invoke(studyRight, lee);
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      assertThat(lee, hasProperty("uni", equalTo(studyRight)));

      withStudents.invoke(studyFuture, karli);
      withStudents.invoke(studyFuture, lee);
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(studyFuture, hasProperty("students", not(containsInAnyOrder(studyRight))));
      assertThat(karli, hasProperty("uni", equalTo(studyFuture)));
      assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

      Method withoutStudents = uniClass.getMethod("withoutStudents", studClass);
      withoutStudents.invoke(studyFuture, karli);
      withoutStudents.invoke(studyFuture, lee);
      withoutStudents.invoke(studyFuture, lee);
      assertThat(studyFuture, hasProperty("students", is(empty())));
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(lee, hasProperty("uni", nullValue()));

      withStudents.invoke(studyRight, karli);
      withStudents.invoke(studyRight, lee);
      withStudents.invoke(studyFuture, lee);
      assertThat(studyRight, hasProperty("students", containsInAnyOrder(karli)));
      assertThat(studyFuture, hasProperty("students", containsInAnyOrder(lee)));
      assertThat(karli, hasProperty("uni", equalTo(studyRight)));
      assertThat(lee, hasProperty("uni", equalTo(studyFuture)));

      // test LinkedHashSet role
      Object wa1337 = roomClass.newInstance();
      Object wa1342 = roomClass.newInstance();

      Method withRooms = uniClass.getMethod("withRooms", roomClass);
      Method setUni4Room = roomClass.getMethod("setUni", uniClass);

      Object withRoomsResult = withRooms.invoke(studyRight, wa1337);
      withRooms.invoke(studyRight, wa1342);
      assertThat(withRoomsResult, equalTo(studyRight));
      assertThat(studyRight, hasProperty("rooms", containsInAnyOrder(wa1337, wa1342)));
      assertThat(wa1337, hasProperty("uni", equalTo(studyRight)));
      assertThat(wa1342, hasProperty("uni", equalTo(studyRight)));

      withRooms.invoke(studyFuture, wa1342);
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
      Method withIn = studClass.getMethod("withIn", roomClass);
      Method withStudents4Room = roomClass.getMethod("withStudents", studClass);

      Object withInResult = withIn.invoke(karli, wa1337);
      withIn.invoke(karli, wa1342);
      withIn.invoke(lee, wa1337);
      withIn.invoke(lee, wa1342);
      assertThat(withInResult, equalTo(karli));
      assertThat(karli, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
      assertThat(lee, hasProperty("in", containsInAnyOrder(wa1337, wa1342)));
      assertThat(wa1337, hasProperty("students", containsInAnyOrder(karli, lee)));
      assertThat(wa1342, hasProperty("students", containsInAnyOrder(karli, lee)));

      Method withoutStudents4Room = roomClass.getMethod("withoutStudents", studClass);
      withoutStudents4Room.invoke(wa1337, lee);
      assertThat(wa1337, hasProperty("students", not(containsInAnyOrder(lee))));
      assertThat(lee, hasProperty("in", not(containsInAnyOrder(wa1337))));

      Method removeYou = uniClass.getMethod("removeYou");
      removeYou.invoke(studyRight);
      assertThat(karli, hasProperty("uni", nullValue()));
      assertThat(wa1337, hasProperty("uni", nullValue()));
      assertThat(wa1337, hasProperty("students", not(containsInAnyOrder(karli))));
   }
}
