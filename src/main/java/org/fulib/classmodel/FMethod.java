package org.fulib.classmodel;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.fulib.parser.FulibClassLexer;
import org.fulib.parser.FulibClassParser;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FMethod
{
   // =============== Constants ===============

   public static final String PROPERTY_methodBody = "methodBody";
   public static final String PROPERTY_declaration = "declaration";
   public static final String PROPERTY_clazz = "clazz";
   public static final String PROPERTY_modified = "modified";
   public static final String PROPERTY_annotations = "annotations";
   /** @since 1.2 */
   public static final String PROPERTY_modifiers = "modifiers";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private Clazz clazz;
   private String name;
   private String annotations;
   private String modifiers = "public";
   private LinkedHashMap<String, String> params;
   private String returnType;
   private String methodBody;

   private boolean modified;

   // =============== Properties ===============

   public Clazz getClazz()
   {
      return this.clazz;
   }

   public FMethod setClazz(Clazz value)
   {
      if (this.clazz == value)
      {
         return this;
      }

      final Clazz oldValue = this.clazz;
      if (this.clazz != null)
      {
         this.clazz = null;
         oldValue.withoutMethods(this);
      }
      this.clazz = value;
      if (value != null)
      {
         value.withMethods(this);
      }
      this.firePropertyChange(PROPERTY_clazz, oldValue, value);
      return this;
   }

   /**
    * @since 1.2
    */
   public String getName()
   {
      return this.name;
   }

   /**
    * @since 1.2
    */
   public FMethod setName(String newName)
   {
      this.name = newName;
      return this;
   }

   /**
    * @return the declaration of this method.
    * Includes header, including annotations, modifiers, return type, name, and parameters.
    */
   public String getDeclaration() // no fulib
   {
      // e.g. public void m(T1 p1, T2 p2)
      if (this.name == null)
      {
         return null;
      }

      final StringBuilder builder = new StringBuilder();
      if (this.annotations != null && !this.annotations.isEmpty())
      {
         builder.append(this.annotations);
         builder.append("\n   ");
      }

      builder.append(this.modifiers);
      builder.append(' ');
      builder.append(this.returnType);
      builder.append(' ');
      builder.append(this.name);
      builder.append('(');
      builder.append(this.getParamsString());
      builder.append(')');

      return builder.toString();
   }

   private static String inputText(ParserRuleContext rule)
   {
      final Token start = rule.getStart();
      final CharStream inputStream = start.getInputStream();
      return inputStream.getText(Interval.of(start.getStartIndex(), rule.getStop().getStopIndex()));
   }

   public FMethod setDeclaration(String value) // no fulib
   {
      // a declaration looks like
      // public void m(T1 p1, T2 p2)

      final String oldValue = this.getDeclaration();
      if (value.equals(oldValue))
      {
         return this;
      }

      // adding ";" because the "method" rule expects it
      final CharStream input = CharStreams.fromString(value + ";");
      final FulibClassLexer lexer = new FulibClassLexer(input);
      final FulibClassParser parser = new FulibClassParser(new CommonTokenStream(lexer));

      final FulibClassParser.MethodContext methodCtx = parser.method();
      final FulibClassParser.MethodMemberContext memberCtx = methodCtx.methodMember();

      final String annotations = methodCtx
         .annotation()
         .stream()
         .map(FMethod::inputText)
         .collect(Collectors.joining(" "));
      this.setAnnotations(annotations);

      this.setModifiers(methodCtx.modifier().stream().map(FMethod::inputText).collect(Collectors.joining(" ")));

      String returnType = inputText(memberCtx.type());

      for (Object ignored : memberCtx.arraySuffix())
      {
         //noinspection StringConcatenationInLoop
         returnType += "[]";
      }

      this.setReturnType(returnType);
      this.setName(memberCtx.IDENTIFIER().getText());
      this.setParams(memberCtx.parameterList());

      this.firePropertyChange("declaration", oldValue, value);
      return this;
   }

   private void setParams(FulibClassParser.ParameterListContext paramsCtx)
   {
      if (this.params == null)
      {
         this.params = new LinkedHashMap<>();
      }
      else
      {
         this.params.clear();
      }

      for (final FulibClassParser.ParameterContext paramCtx : paramsCtx.parameter())
      {
         final String name = paramCtx.IDENTIFIER().getText();
         final String type = inputText(paramCtx.type());
         this.params.put(name, type);
      }
   }

   public String getAnnotations()
   {
      return this.annotations;
   }

   public FMethod setAnnotations(String value)
   {
      if (Objects.equals(value, this.annotations))
      {
         return this;
      }

      final String oldValue = this.annotations;
      this.annotations = value;
      this.firePropertyChange(PROPERTY_annotations, oldValue, value);
      return this;
   }

   /**
    * @since 1.2
    */
   public String getModifiers()
   {
      return this.modifiers;
   }

   /**
    * @since 1.2
    */
   public FMethod setModifiers(String value)
   {
      if (Objects.equals(value, this.modifiers))
      {
         return this;
      }

      final String oldValue = this.modifiers;
      this.modifiers = value;
      this.firePropertyChange(PROPERTY_modifiers, oldValue, value);
      return this;
   }

   /**
    * @since 1.2
    */
   public LinkedHashMap<String, String> getParams()
   {
      if (this.params == null)
      {
         this.params = new LinkedHashMap<>();
      }
      return this.params;
   }

   /**
    * @since 1.2
    */
   public String getParamsString()
   {
      return this.getParams().entrySet().stream().map(e -> e.getValue() + " " + e.getKey())
                 .collect(Collectors.joining(", "));
   }

   /**
    * @since 1.2
    */
   public FMethod setParamsString(String params)
   {
      final CharStream input = CharStreams.fromString("(" + params + ")");
      final FulibClassLexer lexer = new FulibClassLexer(input);
      final FulibClassParser parser = new FulibClassParser(new CommonTokenStream(lexer));

      this.setParams(parser.parameterList());

      return this;
   }

   /**
    * @since 1.2
    */
   public String getSignature()
   {
      String paramTypes = this.getParams().entrySet().stream().filter(e -> !"this".equals(e.getKey()))
                              .map(Map.Entry::getValue).collect(Collectors.joining(","));
      return FileFragmentMap.CLASS + '/' + this.getClazz().getName() + '/' + FileFragmentMap.METHOD + '/'
             + this.getName() + '(' + paramTypes + ')';
   }

   /**
    * @since 1.2
    */
   public String getReturnType()
   {
      return this.returnType;
   }

   /**
    * @since 1.2
    */
   public FMethod setReturnType(String value)
   {
      this.returnType = value;
      return this;
   }

   public String getMethodBody()
   {
      return this.methodBody;
   }

   public FMethod setMethodBody(String value)
   {
      if (Objects.equals(value, this.methodBody))
      {
         return this;
      }

      final String oldValue = this.methodBody;
      this.methodBody = value;
      this.firePropertyChange(PROPERTY_methodBody, oldValue, value);
      return this;
   }

   public boolean getModified()
   {
      return this.modified;
   }

   public FMethod setModified(boolean value)
   {
      if (value == this.modified)
      {
         return this;
      }

      final boolean oldValue = this.modified;
      this.modified = value;
      this.firePropertyChange(PROPERTY_modified, oldValue, value);
      return this;
   }

   // =============== Methods ===============

   /**
    * @since 1.2
    */
   public boolean signatureMatches(FMethod that)
   {
      if (!Objects.equals(this.getName(), that.getName()) || this.getParams().size() != that.getParams().size())
      {
         return false;
      }

      // unfortunately this.params.values().equals(that.params.value()) does not work
      final Iterator<String> it1 = this.getParams().values().iterator();
      final Iterator<String> it2 = that.getParams().values().iterator();
      while (it1.hasNext() && it2.hasNext())
      {
         final String type1 = it1.next();
         final String type2 = it2.next();
         if (!type1.equals(type2))
         {
            return false;
         }
      }

      return true;
   }

   /** @deprecated since 1.2; use {@link #getName()} instead */
   @Deprecated
   public String readName()
   {
      return this.getName();
   }

   /** @deprecated since 1.2; use {@link #setName(String)} instead */
   @Deprecated
   public FMethod writeName(String newName)
   {
      return this.setName(newName);
   }

   /** @deprecated since 1.2; use {@link #getSignature()} instead */
   @Deprecated
   public String readSignature()
   {
      return this.getSignature();
   }

   /** @deprecated since 1.2; use {@link #getReturnType()} instead */
   @Deprecated
   public String readReturnType()
   {
      return this.getReturnType();
   }

   /** @deprecated since 1.2; use {@link #setReturnType(String)} instead */
   @Deprecated
   public FMethod writeReturnType(String newReturnType)
   {
      return this.setReturnType(newReturnType);
   }

   /** @deprecated since 1.2; use {@link #getParams()} instead */
   @Deprecated
   public LinkedHashMap<String, String> readParams()
   {
      return this.getParams();
   }

   /** @deprecated since 1.2; use {@link #getParamsString()} instead */
   @Deprecated
   public String readFullParamsString()
   {
      return this.getParamsString();
   }

   /** @deprecated since 1.2; use {@link #setParamsString(String)} instead */
   @Deprecated
   public FMethod setParamsByString(String params)
   {
      return this.setParamsString(params);
   }

   public boolean addPropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(listener);
      return true;
   }

   public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      this.listeners.addPropertyChangeListener(propertyName, listener);
      return true;
   }

   public boolean removePropertyChangeListener(PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(listener);
      }
      return true;
   }

   public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      if (this.listeners != null)
      {
         this.listeners.removePropertyChangeListener(propertyName, listener);
      }
      return true;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public void removeYou()
   {
      this.setClazz(null);
   }

   @Override
   public String toString() // no fulib
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getDeclaration());
      result.append(' ').append(this.getMethodBody());
      result.append(' ').append(this.getAnnotations());
      return result.substring(1);
   }
}
