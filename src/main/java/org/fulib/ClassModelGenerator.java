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
   private final List<AbstractGenerator> generators = new ArrayList<>();

   public ClassModelGenerator(ClassModel classModel)
   {
      this.classModel = classModel;
   }

   public ClassModel getClassModel()
   {
      return this.classModel;
   }

   public List<AbstractGenerator> getGenerators()
   {
      return Collections.unmodifiableList(this.generators);
   }

   public ClassModelGenerator withGenerator(AbstractGenerator generator)
   {
      this.generators.add(generator);
      return this;
   }

   public ClassModelGenerator withoutGenerator(AbstractGenerator generator)
   {
      this.generators.remove(generator);
      return this;
   }

   /**
    * Applies the given plugin to this generator.
    *
    * @param plugin
    *    the plugin to apply
    */
   public void apply(Plugin<? super ClassModelGenerator> plugin)
   {
      plugin.apply(this);
   }

   /**
    * Invokes all generators.
    *
    * @see AbstractGenerator#generate(ClassModel)
    */
   public void generate()
   {
      for (final AbstractGenerator generator : this.generators)
      {
         generator.generate(this.classModel);
      }
   }
}
