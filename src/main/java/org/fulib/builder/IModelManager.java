package org.fulib.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public interface IModelManager
{
   /**
    * @param registry
    *    the map from event type to consumer
    *
    * @since 1.2
    */
   default void initConsumers(Map<String, Consumer<? super Map<String, String>>> registry)
   {
      final LinkedHashMap<String, Consumer<LinkedHashMap<String, String>>> consumerMap = new LinkedHashMap<>();
      this.initConsumers(consumerMap);

      for (final Map.Entry<String, Consumer<LinkedHashMap<String, String>>> entry : consumerMap.entrySet())
      {
         final Consumer<LinkedHashMap<String, String>> value = entry.getValue();

         final Consumer<Map<String, String>> bridge = m -> {
            final LinkedHashMap<String, String> linkedMap = m instanceof LinkedHashMap ?
               (LinkedHashMap<String, String>) m :
               new LinkedHashMap<>(m);
            value.accept(linkedMap);
         };

         registry.put(entry.getKey(), bridge);
      }
   }

   /**
    * @param consumerMap
    *    the map from event type to consumer
    *
    * @deprecated since 1.2; use {@link #initConsumers(Map)} instead
    */
   @Deprecated
   void initConsumers(LinkedHashMap<String, Consumer<LinkedHashMap<String, String>>> consumerMap);
}
