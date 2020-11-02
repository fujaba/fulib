package org.fulib.builder.reflect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the actual type for an attribute or association.
 * This is useful if the type is not available in the gen source set,
 * or when using a collection type that does not have a generic type argument.
 *
 * @since 1.4
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Type
{
   /**
    * @return the type
    */
   String value();
}
