package org.fulib.classmodel.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodel.Clazz;

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
      return org.fulib.classmodel.util.CreatorCreator.createIdMap(sessionID);
   }
}
