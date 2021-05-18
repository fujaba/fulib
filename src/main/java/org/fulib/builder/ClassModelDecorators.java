package org.fulib.builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for specifying which classes in a package are {@link ClassModelDecorator}s.
 *
 * @since 1.6
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface ClassModelDecorators
{
   /**
    * @return the classes in this package that implement {@link ClassModelDecorator}
    */
   Class<? extends ClassModelDecorator>[] value();
}
