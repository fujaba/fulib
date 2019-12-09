package org.fulib.builder;

import org.fulib.yaml.EventSource;
import org.fulib.yaml.Yamler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

public class ModelEventManager
{
   // =============== Fields ===============

   private final EventSource   eventSource;
   private       IModelManager modelManager;

   private LinkedHashMap<String, Consumer<LinkedHashMap<String, String>>> consumerMap;

   // =============== Constructors ===============

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

   // =============== Properties ===============

   public IModelManager getModelManager()
   {
      return this.modelManager;
   }

   public void setModelManager(IModelManager modelManager)
   {
      this.modelManager = modelManager;
   }

   // =============== Methods ===============

   // event handling
   public void applyEvents(String yaml)
   {
      if (yaml == null)
      {
         return;
      }

      Yamler yamler = new Yamler();
      ArrayList<LinkedHashMap<String, String>> list = yamler.decodeList(yaml);
      this.applyEvents(list);
   }

   public void applyEvents(ArrayList<LinkedHashMap<String, String>> events)
   {
      this.modelManager.initConsumers(this.consumerMap);

      // consume event list
      for (LinkedHashMap<String, String> map : events)
      {
         if (this.eventSource.isOverwritten(map))
         {
            continue;
         }

         String oldTimeStampString = map.get(EventSource.EVENT_TIMESTAMP);

         this.eventSource.setOldEventTimeStamp(oldTimeStampString);

         String eventType = map.get(EventSource.EVENT_TYPE);
         Consumer<LinkedHashMap<String, String>> consumer = this.consumerMap.get(eventType);
         consumer.accept(map);
      }

      this.eventSource.setOldEventTimeStamp(0);
   }

   public void append(LinkedHashMap<String, String> event)
   {
      this.eventSource.append(event);
   }
}
