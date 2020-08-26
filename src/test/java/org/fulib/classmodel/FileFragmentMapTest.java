package org.fulib.classmodel;

import org.antlr.v4.runtime.CharStreams;
import org.fulib.parser.FragmentMapBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FileFragmentMapTest
{
   @Test
   void mergeClassDecl()
   {
      assertThat("it keeps annotations", FileFragmentMap.mergeClassDecl("@Cool class Foo {", "class Foo {"),
                 equalTo("@Cool class Foo {"));
      assertThat("it keeps interfaces",
                 FileFragmentMap.mergeClassDecl("class Foo implements Serializable {", "class Foo {"),
                 equalTo("class Foo implements Serializable {"));

      assertThat("it removes extends clause", FileFragmentMap.mergeClassDecl("class Foo extends Bar {", "class Foo {"),
                 equalTo("class Foo {"));
      assertThat("it removes extends clause and keeps interfaces",
                 FileFragmentMap.mergeClassDecl("class Foo extends Bar implements Baz {", "class Foo {"),
                 equalTo("class Foo implements Baz {"));

      assertThat("it adds extends clause before interface",
                 FileFragmentMap.mergeClassDecl("class Foo implements Baz {", "class Foo extends Bar {"),
                 equalTo("class Foo extends Bar implements Baz {"));
      assertThat("it adds extends clause before body",
                 FileFragmentMap.mergeClassDecl("class Foo {", "class Foo extends Bar {"),
                 equalTo("class Foo extends Bar {"));
      assertThat("it adds extends clause and keeps annotations",
                 FileFragmentMap.mergeClassDecl("@Cool class Foo {", "class Foo extends Bar {"),
                 equalTo("@Cool class Foo extends Bar {"));

      assertThat("it replaces the super type",
                 FileFragmentMap.mergeClassDecl("class Foo extends Moo {", "class Foo extends Bar {"),
                 equalTo("class Foo extends Bar {"));
      assertThat("it replaces the super type and keeps interfaces",
                 FileFragmentMap.mergeClassDecl("class Foo extends Moo implements Baz {", "class Foo extends Bar {"),
                 equalTo("class Foo extends Bar implements Baz {"));
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

   @Test
   void getFragment()
   {
      // language=JAVA
      final String example =
         "package org.example;\n" + "\n" + "import java.util.List;\n" + "import java.util.Map;\n" + "\n"
         + "class Example {\n" + "   int i;\n" + "   void foo() {}\n" + "   int j;\n" + "   void bar() {}\n"
         + "   void baz(String s) {}\n" + "   void baz(int i) {}\n" + "}\n";
      final FileFragmentMap map = FragmentMapBuilder.parse("Example.java", CharStreams.fromString(example));

      final String[] keys = {
         "package",
         "import/java.util.List",
         "import/java.util.Map",
         "class/Example/classDecl",
         "class/Example/attribute/i",
         "class/Example/method/foo()",
         "class/Example/attribute/j",
         "class/Example/method/bar()",
         "class/Example/method/baz(String)",
         "class/Example/method/baz(int)",
         "class/Example/classEnd",
         "eof",
      };
      final String[] texts = {
         "package org.example;",
         "import java.util.List;",
         "import java.util.Map;",
         "class Example {",
         "int i;",
         "void foo() {}",
         "int j;",
         "void bar() {}",
         "void baz(String s) {}",
         "void baz(int i) {}",
         "}",
         "",
      };

      for (int i = 0; i < keys.length; i++)
      {
         final CodeFragment fragment = map.getFragment(keys[i]);
         assertThat("fragment with key '" + keys[i] + "' exists", fragment, notNullValue());
         assertThat("fragment with key '" + keys[i] + "' has correct text", fragment.getText(), equalTo(texts[i]));
      }

      assertThat(map
                    .codeFragments()
                    .map(CodeFragment::getKey)
                    .filter(s -> !s.endsWith("#gap-before") && !s.endsWith("#start"))
                    .collect(Collectors.toList()), equalTo(Arrays.asList(keys)));
   }

   @ParameterizedTest()
   // language=JAVA
   @ValueSource(strings = {
      "class Empty {}",
      "class Empty {\n" + "}\n",
      "class Empty {\n" + "   // some comment\n" + "}\n",
      "package org.example;\n" + "\n" + "class Empty {\n" + "}\n",
      "package org.example;\n" + "\n" + "class Empty {\n" + "   // some comment\n" + "}\n",
   })
   void isClassBodyEmpty(String body)
   {
      final FileFragmentMap fragmentMap = FragmentMapBuilder.parse("Empty.java", CharStreams.fromString(body));
      assertThat(fragmentMap.isClassBodyEmpty(), is(true));
   }

   @ParameterizedTest()
   // language=JAVA
   @ValueSource(strings = {
      "class NotEmpty {int i;}",
      "class NotEmpty {\n" + "   int i;\n" + "}\n",
      "class NotEmpty {\n" + "   // some comment\n" + "   int i;\n" + "}\n",
      "package org.example;\n" + "\n" + "class NotEmpty {\n" + "   int i;\n" + "}\n",
      "package org.example;\n" + "\n" + "class NotEmpty {\n" + "   // some comment\n" + "   int i;\n" + "}\n",
   })
   void not_isClassBodyEmpty(String body)
   {
      final FileFragmentMap fragmentMap = FragmentMapBuilder.parse("NotClass.java", CharStreams.fromString(body));
      assertThat(fragmentMap.isClassBodyEmpty(), is(false));
   }
}
