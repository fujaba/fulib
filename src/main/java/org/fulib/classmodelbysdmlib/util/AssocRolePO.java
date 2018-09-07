package org.fulib.classmodelbysdmlib.util;

import org.sdmlib.models.pattern.PatternObject;
import org.fulib.classmodelbysdmlib.AssocRole;
import org.sdmlib.models.pattern.AttributeConstraint;
import org.sdmlib.models.pattern.Pattern;
import org.fulib.classmodelbysdmlib.Clazz;

public class AssocRolePO extends PatternObject<AssocRolePO, AssocRole>
{

    public AssocRoleSet allMatches()
   {
      this.setDoAllMatches(true);
      
      AssocRoleSet matches = new AssocRoleSet();

      while (this.getPattern().getHasMatch())
      {
         matches.add((AssocRole) this.getCurrentMatch());
         
         this.getPattern().findMatch();
      }
      
      return matches;
   }


   public AssocRolePO(){
      newInstance(null);
   }

   public AssocRolePO(AssocRole... hostGraphObject) {
      if(hostGraphObject==null || hostGraphObject.length<1){
         return ;
      }
      newInstance(null, hostGraphObject);
   }

   public AssocRolePO(String modifier)
   {
      this.setModifier(modifier);
   }
   public AssocRolePO createCardinalityCondition(int value)
   {
      new AttributeConstraint()
      .withAttrName(AssocRole.PROPERTY_CARDINALITY)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AssocRolePO createCardinalityCondition(int lower, int upper)
   {
      new AttributeConstraint()
      .withAttrName(AssocRole.PROPERTY_CARDINALITY)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AssocRolePO createCardinalityAssignment(int value)
   {
      new AttributeConstraint()
      .withAttrName(AssocRole.PROPERTY_CARDINALITY)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public int getCardinality()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((AssocRole) getCurrentMatch()).getCardinality();
      }
      return 0;
   }
   
   public AssocRolePO withCardinality(int value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((AssocRole) getCurrentMatch()).setCardinality(value);
      }
      return this;
   }
   
   public AssocRolePO createNameCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(AssocRole.PROPERTY_NAME)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AssocRolePO createNameCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(AssocRole.PROPERTY_NAME)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AssocRolePO createNameAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(AssocRole.PROPERTY_NAME)
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
         return ((AssocRole) getCurrentMatch()).getName();
      }
      return null;
   }
   
   public AssocRolePO withName(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((AssocRole) getCurrentMatch()).setName(value);
      }
      return this;
   }
   
   public ClazzPO createClazzPO()
   {
      ClazzPO result = new ClazzPO(new Clazz[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(AssocRole.PROPERTY_CLAZZ, result);
      
      return result;
   }

   public ClazzPO createClazzPO(String modifier)
   {
      ClazzPO result = new ClazzPO(new Clazz[]{});
      
      result.setModifier(modifier);
      super.hasLink(AssocRole.PROPERTY_CLAZZ, result);
      
      return result;
   }

   public AssocRolePO createClazzLink(ClazzPO tgt)
   {
      return hasLinkConstraint(tgt, AssocRole.PROPERTY_CLAZZ);
   }

   public AssocRolePO createClazzLink(ClazzPO tgt, String modifier)
   {
      return hasLinkConstraint(tgt, AssocRole.PROPERTY_CLAZZ, modifier);
   }

   public Clazz getClazz()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((AssocRole) this.getCurrentMatch()).getClazz();
      }
      return null;
   }

   public AssocRolePO createOtherPO()
   {
      AssocRolePO result = new AssocRolePO(new AssocRole[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(AssocRole.PROPERTY_OTHER, result);
      
      return result;
   }

   public AssocRolePO createOtherPO(String modifier)
   {
      AssocRolePO result = new AssocRolePO(new AssocRole[]{});
      
      result.setModifier(modifier);
      super.hasLink(AssocRole.PROPERTY_OTHER, result);
      
      return result;
   }

   public AssocRolePO createOtherLink(AssocRolePO tgt)
   {
      return hasLinkConstraint(tgt, AssocRole.PROPERTY_OTHER);
   }

   public AssocRolePO createOtherLink(AssocRolePO tgt, String modifier)
   {
      return hasLinkConstraint(tgt, AssocRole.PROPERTY_OTHER, modifier);
   }

   public AssocRole getOther()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((AssocRole) this.getCurrentMatch()).getOther();
      }
      return null;
   }

   public AssocRolePO createRoleTypeCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(AssocRole.PROPERTY_ROLETYPE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AssocRolePO createRoleTypeCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(AssocRole.PROPERTY_ROLETYPE)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public AssocRolePO createRoleTypeAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(AssocRole.PROPERTY_ROLETYPE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getRoleType()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((AssocRole) getCurrentMatch()).getRoleType();
      }
      return null;
   }
   
   public AssocRolePO withRoleType(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((AssocRole) getCurrentMatch()).setRoleType(value);
      }
      return this;
   }
   
}
