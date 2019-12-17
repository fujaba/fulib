package org.fulib.builder;

import org.fulib.Fulib;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ClassBuilderTest
{
   @ParameterizedTest
   @ValueSource(strings = { "org.extends.tools", // keyword
      "org.fulib.", ".org.fulib", "org fulib",
      // "org$fulib", // valid Java identifier
   })
   void testValidPackageNames(String packageName)
   {
      assertThrows(IllegalArgumentException.class, () -> Fulib.classModelBuilder(packageName));
   }

   @Test
   void testValidClassNames()
   {
      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");

      assertThrows(IllegalArgumentException.class, () -> mb.buildClass(null));
      assertThrows(IllegalArgumentException.class, () -> mb.buildClass(""));
   }

   @Test
   void testValidIdentifiers()
   {

      ClassModelBuilder mb = Fulib.classModelBuilder("org.fulib");
      ClassBuilder c1 = mb.buildClass("C1");

      assertThrows(IllegalArgumentException.class, () -> mb.buildClass("C1"));

      assertThrows(IllegalArgumentException.class, () -> c1.buildAttribute("42", Type.STRING));

      c1.buildAttribute("a42", Type.STRING);
      assertThrows(IllegalArgumentException.class, () -> c1.buildAttribute("a42", Type.STRING));

      Function<String, Boolean> f;
      c1.buildAttribute("myFunction", "java.util.function.Function<String,Boolean>");

      ClassBuilder c2 = mb.buildClass("C2");
      assertThrows(IllegalArgumentException.class, () -> c1.buildAssociation(c2, "a42", Type.MANY, "b", Type.MANY));

      assertThrows(IllegalArgumentException.class, () -> c1.buildAssociation(c1, "x", Type.MANY, "x", Type.ONE));

      c1.buildAssociation(c1, "x", Type.MANY, "x", Type.MANY);
   }

   @Test
   public void testForbiddenClasses()
   {
      ClassModelBuilder mb = new ClassModelBuilder("org.testFulib");
      assertThrows(Exception.class, () -> mb.buildClass("Object"));
      assertThrows(Exception.class, () -> mb.buildClass("String"));
      assertThrows(Exception.class, () -> mb.buildClass("Integer"));
   }
}
