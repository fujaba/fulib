package org.fulib.builder;

import org.fulib.classmodel.ClassModel;
import org.fulib.yaml.EventFiler;
import org.fulib.yaml.EventSource;
import org.fulib.yaml.Yamler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.function.Consumer;

import static org.fulib.builder.ClassModelBuilder.COLLECTION_ARRAY_LIST;
import static org.fulib.builder.ClassModelBuilder.POJO;

public class ClassModelEventManager
{
   private final EventSource eventSource;
   private final ClassModelManager modelManager;
   private LinkedHashMap<String, Consumer<LinkedHashMap<String, String>>> consumerMap;



   public ClassModelEventManager()
   {
      this.modelManager = new ClassModelManager(this);

      this.eventSource = new EventSource();

      EventFiler eventFiler = new EventFiler(this.eventSource)
            .setHistoryFileName("tmp/classmodel/ClassModel.yaml");

      String yaml = eventFiler.loadHistory();
      this.applyEvents(yaml);

      eventFiler.startEventLogging();
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
      if (consumerMap == null)
      {
         consumerMap = new LinkedHashMap<>();

         consumerMap.put(ClassModelManager.HAVE_PACKAGE_NAME, map -> {
            String packageName = map.get(ClassModel.PROPERTY_packageName);
            modelManager.havePackageName(packageName);
         });

         consumerMap.put(ClassModelManager.HAVE_MAIN_JAVA_DIR, map -> {
            String sourceFolder = map.get(ClassModel.PROPERTY_mainJavaDir);
            modelManager.haveMainJavaDir(sourceFolder);
         });
      }

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
