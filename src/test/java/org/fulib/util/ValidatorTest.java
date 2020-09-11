package org.fulib.util;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class ValidatorTest
{

   @Test
   void isProperty()
   {
      assertThat(Validator.isProperty("getName", 0), is(true));
      assertThat(Validator.isProperty("getName", 1), is(false));
      assertThat(Validator.isProperty("setName", 0), is(false));
      assertThat(Validator.isProperty("setName", 1), is(true));
   }

   @Test
   void isSetter()
   {
      assertThat(Validator.isSetter("getName", 0), is(false));
      assertThat(Validator.isSetter("setName", 0), is(false));
      assertThat(Validator.isSetter("setName", 1), is(true));
      assertThat(Validator.isSetter("setName", 2), is(false));
      assertThat(Validator.isSetter("set_id", 0), is(false));
      assertThat(Validator.isSetter("set_id", 1), is(true));
      assertThat(Validator.isSetter("set_id", 2), is(false));
      assertThat(Validator.isSetter("withName", 0), is(false));
      assertThat(Validator.isSetter("withName", 1), is(true));
      assertThat(Validator.isSetter("withName", 2), is(false));
      assertThat(Validator.isSetter("withoutName", 0), is(false));
      assertThat(Validator.isSetter("withoutName", 1), is(true));
      assertThat(Validator.isSetter("withoutName", 2), is(false));

      assertThat(Validator.isSetter("set", 1), is(false));
      assertThat(Validator.isSetter("with", 1), is(false));
      assertThat(Validator.isSetter("without", 1), is(false));
      assertThat(Validator.isSetter("settings", 1), is(false));
      assertThat(Validator.isSetter("withdraw", 1), is(false));
      assertThat(Validator.isSetter("withouten", 1), is(false));

   }

   @Test
   void isGetter()
   {
      assertThat(Validator.isGetter("getName", 0), is(true));
      assertThat(Validator.isGetter("getName", 1), is(false));
      assertThat(Validator.isGetter("get_id", 0), is(true));
      assertThat(Validator.isGetter("get_id", 1), is(false));
      assertThat(Validator.isGetter("_initName", 0), is(true));
      assertThat(Validator.isGetter("_initName", 1), is(false));
      assertThat(Validator.isGetter("nameProperty", 0), is(true));
      assertThat(Validator.isGetter("nameProperty", 1), is(false));

      assertThat(Validator.isGetter("get", 0), is(false));
      assertThat(Validator.isGetter("getaway", 0), is(false));
      assertThat(Validator.isGetter("_init", 0), is(false));
      assertThat(Validator.isGetter("_initialize", 0), is(false));
   }
}
