package org.fulib.classmodel.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodel.Attribute;

public class AttributePOCreator extends PatternObjectCreator
{
   @Override
   public Object getSendableInstance(boolean reference)
   {
      if(reference) {
          return new AttributePO(new Attribute[]{});
      } else {
          return new AttributePO();
      }
   }
   
   public static IdMap createIdMap(String sessionID) {
      return org.fulib.classmodel.util.CreatorCreator.createIdMap(sessionID);
   }
}
