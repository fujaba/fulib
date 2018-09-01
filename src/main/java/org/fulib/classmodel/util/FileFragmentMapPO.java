package org.fulib.classmodel.util;

import org.sdmlib.models.pattern.PatternObject;
import org.fulib.classmodel.FileFragmentMap;
import org.sdmlib.models.pattern.AttributeConstraint;
import org.sdmlib.models.pattern.Pattern;

public class FileFragmentMapPO extends PatternObject<FileFragmentMapPO, FileFragmentMap>
{

    public FileFragmentMapSet allMatches()
   {
      this.setDoAllMatches(true);
      
      FileFragmentMapSet matches = new FileFragmentMapSet();

      while (this.getPattern().getHasMatch())
      {
         matches.add((FileFragmentMap) this.getCurrentMatch());
         
         this.getPattern().findMatch();
      }
      
      return matches;
   }


   public FileFragmentMapPO(){
      newInstance(null);
   }

   public FileFragmentMapPO(FileFragmentMap... hostGraphObject) {
      if(hostGraphObject==null || hostGraphObject.length<1){
         return ;
      }
      newInstance(null, hostGraphObject);
   }

   public FileFragmentMapPO(String modifier)
   {
      this.setModifier(modifier);
   }
   public FileFragmentMapPO createFileNameCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(FileFragmentMap.PROPERTY_FILENAME)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public FileFragmentMapPO createFileNameCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(FileFragmentMap.PROPERTY_FILENAME)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public FileFragmentMapPO createFileNameAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(FileFragmentMap.PROPERTY_FILENAME)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getFileName()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((FileFragmentMap) getCurrentMatch()).getFileName();
      }
      return null;
   }
   
   public FileFragmentMapPO withFileName(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((FileFragmentMap) getCurrentMatch()).setFileName(value);
      }
      return this;
   }
   
}
