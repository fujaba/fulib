package org.fulib.classmodel;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class AttributeTest
{
   @Test
   @SuppressWarnings("deprecation") // the getTypeSignature method is only deprecated for external uses
   void setType()
   {
      final Attribute primitive = new Attribute();
      primitive.setType("int");
      assertThat(primitive.getTypeSignature(), equalTo("int"));

      final Attribute simple = new Attribute();
      simple.setType("String");
      assertThat(simple.getTypeSignature(), equalTo("String"));

      final Attribute generic = new Attribute();
      generic.setType("List<String>");
      assertThat(generic.getTypeSignature(), equalTo("List<String>"));

      final Attribute generic2 = new Attribute();
      generic2.setType("Map<String, Integer>");
      assertThat(generic2.getTypeSignature(), equalTo("Map<String,Integer>"));

      final Attribute annotated = new Attribute();
      annotated.setType("Map<String, @NonNull Object>");
      assertThat(annotated.getTypeSignature(), equalTo("Map<String,Object>"));
   }
}
