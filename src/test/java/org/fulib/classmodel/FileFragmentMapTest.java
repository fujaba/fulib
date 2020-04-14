package org.fulib.classmodel;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.emptyArray;
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

   @Test
   void getPath()
   {
      // assertThat(FileFragmentMap.getPath(""), emptyArray());
      assertThat(FileFragmentMap.getPath("org"), arrayContaining("org"));
      assertThat(FileFragmentMap.getPath("org/foo"), arrayContaining("org", "foo"));
      assertThat(FileFragmentMap.getPath("org/foo/bar"), arrayContaining("org", "foo", "bar"));
   }

   @Test
   void getParentKeys()
   {
      assertThat(FileFragmentMap.getParentKeys(""), emptyArray());
      assertThat(FileFragmentMap.getParentKeys("org"), emptyArray());
      assertThat(FileFragmentMap.getParentKeys("org/foo"), arrayContaining("org"));
      assertThat(FileFragmentMap.getParentKeys("org/foo/bar"), arrayContaining("org", "org/foo"));
      assertThat(FileFragmentMap.getParentKeys("org/foo/bar/baz"), arrayContaining("org", "org/foo", "org/foo/bar"));
   }
}
