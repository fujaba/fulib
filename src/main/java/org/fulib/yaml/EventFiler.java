package org.fulib.yaml;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventFiler
{
   String historyFileName = null;

   private EventSource eventSource;

   public EventFiler(EventSource eventSource)
   {
      this.eventSource = eventSource;
   }

   public EventFiler setHistoryFileName(String historyFileName)
   {
      this.historyFileName = historyFileName;
      return this;
   }

   public String loadHistory()
   {
      File historyFile = new File(historyFileName);
      String content = null;
      try
      {
         byte[] bytes = new byte[(int) historyFile.length()];
         InputStream inputStream = new FileInputStream(historyFile);
         int read = inputStream.read(bytes);
         content = new String(bytes);
      }
      catch (Exception e)
      {
         // Logger.getGlobal().log(Level.SEVERE, "could not load history", e);
      }

      return content;
   }


   public boolean storeHistory()
   {
      File historyFile = new File(historyFileName);

      String yaml = eventSource.encodeYaml();
      try {
         PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(historyFileName)));
         out.print(yaml);
         out.close();
      } catch (IOException e) {
         Logger.getGlobal().log(Level.SEVERE, "could not write to historyFile " + historyFileName, e);
         return false;
      }

      return true;
   }



   public EventFiler startEventLogging()
   {
      eventSource.addEventListener(map -> this.storeEvent(map));

      return this;
   }

   public void storeEvent(LinkedHashMap<String, String> event)
   {
      Objects.requireNonNull(historyFileName);
      int dirEndPos = historyFileName.lastIndexOf('/');
      if (dirEndPos > 0)
      {
         // mkdir
         String dirName = historyFileName.substring(0, dirEndPos);
         File dataDir = new File(dirName);
         if ( ! dataDir.exists())
         {
            dataDir.mkdirs();
         }
      }

      File historyFile = new File(historyFileName);
      if ( ! historyFile.exists())
      {
         try
         {
            historyFile.createNewFile();
         }
         catch (IOException e)
         {
            Logger.getGlobal().log(Level.SEVERE, "could not create historyFile " + historyFileName, e);
         }
      }

      String yaml = EventSource.encodeYaml(event);

      try {
         PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(historyFileName, true)));
         out.print(yaml);
         out.close();
      } catch (IOException e) {
         Logger.getGlobal().log(Level.SEVERE, "could not write to historyFile " + historyFileName, e);
      }
   }
}
