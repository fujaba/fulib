package org.fulib;

/**
 * A generic plugin.
 *
 * @param <T>
 *    The type of component this plugin can be used with.
 *
 * @since 1.6
 */
public interface Plugin<T>
{
   /**
    * Applies this plugin to the component.
    *
    * @param component
    *    the target component
    */
   void apply(T component);
}
