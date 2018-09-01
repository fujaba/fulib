package org.fulib.classmodel.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodel.CodeFragment;

public class CodeFragmentPOCreator extends PatternObjectCreator
{
   @Override
   public Object getSendableInstance(boolean reference)
   {
      if(reference) {
          return new CodeFragmentPO(new CodeFragment[]{});
      } else {
          return new CodeFragmentPO();
      }
   }
   
   public static IdMap createIdMap(String sessionID) {
      return org.fulib.classmodel.util.CreatorCreator.createIdMap(sessionID);
   }
}
