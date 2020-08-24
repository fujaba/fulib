package org.fulib.classmodel;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class FMethodTest
{
   @Test
   void setDeclaration()
   {
      final FMethod simple = new FMethod();
      simple.setDeclaration("void foo()");
      assertThat(simple.getModifiers(), equalTo(""));
      assertThat(simple.getReturnType(), equalTo("void"));
      assertThat(simple.getAnnotations(), emptyString());
      assertThat(simple.getName(), equalTo("foo"));
      assertThat(simple.getParams(), anEmptyMap());

      final FMethod oneParam = new FMethod();
      oneParam.setDeclaration("@NonNull private String bar(String par1)");
      assertThat(oneParam.getModifiers(), equalTo("private"));
      assertThat(oneParam.getReturnType(), equalTo("String"));
      assertThat(oneParam.getAnnotations(), equalTo("@NonNull"));
      assertThat(oneParam.getName(), equalTo("bar"));
      assertThat(oneParam.getParams(), aMapWithSize(1));
      assertThat(oneParam.getParams(), hasEntry("par1", "String"));

      final FMethod twoParams = new FMethod();
      twoParams.setDeclaration("@Override @NonNull protected synchronized String baz(String par1, int par2)");
      assertThat(twoParams.getModifiers(), equalTo("protected synchronized"));
      assertThat(twoParams.getReturnType(), equalTo("String"));
      assertThat(twoParams.getAnnotations(), equalTo("@Override @NonNull"));
      assertThat(twoParams.getName(), equalTo("baz"));
      assertThat(twoParams.getParams(), aMapWithSize(2));
      assertThat(twoParams.getParams(), hasEntry("par1", "String"));
      assertThat(twoParams.getParams(), hasEntry("par2", "int"));

      final FMethod varargs = new FMethod();
      varargs.setDeclaration("public void varargs(String... args)");
      assertThat(varargs.getModifiers(), equalTo("public"));
      assertThat(varargs.getReturnType(), equalTo("void"));
      assertThat(varargs.getName(), equalTo("varargs"));
      assertThat(varargs.getParams(), aMapWithSize(1));
      assertThat(varargs.getParams(), hasEntry("args", "String..."));

      final FMethod parametricTypes = new FMethod();
      parametricTypes.setDeclaration(
         "public Map<String, Integer> parametricTypes(List<Integer> ints, Map<Integer, Map<Integer, String>> matrix)");
      assertThat(parametricTypes.getModifiers(), equalTo("public"));
      assertThat(parametricTypes.getReturnType(), equalTo("Map<String, Integer>"));
      assertThat(parametricTypes.getName(), equalTo("parametricTypes"));
      assertThat(parametricTypes.getParams(), aMapWithSize(2));
      assertThat(parametricTypes.getParams(), hasEntry("ints", "List<Integer>"));
      assertThat(parametricTypes.getParams(), hasEntry("matrix", "Map<Integer, Map<Integer, String>>"));
   }

   @Test
   void setDeclaration_cStyleArrays()
   {
      final FMethod cStyleArrays = new FMethod();
      cStyleArrays.setDeclaration("String cStyleArrays(String args[])[]");
      assertThat(cStyleArrays.getReturnType(), equalTo("String[]"));
      assertThat(cStyleArrays.getParams(), aMapWithSize(1));
      assertThat(cStyleArrays.getParams(), hasEntry("args", "String[]"));

      final FMethod cStyleMultiArrays = new FMethod();
      cStyleMultiArrays.setDeclaration("String cStyleMultiArrays(String args[][])[][]");
      assertThat(cStyleMultiArrays.getReturnType(), equalTo("String[][]"));
      assertThat(cStyleMultiArrays.getParams(), aMapWithSize(1));
      assertThat(cStyleMultiArrays.getParams(), hasEntry("args", "String[][]"));

      final FMethod cStyleMixedArrays = new FMethod();
      cStyleMixedArrays.setDeclaration("String[] cStyleMixedArrays(String[] args[])[]");
      assertThat(cStyleMixedArrays.getReturnType(), equalTo("String[][]"));
      assertThat(cStyleMixedArrays.getParams(), aMapWithSize(1));
      assertThat(cStyleMixedArrays.getParams(), hasEntry("args", "String[][]"));
   }
}
