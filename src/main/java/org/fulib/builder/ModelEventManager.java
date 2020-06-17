package org.fulib.builder;

import org.fulib.yaml.EventSource;
import org.fulib.yaml.Yamler;

import java.util.*;
import java.util.function.Consumer;

public class ModelEventManager
{
   // =============== Fields ===============

   private final EventSource eventSource;
   private IModelManager modelManager;

   private final Map<String, Consumer<? super Map<String, String>>> consumerMap = new HashMap<>();

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
      if (modelManager == this.modelManager)
      {
         return;
      }

      this.modelManager = modelManager;
      this.consumerMap.clear();

      if (modelManager != null)
      {
         modelManager.initConsumers(this.consumerMap);
      }
   }

   // =============== Methods ===============

   public void applyEvents(String yaml)
   {
      if (yaml == null)
      {
         return;
      }

      final Yamler yamler = new Yamler();
      final List<? extends Map<String, String>> list = yamler.decodeList(yaml);
      this.applyEvents(list);
   }

   /**
    * Applies all events, disregarding superseded ones.
    *
    * @param events
    *    the events
    */
   public void applyEvents(Iterable<? extends Map<String, String>> events)
   {
      for (Map<String, String> map : events)
      {
         if (this.eventSource.isOverwritten(map))
         {
            continue;
         }

         String oldTimeStampString = map.get(EventSource.EVENT_TIMESTAMP);

         this.eventSource.setOldEventTimeStamp(oldTimeStampString);

         String eventType = map.get(EventSource.EVENT_TYPE);
         Consumer<? super Map<String, String>> consumer = this.consumerMap.get(eventType);
         consumer.accept(map);
      }

      this.eventSource.setOldEventTimeStamp(0);
   }

   /**
    * @param events
    *    the list of events to apply
    *
    * @deprecated since 1.2; use {@link #applyEvents(Iterable)} instead
    */
   @Deprecated
   public void applyEvents(ArrayList<LinkedHashMap<String, String>> events)
   {
      this.applyEvents((Iterable<LinkedHashMap<String, String>>) events);
   }

   /**
    * Appends the event.
    *
    * @param event
    *    the event
    *
    * @since 1.2
    */
   public void append(Map<String, String> event)
   {
      this.eventSource.append(event);
   }

   /**
    * Appends the event.
    *
    * @param event
    *    the event
    *
    * @deprecated since 1.2; use {@link #append(Map)} instead
    */
   @Deprecated
   public void append(LinkedHashMap<String, String> event)
   {
      this.eventSource.append(event);
   }
}
