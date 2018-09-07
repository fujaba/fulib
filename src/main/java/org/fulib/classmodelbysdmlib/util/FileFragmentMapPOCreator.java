package org.fulib.classmodelbysdmlib.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import org.fulib.classmodelbysdmlib.FileFragmentMap;

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
