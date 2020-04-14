package org.fulib;

import org.fulib.classmodel.FileFragmentMap;
import org.fulib.parser.FragmentMapBuilder;

public class ViewFragments
{
   public static void main(String[] args)
   {
      final FileFragmentMap map = FragmentMapBuilder.parse(args[0]);

      System.out.println("/////////////// " + map.getFileName() + " ///////////////");

      map.codeFragments().forEach(fragment -> {
         System.out.println("// --------------- " + fragment.getKey() + " ---------------");
         System.out.print(fragment.getText());
      });

      System.out.println("/////////////// EOF ///////////////");
   }
}
