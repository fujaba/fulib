package org.fulib.classmodel.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import de.uniks.networkparser.IdMap;
import org.fulib.classmodel.FileFragmentMap;

public class FileFragmentMapPOCreator extends PatternObjectCreator
{
   @Override
   public Object getSendableInstance(boolean reference)
   {
      if(reference) {
          return new FileFragmentMapPO(new FileFragmentMap[]{});
      } else {
          return new FileFragmentMapPO();
      }
   }
   

}
