package org.fulib.classmodelbysdmlib.util;

import org.sdmlib.models.pattern.util.PatternObjectCreator;
import org.fulib.classmodelbysdmlib.CodeFragment;

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
   

}
