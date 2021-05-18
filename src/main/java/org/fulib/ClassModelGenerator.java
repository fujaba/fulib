package org.fulib;

import org.fulib.classmodel.ClassModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Provides a model for the code generation phase.
 *
 * @since 1.6
 */
public class ClassModelGenerator
{
   private final ClassModel classModel;
   private final List<Generator> generators = new ArrayList<>();

   public ClassModelGenerator(ClassModel classModel)
   {
      this.classModel = classModel;
   }

   public ClassModel getClassModel()
   {
      return this.classModel;
   }

   public List<Generator> getGenerators()
   {
      return Collections.unmodifiableList(this.generators);
   }

   public ClassModelGenerator withGenerator(Generator generator)
   {
      this.generators.add(generator);
      return this;
   }

   public ClassModelGenerator withoutGenerator(Generator generator)
   {
      this.generators.remove(generator);
      return this;
   }

   /**
    * Invokes all generators.
    *
    * @see Generator#generate(ClassModel)
    */
   public void generate()
   {
      for (final Generator generator : this.generators)
      {
         generator.generate(this.classModel);
      }
   }
}
