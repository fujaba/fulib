package org.fulib.classmodel.util;

import org.sdmlib.models.pattern.PatternObject;
import org.fulib.classmodel.CodeFragment;
import org.sdmlib.models.pattern.AttributeConstraint;
import org.sdmlib.models.pattern.Pattern;

public class CodeFragmentPO extends PatternObject<CodeFragmentPO, CodeFragment>
{

    public CodeFragmentSet allMatches()
   {
      this.setDoAllMatches(true);
      
      CodeFragmentSet matches = new CodeFragmentSet();

      while (this.getPattern().getHasMatch())
      {
         matches.add((CodeFragment) this.getCurrentMatch());
         
         this.getPattern().findMatch();
      }
      
      return matches;
   }


   public CodeFragmentPO(){
      newInstance(null);
   }

   public CodeFragmentPO(CodeFragment... hostGraphObject) {
      if(hostGraphObject==null || hostGraphObject.length<1){
         return ;
      }
      newInstance(null, hostGraphObject);
   }

   public CodeFragmentPO(String modifier)
   {
      this.setModifier(modifier);
   }
   public CodeFragmentPO createKeyCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(CodeFragment.PROPERTY_KEY)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public CodeFragmentPO createKeyCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(CodeFragment.PROPERTY_KEY)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public CodeFragmentPO createKeyAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(CodeFragment.PROPERTY_KEY)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getKey()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((CodeFragment) getCurrentMatch()).getKey();
      }
      return null;
   }
   
   public CodeFragmentPO withKey(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((CodeFragment) getCurrentMatch()).setKey(value);
      }
      return this;
   }
   
   public CodeFragmentPO createTextCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(CodeFragment.PROPERTY_TEXT)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public CodeFragmentPO createTextCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(CodeFragment.PROPERTY_TEXT)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public CodeFragmentPO createTextAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(CodeFragment.PROPERTY_TEXT)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getText()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((CodeFragment) getCurrentMatch()).getText();
      }
      return null;
   }
   
   public CodeFragmentPO withText(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((CodeFragment) getCurrentMatch()).setText(value);
      }
      return this;
   }
   
}
