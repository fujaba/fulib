package org.fulib.builder.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Allows creating Data Transfer Object (DTO) classes by copying attributes from a model class.
 * Associations are automatically converted to String attributes meant for holding the ID of the link target.
 *
 * @since 1.6
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DTO
{
   /**
    * @return the model class to copy attributes from
    */
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
