package org.fulib.classmodel;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FMethodTest
{
   @Test
   void setDeclaration()
   {
      final Clazz clazz = new Clazz().setName("Foo");

      final FMethod simple = new FMethod().setClazz(clazz);
      simple.setDeclaration("void foo()");
      assertThat(simple.getModifiers(), equalTo(""));
      assertThat(simple.getReturnType(), equalTo("void"));
      assertThat(simple.getAnnotations(), emptyString());
      assertThat(simple.getName(), equalTo("foo"));
      assertThat(simple.getParams(), anEmptyMap());
      assertThat(simple.getSignature(), equalTo("class/Foo/method/foo()"));

      final FMethod oneParam = new FMethod().setClazz(clazz);
      oneParam.setDeclaration("@NonNull private String bar(String par1)");
      assertThat(oneParam.getModifiers(), equalTo("private"));
      assertThat(oneParam.getReturnType(), equalTo("String"));
      assertThat(oneParam.getAnnotations(), equalTo("@NonNull"));
      assertThat(oneParam.getName(), equalTo("bar"));
      assertThat(oneParam.getParams(), aMapWithSize(1));
      assertThat(oneParam.getParams(), hasEntry("par1", "String"));
      assertThat(oneParam.getSignature(), equalTo("class/Foo/method/bar(String)"));

      final FMethod twoParams = new FMethod().setClazz(clazz);
      twoParams.setDeclaration("@Override @NonNull protected synchronized String baz(String par1, int par2)");
      assertThat(twoParams.getModifiers(), equalTo("protected synchronized"));
      assertThat(twoParams.getReturnType(), equalTo("String"));
      assertThat(twoParams.getAnnotations(), equalTo("@Override @NonNull"));
      assertThat(twoParams.getName(), equalTo("baz"));
      assertThat(twoParams.getParams(), aMapWithSize(2));
      assertThat(twoParams.getParams(), hasEntry("par1", "String"));
      assertThat(twoParams.getParams(), hasEntry("par2", "int"));
      assertThat(twoParams.getSignature(), equalTo("class/Foo/method/baz(String,int)"));

      final FMethod varargs = new FMethod().setClazz(clazz);
      varargs.setDeclaration("public void varargs(String... args)");
      assertThat(varargs.getModifiers(), equalTo("public"));
      assertThat(varargs.getReturnType(), equalTo("void"));
      assertThat(varargs.getName(), equalTo("varargs"));
      assertThat(varargs.getParams(), aMapWithSize(1));
      assertThat(varargs.getParams(), hasEntry("args", "String..."));
      assertThat(varargs.getSignature(), equalTo("class/Foo/method/varargs(String...)"));

      final FMethod parametricTypes = new FMethod().setClazz(clazz);
      parametricTypes.setDeclaration(
         "public Map<String, Integer> parametricTypes(List<Integer> ints, Map<Integer, Map<Integer, String>> matrix)");
      assertThat(parametricTypes.getModifiers(), equalTo("public"));
      assertThat(parametricTypes.getReturnType(), equalTo("Map<String, Integer>"));
      assertThat(parametricTypes.getName(), equalTo("parametricTypes"));
      assertThat(parametricTypes.getParams(), aMapWithSize(2));
      assertThat(parametricTypes.getParams(), hasEntry("ints", "List<Integer>"));
      assertThat(parametricTypes.getParams(), hasEntry("matrix", "Map<Integer, Map<Integer, String>>"));
      assertThat(parametricTypes.getSignature(),
                 equalTo("class/Foo/method/parametricTypes(List<Integer>,Map<Integer,Map<Integer,String>>)"));
   }

   @Test
   void setDeclaration_cStyleArrays()
   {
      final Clazz clazz = new Clazz().setName("Foo");

      final FMethod cStyleArrays = new FMethod().setClazz(clazz);
      cStyleArrays.setDeclaration("String cStyleArrays(String args[])[]");
      assertThat(cStyleArrays.getReturnType(), equalTo("String[]"));
      assertThat(cStyleArrays.getParams(), aMapWithSize(1));
      assertThat(cStyleArrays.getParams(), hasEntry("args", "String[]"));
      assertThat(cStyleArrays.getSignature(), equalTo("class/Foo/method/cStyleArrays(String[])"));

      final FMethod cStyleMultiArrays = new FMethod().setClazz(clazz);
      cStyleMultiArrays.setDeclaration("String cStyleMultiArrays(String args[][])[][]");
      assertThat(cStyleMultiArrays.getReturnType(), equalTo("String[][]"));
      assertThat(cStyleMultiArrays.getParams(), aMapWithSize(1));
      assertThat(cStyleMultiArrays.getParams(), hasEntry("args", "String[][]"));
      assertThat(cStyleMultiArrays.getSignature(), equalTo("class/Foo/method/cStyleMultiArrays(String[][])"));

      final FMethod cStyleMixedArrays = new FMethod().setClazz(clazz);
      cStyleMixedArrays.setDeclaration("String[] cStyleMixedArrays(String[] args[])[]");
      assertThat(cStyleMixedArrays.getReturnType(), equalTo("String[][]"));
      assertThat(cStyleMixedArrays.getParams(), aMapWithSize(1));
      assertThat(cStyleMixedArrays.getParams(), hasEntry("args", "String[][]"));
      assertThat(cStyleMixedArrays.getSignature(), equalTo("class/Foo/method/cStyleMixedArrays(String[][])"));
   }

   @Test
   void setDeclaration_syntaxErrors()
   {
      final FMethod syntaxErrors = new FMethod();

      final IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
         syntaxErrors.setDeclaration("void (x, y)");
      });

      assertThat(ex.getMessage(), equalTo("syntax errors in declaration:\n" + "void (x, y)\n"
                                          + "<unknown>:1:5: syntax: extraneous input '(' expecting IDENTIFIER\n"
                                          + "<unknown>:1:7: syntax: mismatched input ',' expecting {'@', '[', IDENTIFIER}\n"));
   }
}
