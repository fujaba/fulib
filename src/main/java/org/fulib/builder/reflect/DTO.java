package org.fulib.builder.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DTO
{
   Class<?> model();

   /**
    * @return the names of fields that should be included. Ignored if empty.
    */
   String[] pick() default {};

   /**
    * @return the names of fields that should be excluded
    */
   String[] omit() default {};
}
