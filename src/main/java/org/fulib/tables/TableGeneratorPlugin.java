package org.fulib.tables;

import org.fulib.ClassModelGenerator;
import org.fulib.Plugin;
import org.fulib.TablesGenerator;

/**
 * A plugin that uses registers the {@link TablesGenerator}.
 *
 * @since 1.6
 */
public class TableGeneratorPlugin implements Plugin<ClassModelGenerator>
{
   @Override
   public void apply(ClassModelGenerator component)
   {
      component.withGenerator(new TablesGenerator());
   }
}
