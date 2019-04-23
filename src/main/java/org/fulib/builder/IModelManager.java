package org.fulib.builder;

import java.util.LinkedHashMap;
import java.util.function.Consumer;

public interface IModelManager
{
   void initConsumers(LinkedHashMap<String, Consumer<LinkedHashMap<String, String>>> consumerMap);
}
