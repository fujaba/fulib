package org.fulib.yaml;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class EventSource
{
   public static final String EVENT_KEY = ".eventKey";
   public static final String EVENT_TIMESTAMP = ".eventTimestamp";
   public static final String EVENT_TYPE = "eventType";
   private Yamler yamler = new Yamler();

   private LinkedHashMap<String,Long> keyNumMap = new LinkedHashMap<>();
   private TreeMap<Long, LinkedHashMap<String, String>> numEventMap = new TreeMap<>();
   
   private long lastEventTime;

   public long getLastEventTime() {
      return lastEventTime;
   }

   public SortedMap<Long, LinkedHashMap<String, String>> pull(long since)
   {
      SortedMap<Long, LinkedHashMap<String, String>> tailMap = numEventMap.tailMap(since);
      return tailMap;
   }

   public SortedMap<Long, LinkedHashMap<String, String>> pull(long since, String... relevantEventTypes)
   {
      return pull(since, e -> filterRelevantEventTypes(e, Arrays.asList(relevantEventTypes)));
   }



   private Boolean filterRelevantEventTypes(Map.Entry<Long, LinkedHashMap<String, String>> e, List<String> relevantEventTypes)
   {
      LinkedHashMap<String, String> map = e.getValue();
      return relevantEventTypes.contains(map.get(EVENT_TYPE));
   }



   public SortedMap<Long, LinkedHashMap<String, String>> pull(long since, Function<Map.Entry<Long, LinkedHashMap<String, String>>,Boolean> filterOp)
   {
      SortedMap<Long, LinkedHashMap<String, String>> tailMap = numEventMap.tailMap(since);
      TreeMap<Long, LinkedHashMap<String, String>> resultMap = new TreeMap<>();
      for (Map.Entry<Long, LinkedHashMap<String, String>> entry : tailMap.entrySet())
      {
         boolean result = filterOp.apply(entry);

         if (result)
         {
            resultMap.put(entry.getKey(), entry.getValue());
         }
      }

      return resultMap;
   }



   public LinkedHashMap<String, String> getEvent(String eventKey)
   {
      Long aLong = keyNumMap.get(eventKey);

      if (aLong == null) return null; //======================

      LinkedHashMap<String, String> map = numEventMap.get(aLong);

      return map;
   }



   public boolean isOverwritten(LinkedHashMap<String, String> map)
   {
      String eventKey = map.get(EVENT_KEY);
      String eventTimeTxt = map.get(EVENT_TIMESTAMP);

      Long storedTime = keyNumMap.get(eventKey);

      if (storedTime == null) return false;

      String storedTimeTxt = dateFormat.format(storedTime);

      return storedTimeTxt.compareTo(eventTimeTxt) >= 0;
   }


   private long oldEventTimeStamp = 0;

   public DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

   public EventSource setOldEventTimeStamp(String oldTimeStampString)
   {
      if (oldTimeStampString == null) return this; //========================

      long oldTimeStamp = 0;
      try
      {
         oldTimeStamp = dateFormat.parse(oldTimeStampString).getTime();
      }
      catch (ParseException e)
      {
         e.printStackTrace();
      }

      return setOldEventTimeStamp(oldTimeStamp);
   }


   public EventSource setOldEventTimeStamp(long oldEventTimeStamp)
   {
      this.oldEventTimeStamp = oldEventTimeStamp;
      return this;
   }


   public EventSource append(LinkedHashMap<String, String> event)
   {
      String timestampString;

      setOldEventTimeStamp(event.get(EVENT_TIMESTAMP));

      if (oldEventTimeStamp > lastEventTime)
      {
         lastEventTime = oldEventTimeStamp;
      }
      else
      {
         lastEventTime = System.currentTimeMillis();
      }
      timestampString = dateFormat.format(lastEventTime);
      oldEventTimeStamp = 0;

      event.put(EVENT_TIMESTAMP, timestampString);
      
      String key = event.get(EVENT_KEY);
      if (key != null)
      {
         Long oldNum = keyNumMap.get(key);
         if (oldNum != null) numEventMap.remove(oldNum);
      }

      keyNumMap.put(key, lastEventTime);
      numEventMap.put(lastEventTime, event);

      for (Consumer<LinkedHashMap<String, String>> listener : eventListeners)
      {
         listener.accept(event);
      }

      return this;
   }


   public EventSource append(String buf)
   {
      if (buf == null) return this; //===========================================

      ArrayList<LinkedHashMap<String, String>> list = yamler.decodeList(buf);

      for (LinkedHashMap<String, String> event : list)
      {
         append(event);
      }

      return this;
   }

   public String encodeYaml()
   {
      return encodeYaml(numEventMap);
   }

   public static String encodeYaml(SortedMap<Long, LinkedHashMap<String, String>> eventMap)
   {
      StringBuffer buf = new StringBuffer();

      for (Map.Entry<Long, LinkedHashMap<String, String>> entry : eventMap.entrySet())
      {
         LinkedHashMap<String, String> event = entry.getValue();

         String oneObj = encodeYaml(event);

         buf.append(oneObj);
      }

      return buf.toString();
   }
   
   public static String encodeYaml(List<LinkedHashMap<String, String>> events)
   {
      StringBuffer buf = new StringBuffer();
      
      for (LinkedHashMap<String, String> event : events) {
         String oneObj = encodeYaml(event);
         buf.append(oneObj);
      }
      
      return buf.toString();
   }

   public static String encodeYaml(LinkedHashMap<String, String> event)
   {
      StringBuffer buf = new StringBuffer();

      String prefix = "- ";
      for (Map.Entry<String, String> keyValuePair : event.entrySet())
      {
         buf.append(prefix).append(keyValuePair.getKey()).append(": ")
               .append(Yamler.encapsulate(keyValuePair.getValue())).append("\n");
         prefix = "  ";
      }
      buf.append("\n");

      return buf.toString();
   }

   private ArrayList<Consumer<LinkedHashMap<String,String>>> eventListeners = new ArrayList<>();

   public void addEventListener(Consumer<LinkedHashMap<String,String>> listener)
   {
      eventListeners.add(listener);
   }

}
