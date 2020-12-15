package org.fulib.generator;

import org.fulib.builder.ClassModelBuilder;
import org.fulib.builder.Type;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

public class BeanAttributeTest extends AttributeTest
{
   @Override
   protected String getTargetFolder()
   {
      return "tmp/bean/attributes";
   }

   @Override
   protected void configureModel(ClassModelBuilder mb)
   {
      mb.setDefaultPropertyStyle(Type.BEAN);
   }

   @Override
   protected void runDataTests(ClassLoader classLoader, String packageName) throws Exception
   {
      final ArrayList<PropertyChangeEvent> eventList = new ArrayList<>();
      final PropertyChangeListener listener = eventList::add;

      // run self test

      Class<?> uniClass = Class.forName(packageName + ".University", true, classLoader);

      Object studyRight = uniClass.newInstance();

      ((PropertyChangeSupport) uniClass.getMethod("listeners").invoke(studyRight)).addPropertyChangeListener(listener);

      assertThat(studyRight, hasProperty("name", nullValue()));

      Method setName = uniClass.getMethod("setName", String.class);

      Object setNameReturn = setName.invoke(studyRight, "StudyRight");
      assertThat("setName returned this", setNameReturn, is(sameInstance(studyRight)));
      assertThat("got property change", !eventList.isEmpty());

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

      Class<?> studClass = Class.forName(packageName + ".Student", true, classLoader);

      Object karli = studClass.newInstance();
      Method setStudentName = studClass.getMethod("setName", String.class);
      setStudentName.invoke(karli, "Karli");

      Method setMatrNo = studClass.getMethod("setMatrNo", long.class);
      ((PropertyChangeSupport) studClass.getMethod("listeners").invoke(karli)).addPropertyChangeListener(listener);

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
}
