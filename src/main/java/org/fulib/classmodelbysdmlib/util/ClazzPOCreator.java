package org.fulib.classmodelbysdmlib.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodelbysdmlib.Clazz;

public class ClazzPOCreator extends PatternObjectCreator
{
   @Override
   public Object getSendableInstance(boolean reference)
   {
      if(reference) {
          return new ClazzPO(new Clazz[]{});
      } else {
          return new ClazzPO();
      }
   }
   
   public static IdMap createIdMap(String sessionID) {
      return CreatorCreator.createIdMap(sessionID);
   }
}
