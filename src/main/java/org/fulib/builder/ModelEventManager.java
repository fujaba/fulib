package org.fulib.builder;

import org.fulib.yaml.EventSource;
import org.fulib.yaml.Yamler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

public class ModelEventManager
{
   private final EventSource eventSource;
   private IModelManager modelManager;
   private LinkedHashMap<String, Consumer<LinkedHashMap<String, String>>> consumerMap;



   public ModelEventManager()
   {
      this.eventSource = new EventSource();

//      EventFiler eventFiler = new EventFiler(this.eventSource)
//            .setHistoryFileName("tmp/classmodel/ClassModel.yaml");
//
//      String yaml = eventFiler.loadHistory();
//      this.applyEvents(yaml);
//
//      eventFiler.startEventLogging();
   }


   public IModelManager getModelManager()
   {
      return modelManager;
   }

   public void setModelManager(IModelManager modelManager)
   {
      this.modelManager = modelManager;
   }



   // event handling
   public void applyEvents(String yaml)
   {
      if (yaml == null) return;

      Yamler yamler = new Yamler();
      ArrayList<LinkedHashMap<String, String>> list = yamler.decodeList(yaml);
      applyEvents(list);
   }




   public void applyEvents(ArrayList<LinkedHashMap<String, String>> events)
   {
      modelManager.initConsumers(consumerMap);

      // consume event list
      for (LinkedHashMap<String, String> map : events)
      {
         if (eventSource.isOverwritten(map)) continue;

         String oldTimeStampString = map.get(EventSource.EVENT_TIMESTAMP);

         eventSource.setOldEventTimeStamp(oldTimeStampString);

         String eventType = map.get(EventSource.EVENT_TYPE);
         Consumer<LinkedHashMap<String, String>> consumer = consumerMap.get(eventType);
         consumer.accept(map);
      }

      this.eventSource.setOldEventTimeStamp(0);
   }

   public void append(LinkedHashMap<String, String> event)
   {
      this.eventSource.append(event);
   }
}
