package org.fulib.builder;

import org.fulib.ClassModelGenerator;

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

   /**
    * Hook for modifying the code generation phase.
    *
    * @param generator
    *    the generator
    */
   default void decorate(ClassModelGenerator generator)
   {
   }
}
