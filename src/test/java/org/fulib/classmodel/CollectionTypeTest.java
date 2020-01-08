package org.fulib.classmodel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionTypeTest
{
   private static class StringList extends ArrayList<String> {}

   private static class StringSet extends LinkedHashSet<String> {}

   private static class CustomList<T> extends ArrayList<T> {}
   
   private static class CustomSet<T> extends LinkedHashSet<T> {}

   private static class Generic2List<T, U> extends ArrayList<T> {}

   @Test
   public void testFactories()
   {
      final CollectionType stringList = CollectionType.of(StringList.class);
      assertEquals(CollectionItf.List, stringList.getItf());
      assertEquals(StringList.class.getName(), stringList.getImplTemplate());
      assertEquals(StringList.class, stringList.getImplClass());

      final CollectionType stringSet = CollectionType.of(StringSet.class);
      assertEquals(CollectionItf.Set, stringSet.getItf());
      assertEquals(StringSet.class.getName(), stringSet.getImplTemplate());
      assertEquals(StringSet.class, stringSet.getImplClass());

      final CollectionType customList = CollectionType.of(CustomList.class);
      assertEquals(CollectionItf.List, customList.getItf());
      assertEquals(CustomList.class.getName() + "<%s>", customList.getImplTemplate());
      assertEquals(CustomList.class, customList.getImplClass());

      final CollectionType customList2 = CollectionType.of(CustomSet.class.getName() + "<%s>");
      assertEquals(CollectionItf.Set, customList2.getItf());
      assertEquals(CustomSet.class.getName() + "<%s>", customList2.getImplTemplate());
      assertEquals(CustomSet.class, customList2.getImplClass());

      final CollectionType unloadedList = CollectionType.of("org.example.UnloadedList<%s>");
      assertEquals(CollectionItf.List, unloadedList.getItf());
      assertEquals("org.example.UnloadedList<%s>", unloadedList.getImplTemplate());
      assertNull(unloadedList.getImplClass());

      final CollectionType unloadedSet = CollectionType.of("org.example.UnloadedSet<%s>");
      assertEquals(CollectionItf.Set, unloadedSet.getItf());
      assertEquals("org.example.UnloadedSet<%s>", unloadedSet.getImplTemplate());
      assertNull(unloadedSet.getImplClass());
   }

   @SuppressWarnings( { "unchecked", "rawtypes" })
   @Test
   public void testIllegalCollectionClasses()
   {
      assertThrows(IllegalArgumentException.class, () -> CollectionType.of((Class) String.class));
      assertThrows(IllegalArgumentException.class, () -> CollectionType.of(String.class.getName()));
      assertThrows(IllegalArgumentException.class, () -> CollectionType.of(Generic2List.class));
      // assertThrows(IllegalArgumentException.class, () -> CollectionType.of(Generic2List.class.getName()));
   }

   @Test
   public void testConcurrentFactories()
   {
      testConcurrentCachedFactory(100000, () -> CollectionType.of(StringList.class));
      testConcurrentCachedFactory(100000, () -> CollectionType.of("org.example.UnloadedList<%s>"));
      testConcurrentCachedFactory(100000, () -> CollectionType.of("org.example.UnloadedSimpleSet"));
   }

   private static <T> void testConcurrentCachedFactory(int n, Supplier<T> supplier)
   {
      final Set<Integer> hashCodes = new ConcurrentSkipListSet<>();

      // have a bunch of threads call the supploer and check that each call returned the same instance
      IntStream.range(0, n).parallel().forEach(i -> {
         T type = supplier.get();
         final int hashCode = System.identityHashCode(type);
         hashCodes.add(hashCode);
      });

      T type = supplier.get();
      final int hashCode = System.identityHashCode(type);
      assertEquals(Collections.singleton(hashCode), hashCodes);
   }

   @Test
   public void testCachedSetterThrows()
   {
      final CollectionType cached = CollectionType.of(StringList.class);

      this.testCachedThrows(CollectionType.ArrayList);
      this.testCachedThrows(cached);
   }

   private void testCachedThrows(CollectionType cached)
   {
      assertThrows(UnsupportedOperationException.class, () -> cached.setItf(CollectionItf.Collection));
      assertThrows(UnsupportedOperationException.class, () -> cached.setImplTemplate("foo"));
      assertThrows(UnsupportedOperationException.class, () -> cached.setImplClass(ArrayList.class));
   }

   @Test
   public void testUncachedSetterThrowsNot()
   {
      this.testCachedThrowsNot(new CollectionType());
   }

   private void testCachedThrowsNot(CollectionType cached)
   {
      cached.setItf(CollectionItf.Collection);
      cached.setImplTemplate("foo");
      cached.setImplClass(ArrayList.class);
   }
}
