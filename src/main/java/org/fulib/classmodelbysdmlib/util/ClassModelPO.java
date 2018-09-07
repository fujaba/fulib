package org.fulib.classmodelbysdmlib.util;

import org.sdmlib.models.pattern.PatternObject;
import org.fulib.classmodelbysdmlib.ClassModel;
import org.sdmlib.models.pattern.AttributeConstraint;
import org.sdmlib.models.pattern.Pattern;
import org.fulib.classmodelbysdmlib.Clazz;

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
   public ClassModelPO createMainJavaDirCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_MAINJAVADIR)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createMainJavaDirCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_MAINJAVADIR)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createMainJavaDirAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_MAINJAVADIR)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getMainJavaDir()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((ClassModel) getCurrentMatch()).getMainJavaDir();
      }
      return null;
   }
   
   public ClassModelPO withMainJavaDir(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((ClassModel) getCurrentMatch()).setMainJavaDir(value);
      }
      return this;
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
   
   public ClassModelPO createTestJavaDirCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_TESTJAVADIR)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createTestJavaDirCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_TESTJAVADIR)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createTestJavaDirAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_TESTJAVADIR)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getTestJavaDir()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((ClassModel) getCurrentMatch()).getTestJavaDir();
      }
      return null;
   }
   
   public ClassModelPO withTestJavaDir(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((ClassModel) getCurrentMatch()).setTestJavaDir(value);
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

   public ClassModelPO createDefaultRoleTypeCondition(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_DEFAULTROLETYPE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createDefaultRoleTypeCondition(String lower, String upper)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_DEFAULTROLETYPE)
      .withTgtValue(lower)
      .withUpperTgtValue(upper)
      .withSrc(this)
      .withModifier(this.getPattern().getModifier())
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public ClassModelPO createDefaultRoleTypeAssignment(String value)
   {
      new AttributeConstraint()
      .withAttrName(ClassModel.PROPERTY_DEFAULTROLETYPE)
      .withTgtValue(value)
      .withSrc(this)
      .withModifier(Pattern.CREATE)
      .withPattern(this.getPattern());
      
      super.filterAttr();
      
      return this;
   }
   
   public String getDefaultRoleType()
   {
      if (this.getPattern().getHasMatch())
      {
         return ((ClassModel) getCurrentMatch()).getDefaultRoleType();
      }
      return null;
   }
   
   public ClassModelPO withDefaultRoleType(String value)
   {
      if (this.getPattern().getHasMatch())
      {
         ((ClassModel) getCurrentMatch()).setDefaultRoleType(value);
      }
      return this;
   }
   
}
