package org.fulib.builder;

/**
 * ClassModelDecorator specifies hooks for various steps of the fulib lifecycle.
 *
 * @since 1.6
 */
public interface ClassModelDecorator
{
   /**
    * Hook for modifying the class meta model.
    *
    * @param m
    *    the class model manager
    */
   void decorate(ClassModelManager m);
}
