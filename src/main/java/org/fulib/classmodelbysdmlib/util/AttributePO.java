package org.fulib.classmodelbysdmlib.util;

import org.sdmlib.models.pattern.PatternObject;
import org.fulib.classmodelbysdmlib.Attribute;
import org.sdmlib.models.pattern.AttributeConstraint;
import org.sdmlib.models.pattern.Pattern;
import org.fulib.classmodelbysdmlib.Clazz;

public class AttributePO extends PatternObject<AttributePO, Attribute>
{

    public AttributeSet allMatches()
   {
      this.setDoAllMatches(true);
      
      AttributeSet matches = new AttributeSet();

      while (this.getPattern().getHasMatch())
      {
         matches.add((Attribute) this.getCurrentMatch());
         
         this.getPattern().findMatch();
      }
      
      return matches;
   }


   public AttributePO(){
      newInstance(null);
   }

   public AttributePO(Attribute... hostGraphObject) {
      if(hostGraphObject==null || hostGraphObject.length<1){
         return ;
      }
      newInstance(null, hostGraphObject);
   }

   public AttributePO(String modifier)
   {
      this.setModifier(modifier);
   }
   public AttributePO createInitializationCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(Attribute.PROPERTY_INITIALIZATION)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AttributePO createInitializationCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(Attribute.PROPERTY_INITIALIZATION)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AttributePO createInitializationAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(Attribute.PROPERTY_INITIALIZATION)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getInitialization()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Attribute) getCurrentMatch()).getInitialization();
      }
      return null;
   }
   
   public AttributePO withInitialization(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((Attribute) getCurrentMatch()).setInitialization(value);
      }
      return this;
   }
   
   public AttributePO createNameCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(Attribute.PROPERTY_NAME)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AttributePO createNameCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(Attribute.PROPERTY_NAME)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AttributePO createNameAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(Attribute.PROPERTY_NAME)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getName()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Attribute) getCurrentMatch()).getName();
      }
      return null;
   }
   
   public AttributePO withName(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((Attribute) getCurrentMatch()).setName(value);
      }
      return this;
   }
   
   public AttributePO createTypeCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(Attribute.PROPERTY_TYPE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AttributePO createTypeCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(Attribute.PROPERTY_TYPE)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AttributePO createTypeAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(Attribute.PROPERTY_TYPE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getType()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Attribute) getCurrentMatch()).getType();
      }
      return null;
   }
   
   public AttributePO withType(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((Attribute) getCurrentMatch()).setType(value);
      }
      return this;
   }
   
   public ClazzPO createClazzPO()
   {
      ClazzPO result = new ClazzPO(new Clazz[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(Attribute.PROPERTY_CLAZZ, result);
      
      return result;
   }

   public ClazzPO createClazzPO(String modifier)
   {
      ClazzPO result = new ClazzPO(new Clazz[]{});
      
      result.setModifier(modifier);
      super.hasLink(Attribute.PROPERTY_CLAZZ, result);
      
      return result;
   }

   public AttributePO createClazzLink(ClazzPO tgt)
   {
      return hasLinkConstraint(tgt, Attribute.PROPERTY_CLAZZ);
   }

   public AttributePO createClazzLink(ClazzPO tgt, String modifier)
   {
      return hasLinkConstraint(tgt, Attribute.PROPERTY_CLAZZ, modifier);
   }

   public Clazz getClazz()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Attribute) this.getCurrentMatch()).getClazz();
      }
      return null;
   }

}
