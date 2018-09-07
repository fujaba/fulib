package org.fulib.classmodelbysdmlib.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodelbysdmlib.AssocRole;

public class AssocRolePOCreator extends PatternObjectCreator
{
   @Override
   public Object getSendableInstance(boolean reference)
   {
      if(reference) {
          return new AssocRolePO(new AssocRole[]{});
      } else {
          return new AssocRolePO();
      }
   }
   
   public static IdMap createIdMap(String sessionID) {
      return CreatorCreator.createIdMap(sessionID);
   }
}
