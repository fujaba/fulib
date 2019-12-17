package org.fulib.classmodel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileFragmentMapTest
{
   @Test
   void mergeClassDecl()
   {
      assertEquals("@Cool class Foo\n{", FileFragmentMap.mergeClassDecl("@Cool class Foo {", "class Foo {"));
      assertEquals("@Cool class Foo extends Bar\n{", FileFragmentMap.mergeClassDecl("@Cool class Foo {", "class Foo extends Bar {"));
      assertEquals("class Foo implements Serializable {",
                   FileFragmentMap.mergeClassDecl("class Foo implements Serializable {", "class Foo {"));
      assertEquals("class Foo extends Bar implements Baz {",
                   FileFragmentMap.mergeClassDecl("class Foo implements Baz {", "class Foo extends Bar {"));
   }
}
