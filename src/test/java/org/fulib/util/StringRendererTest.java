package org.fulib.util;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

public class StringRendererTest
{

   @Test
   void toUpperSnakeCase()
   {
      FulibStringRenderer renderer = new FulibStringRenderer();

      assertThat(renderer.toString("defaultCollectionType", "upper_snake", Locale.ENGLISH), is("DEFAULT_COLLECTION_TYPE"));
      assertThat(renderer.toString("name", "upper_snake", Locale.ENGLISH), is("NAME"));
      assertThat(renderer.toString("imgURL", "upper_snake", Locale.ENGLISH), is("IMG_URL"));
      assertThat(renderer.toString("default_collection", "upper_snake", Locale.ENGLISH), is("DEFAULT_COLLECTION"));
      assertThat(renderer.toString("routeV4", "upper_snake", Locale.ENGLISH), is("ROUTE_V4"));
      assertThat(renderer.toString("routeV4Handler", "upper_snake", Locale.ENGLISH), is("ROUTE_V4_HANDLER"));
   }
}
