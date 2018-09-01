package org.fulib.classmodel.util;

import org.sdmlib.models.pattern.PatternObject;
import org.fulib.classmodel.Clazz;
import org.sdmlib.models.pattern.AttributeConstraint;
import org.sdmlib.models.pattern.Pattern;
import org.fulib.classmodel.util.AttributePO;
import org.fulib.classmodel.Attribute;
import org.fulib.classmodel.util.ClazzPO;
import org.fulib.classmodel.util.AttributeSet;
import org.fulib.classmodel.util.ClassModelPO;
import org.fulib.classmodel.ClassModel;
import org.fulib.classmodel.util.AssocRolePO;
import org.fulib.classmodel.AssocRole;
import org.fulib.classmodel.util.AssocRoleSet;

public class ClazzPO extends PatternObject<ClazzPO, Clazz>
{

    public ClazzSet allMatches()
   {
      this.setDoAllMatches(true);
      
      ClazzSet matches = new ClazzSet();

      while (this.getPattern().getHasMatch())
      {
         matches.add((Clazz) this.getCurrentMatch());
         
         this.getPattern().findMatch();
      }
      
      return matches;
   }


   public ClazzPO(){
      newInstance(null);
   }

   public ClazzPO(Clazz... hostGraphObject) {
      if(hostGraphObject==null || hostGraphObject.length<1){
         return ;
      }
      newInstance(null, hostGraphObject);
   }

   public ClazzPO(String modifier)
   {
      this.setModifier(modifier);
   }
   public ClazzPO createNameCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(Clazz.PROPERTY_NAME)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClazzPO createNameCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(Clazz.PROPERTY_NAME)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClazzPO createNameAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(Clazz.PROPERTY_NAME)
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
         return ((Clazz) getCurrentMatch()).getName();
      }
      return null;
   }
   
   public ClazzPO withName(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((Clazz) getCurrentMatch()).setName(value);
      }
      return this;
   }
   
   public AttributePO createAttributesPO()
   {
      AttributePO result = new AttributePO(new Attribute[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(Clazz.PROPERTY_ATTRIBUTES, result);
      
      return result;
   }

   public AttributePO createAttributesPO(String modifier)
   {
      AttributePO result = new AttributePO(new Attribute[]{});
      
      result.setModifier(modifier);
      super.hasLink(Clazz.PROPERTY_ATTRIBUTES, result);
      
      return result;
   }

   public ClazzPO createAttributesLink(AttributePO tgt)
   {
      return hasLinkConstraint(tgt, Clazz.PROPERTY_ATTRIBUTES);
   }

   public ClazzPO createAttributesLink(AttributePO tgt, String modifier)
   {
      return hasLinkConstraint(tgt, Clazz.PROPERTY_ATTRIBUTES, modifier);
   }

   public AttributeSet getAttributes()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Clazz) this.getCurrentMatch()).getAttributes();
      }
      return null;
   }

   public ClassModelPO createModelPO()
   {
      ClassModelPO result = new ClassModelPO(new ClassModel[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(Clazz.PROPERTY_MODEL, result);
      
      return result;
   }

   public ClassModelPO createModelPO(String modifier)
   {
      ClassModelPO result = new ClassModelPO(new ClassModel[]{});
      
      result.setModifier(modifier);
      super.hasLink(Clazz.PROPERTY_MODEL, result);
      
      return result;
   }

   public ClazzPO createModelLink(ClassModelPO tgt)
   {
      return hasLinkConstraint(tgt, Clazz.PROPERTY_MODEL);
   }

   public ClazzPO createModelLink(ClassModelPO tgt, String modifier)
   {
      return hasLinkConstraint(tgt, Clazz.PROPERTY_MODEL, modifier);
   }

   public ClassModel getModel()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Clazz) this.getCurrentMatch()).getModel();
      }
      return null;
   }

   public AssocRolePO createRolesPO()
   {
      AssocRolePO result = new AssocRolePO(new AssocRole[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(Clazz.PROPERTY_ROLES, result);
      
      return result;
   }

   public AssocRolePO createRolesPO(String modifier)
   {
      AssocRolePO result = new AssocRolePO(new AssocRole[]{});
      
      result.setModifier(modifier);
      super.hasLink(Clazz.PROPERTY_ROLES, result);
      
      return result;
   }

   public ClazzPO createRolesLink(AssocRolePO tgt)
   {
      return hasLinkConstraint(tgt, Clazz.PROPERTY_ROLES);
   }

   public ClazzPO createRolesLink(AssocRolePO tgt, String modifier)
   {
      return hasLinkConstraint(tgt, Clazz.PROPERTY_ROLES, modifier);
   }

   public AssocRoleSet getRoles()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((Clazz) this.getCurrentMatch()).getRoles();
      }
      return null;
   }

}
