package org.fulib.classmodel.util;

import org.sdmlib.models.pattern.PatternObject;
import org.fulib.classmodel.ClassModel;
import org.sdmlib.models.pattern.AttributeConstraint;
import org.sdmlib.models.pattern.Pattern;
import org.fulib.classmodel.util.ClazzPO;
import org.fulib.classmodel.Clazz;
import org.fulib.classmodel.util.ClassModelPO;
import org.fulib.classmodel.util.ClazzSet;

public class ClassModelPO extends PatternObject<ClassModelPO, ClassModel>
{

    public ClassModelSet allMatches()
   {
      this.setDoAllMatches(true);
      
      ClassModelSet matches = new ClassModelSet();

      while (this.getPattern().getHasMatch())
      {
         matches.add((ClassModel) this.getCurrentMatch());
         
         this.getPattern().findMatch();
      }
      
      return matches;
   }


   public ClassModelPO(){
      newInstance(null);
   }

   public ClassModelPO(ClassModel... hostGraphObject) {
      if(hostGraphObject==null || hostGraphObject.length<1){
         return ;
      }
      newInstance(null, hostGraphObject);
   }

   public ClassModelPO(String modifier)
   {
      this.setModifier(modifier);
   }
   public ClassModelPO createPackageNameCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_PACKAGENAME)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createPackageNameCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_PACKAGENAME)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createPackageNameAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_PACKAGENAME)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getPackageName()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((ClassModel) getCurrentMatch()).getPackageName();
      }
      return null;
   }
   
   public ClassModelPO withPackageName(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((ClassModel) getCurrentMatch()).setPackageName(value);
      }
      return this;
   }
   
   public ClazzPO createClassesPO()
   {
      ClazzPO result = new ClazzPO(new Clazz[]{});
      
      result.setModifier(this.getPattern().getModifier());
      super.hasLink(ClassModel.PROPERTY_CLASSES, result);
      
      return result;
   }

   public ClazzPO createClassesPO(String modifier)
   {
      ClazzPO result = new ClazzPO(new Clazz[]{});
      
      result.setModifier(modifier);
      super.hasLink(ClassModel.PROPERTY_CLASSES, result);
      
      return result;
   }

   public ClassModelPO createClassesLink(ClazzPO tgt)
   {
      return hasLinkConstraint(tgt, ClassModel.PROPERTY_CLASSES);
   }

   public ClassModelPO createClassesLink(ClazzPO tgt, String modifier)
   {
      return hasLinkConstraint(tgt, ClassModel.PROPERTY_CLASSES, modifier);
   }

   public ClazzSet getClasses()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((ClassModel) this.getCurrentMatch()).getClasses();
      }
      return null;
   }

   public ClassModelPO createCodeDirCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_CODEDIR)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createCodeDirCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_CODEDIR)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createCodeDirAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_CODEDIR)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getCodeDir()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((ClassModel) getCurrentMatch()).getCodeDir();
      }
      return null;
   }
   
   public ClassModelPO withCodeDir(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((ClassModel) getCurrentMatch()).setCodeDir(value);
      }
      return this;
   }
   
}
