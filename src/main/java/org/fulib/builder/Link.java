package org.fulib.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a link for two association roles.
 *
 * @since 1.4
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Link
{
   /**
    * @return the name of the attribute in the other class.
    */
   String value();
}
