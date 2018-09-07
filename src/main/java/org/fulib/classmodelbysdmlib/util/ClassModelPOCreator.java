package org.fulib.classmodelbysdmlib.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodelbysdmlib.ClassModel;

public class ClassModelPOCreator extends PatternObjectCreator
{
   @Override
   public Object getSendableInstance(boolean reference)
   {
      if(reference) {
          return new ClassModelPO(new ClassModel[]{});
      } else {
          return new ClassModelPO();
      }
   }
   
   public static IdMap createIdMap(String sessionID) {
      return CreatorCreator.createIdMap(sessionID);
   }
}
