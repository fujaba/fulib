package org.fulib.classmodel;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.*;

import java.util.LinkedHashSet;
import java.util.List;

@SuppressWarnings("unused")
public class GenModel implements ClassModelDecorator
{
   class ClassModel
   {
      String packageName;
      String mainJavaDir;

      @Description("the default collection type for to-n roles")
      @Since("1.2")
      @Type("CollectionType")
      Object defaultCollectionType;

      @Description("the default property style for attributes and roles.\n"
                   + "Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.")
      @InitialValue("\"POJO\"")
      String defaultPropertyStyle;

      @Description("the classes contained in this model")
      @Since("1.2")
      @Link("model")
      List<Clazz> classes;
   }

   class Clazz
   {
      String name;

      @Description("the default property style for attributes and roles.\n"
                   + "Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.")
      String propertyStyle;

      @Description("a boolean indicating whether this attribute was modified. For internal use only.")
      boolean modified;

      @Description("the set of imported members.\n"
                   + "Elements can have one of the formats {@code org.example.Foo}, {@code static org.example.Foo.bar},\n"
                   + "{@code import org.example.Foo;} or {@code import static org.example.Foo.bar;}")
      @Since("1.2")
      LinkedHashSet<String> imports;

      @Link("classes")
      ClassModel model;

      @Description("the attributes")
      @Since("1.2")
      @Link("clazz")
      List<Attribute> attributes;

      @Description("the roles")
      @Since("1.2")
      @Link("clazz")
      List<AssocRole> roles;

      @Description("the methods")
      @Since("1.2")
      @Link("clazz")
      List<FMethod> methods;

      @Description("the subclasses")
      @Since("1.2")
      @Link("superClass")
      List<Clazz> subClasses;

      @Link("subClasses")
      Clazz superClass;
   }

   class Attribute
   {
      @Link("attributes")
      Clazz clazz;

      String name;

      String type;

      @Description("the collection type")
      @Since("1.2")
      @Type("CollectionType")
      Object collectionType;

      String initialization;

      @Description("the property style.\n"
                   + "Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.")
      String propertyStyle;

      @Description("a boolean indicating whether this attribute was modified. For internal use only.")
      boolean modified;

      @Description("the description of this attribute, used for generating JavaDocs")
      @Since("1.3")
      String description;

      @Description("the version when this attribute was introduced, used for generating JavaDocs")
      @Since("1.3")
      String since;
   }

   class AssocRole
   {
      @Link("roles")
      Clazz clazz;

      @Link("other")
      AssocRole other;

      String name;

      int cardinality;

      @Description("the collection type")
      @Since("1.2")
      @Type("CollectionType")
      Object collectionType;

      @Description("a boolean indicating whether this role is an aggregation,\n"
                   + "i.e. whether the target objects are {@code removeYou}'d completely when using {@code without*} methods or\n"
                   + "{@code removeYou} on the source object")
      boolean aggregation;

      @Description("the property style.\n"
                   + "Currently, only {@link Type#POJO}, {@link Type#BEAN} and {@link Type#JAVA_FX} are supported.")
      String propertyStyle;

      @Description("the description of this role, used for generating JavaDocs")
      @Since("1.3")
      String description;

      @Description("the version when this role was introduced, used for generating JavaDocs")
      @Since("1.3")
      String since;

      @Description("a boolean indicating whether this role was modified. For internal use only.")
      boolean modified;
   }

   class FMethod
   {
      @Link("methods")
      Clazz clazz;

      String methodBody;

      @Description("a boolean indicating whether this method was modified. For internal use only.")
      boolean modified;

      @Description("the modifiers. Defaults to \"public\"")
      @Since("1.2")
      @InitialValue("\"public\"")
      String modifiers;

      String annotations;
   }

   class FileFragmentMap
   {
      String fileName;
   }

   class Fragment
   {
      String key;

      @Link("children")
      CompoundFragment parent;
   }

   class CodeFragment extends Fragment
   {
      String text;
   }

   class CompoundFragment extends Fragment
   {
      @Link("parent")
      List<Fragment> children;
   }

   @Override
   public void decorate(ClassModelManager cmm)
   {
      cmm.haveClasses(GenModel.class);
   }
}
