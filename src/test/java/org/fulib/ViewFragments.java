package org.fulib;

import org.fulib.classmodel.CodeFragment;
import org.fulib.classmodel.FileFragmentMap;
import org.fulib.parser.FragmentMapBuilder;

import java.io.IOException;

public class ViewFragments
{
   public static void main(String[] args) throws IOException
   {
      final FileFragmentMap map = FragmentMapBuilder.parse(args[0]);

      System.out.println("/////////////// " + map.getFileName() + " ///////////////");

      for (final CodeFragment fragment : map.getFragmentList())
      {
         System.out.println("// --------------- " + fragment.getKey() + " ---------------");
         System.out.print(fragment.getText());
      }

      System.out.println();
   }
}
