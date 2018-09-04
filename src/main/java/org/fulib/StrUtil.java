package org.fulib;

import java.util.Objects;

public class StrUtil
{
   public static String cap(String oldTxt)
   {
      Objects.requireNonNull(oldTxt);
      return oldTxt.substring(0,1).toUpperCase() + oldTxt.substring(1);
   }
}
