package org.fulib.emf;

import org.fulib.Plugin;
import org.fulib.builder.ClassModelManager;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Extends the class model with definitions from an {@code ecore} (Eclipse Modeling Framework) file.
 * Currently, the following definitions are supported:
 * <ul>
 *    <li>Classes, including Superclass</li>
 *    <li>Attributes, including collections</li>
 *    <li>Associations, including {@code containment}</li>
 * </ul>
 *
 * @since 1.6
 */
public class ECoreLoaderPlugin implements Plugin<ClassModelManager>
{
   private final String uri;

   /**
    * @param uri
    *    the URI of the Ecore file, e.g. {@code getClass().getResource("example.ecore").toString()}
    */
   public ECoreLoaderPlugin(String uri)
   {
      this.uri = uri;
   }

   @Override
   public void apply(ClassModelManager component)
   {
      try
      {
         new ECoreVisitor(component).load(uri);
      }
      catch (Exception ex)
      {
         Logger.getGlobal().log(Level.SEVERE, "could not parse ecore file", ex);
      }
   }
}
