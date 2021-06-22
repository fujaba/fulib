package org.fulib.classmodel;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.Interval;
import org.fulib.parser.FragmentMapBuilder;
import org.fulib.parser.FulibClassLexer;
import org.fulib.parser.FulibClassParser;
import org.fulib.parser.FulibErrorHandler;
import org.fulib.util.Validator;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
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

   /** @since 1.3 */
   public static final String PROPERTY_METHOD_BODY = "methodBody";
   /** @since 1.3 */
   public static final String PROPERTY_MODIFIED = "modified";
   /** @since 1.3 */ // no fulib
   public static final String PROPERTY_MODIFIERS = "modifiers";
   /** @since 1.3 */
   public static final String PROPERTY_ANNOTATIONS = "annotations";
   /** @since 1.3 */
   public static final String PROPERTY_CLAZZ = "clazz";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private Clazz clazz;
   private String name;
   private String annotations;
   private String modifiers = "public";
   private final LinkedHashMap<String, String> params = new LinkedHashMap<>();
   private final Map<String, String> typeParams = new LinkedHashMap<>();
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
      this.firePropertyChange(PROPERTY_CLAZZ, oldValue, value);
      return this;
   }

   /**
    * @return a string that identifies this method within the enclosing class model
    *
    * @since 1.3
    * @deprecated for serialization purposes only
    */
   @Deprecated
   public String getId()
   {
      final Clazz clazz = this.getClazz();
      return (clazz != null ? clazz.getName() : "_") + "_" + this.getName();
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
    * @return the declaration of this method, including annotations, modifiers, return type, name, and parameters
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
         builder.append('\n');
      }

      builder.append(this.modifiers);
      builder.append(' ');
      if (!this.typeParams.isEmpty())
      {
         builder.append('<');
         for (final Map.Entry<String, String> entry : this.typeParams.entrySet())
         {
            builder.append(entry.getKey());
            if (entry.getValue() != null)
            {
               builder.append(" extends ");
               builder.append(entry.getValue());
            }
            builder.append(", ");
         }
         builder.setCharAt(builder.length() - 2, '>');
      }
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

   /**
    * @param value
    *    the declaration of this method, including annotations, modifiers, return type, name, and parameters
    *
    * @return this
    *
    * @throws IllegalArgumentException
    *    if the declaration has syntax errors
    */
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

      final StringBuilder errors = new StringBuilder("syntax errors in declaration:\n").append(value).append('\n');
      parser.removeErrorListeners();
      parser.addErrorListener(new FulibErrorHandler(errors));

      final FulibClassParser.MethodContext methodCtx = parser.method();

      if (parser.getNumberOfSyntaxErrors() > 0)
      {
         throw new IllegalArgumentException(errors.toString());
      }

      final FulibClassParser.MethodMemberContext memberCtx = methodCtx.methodMember();

      final String annotations = methodCtx
         .annotation()
         .stream()
         .map(FMethod::inputText)
         .collect(Collectors.joining(" "));
      this.setAnnotations(annotations);

      this.setModifiers(methodCtx.modifier().stream().map(FMethod::inputText).collect(Collectors.joining(" ")));

      final FulibClassParser.TypeParamListContext typeParams = memberCtx.typeParamList();
      String returnType = inputText(typeParams != null ? memberCtx.annotatedType(0) : memberCtx.type());

      for (int arrayDimensions = memberCtx.arraySuffix().size(); arrayDimensions > 0; arrayDimensions--)
      {
         // C-style arrays are so rare that normal methods don't need to pay the StringBuilder overhead
         // noinspection StringConcatenationInLoop
         returnType += "[]";
      }

      this.setTypeParams(typeParams);
      this.setReturnType(returnType);
      this.setName(memberCtx.IDENTIFIER().getText());
      this.setParams(memberCtx.parameterList());

      this.firePropertyChange("declaration", oldValue, value);
      return this;
   }

   private void setTypeParams(FulibClassParser.TypeParamListContext paramsCtx)
   {
      this.typeParams.clear();
      if (paramsCtx == null)
      {
         return;
      }

      for (final FulibClassParser.TypeParamContext typeParamCtx : paramsCtx.typeParam())
      {
         final String name = typeParamCtx.IDENTIFIER().getText();
         final List<FulibClassParser.AnnotatedTypeContext> types = typeParamCtx.annotatedType();
         final String type = types.isEmpty()
            ? null
            : types.stream().map(FMethod::inputText).collect(Collectors.joining(" & "));
         this.typeParams.put(name, type);
      }
   }

   private void setParams(FulibClassParser.ParameterListContext paramsCtx)
   {
      this.params.clear();
      for (final FulibClassParser.ParameterContext paramCtx : paramsCtx.parameter())
      {
         final String name = paramCtx.IDENTIFIER().getText();
         String type = inputText(paramCtx.type());
         for (int arrayDimensions = paramCtx.arraySuffix().size(); arrayDimensions > 0; arrayDimensions--)
         {
            // C-style arrays are so rare that normal methods don't need to pay the StringBuilder overhead
            // noinspection StringConcatenationInLoop
            type += "[]";
         }
         if (paramCtx.ELLIPSIS() != null)
         {
            type += "...";
         }
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
      this.firePropertyChange(PROPERTY_ANNOTATIONS, oldValue, value);
      return this;
   }

   /**
    * @return the modifiers. Defaults to "public"
    *
    * @since 1.2
    */
   public String getModifiers()
   {
      return this.modifiers;
   }

   /**
    * @param value
    *    the modifiers. Defaults to "public"
    *
    * @return this
    *
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
      this.firePropertyChange(PROPERTY_MODIFIERS, oldValue, value);
      return this;
   }

   /**
    * @return a mutable, ordered map of parameters, where the key is the name and the value is the type.
    *
    * @since 1.2
    */
   public LinkedHashMap<String, String> getParams()
   {
      return this.params;
   }

   /**
    * @return an mutable, ordered map of type parameters, where the key is the name and the value is either {code null}
    * for a plain type parameter {@code T} or the type bound {@code T extends Bound}.
    *
    * @since 1.5
    */
   public Map<String, String> getTypeParams()
   {
      return typeParams;
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
    * @return a signature that allows inserting this method into {@link FileFragmentMap FileFragmentMaps},
    * using their signature format.
    *
    * @since 1.2
    */
   public String getSignature()
   {
      final CharStream input = CharStreams.fromString("(" + this.getParamsString() + ")");
      final FulibClassLexer lexer = new FulibClassLexer(input);
      final FulibClassParser parser = new FulibClassParser(new CommonTokenStream(lexer));
      final FulibClassParser.ParameterListContext paramsCtx = parser.parameterList();
      final String paramsSignature = FragmentMapBuilder.getParamsSignature(paramsCtx);

      final int parameterCount = this.params.size() - (this.params.containsKey("this") ? 1 : 0);
      final String kind = Validator.isProperty(this.getName(), parameterCount)
         ? FileFragmentMap.PROPERTY
         : FileFragmentMap.METHOD;
      return FileFragmentMap.CLASS + '/' + this.getClazz().getName() + '/' + kind + '/' + this.getName()
             + paramsSignature;
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
      this.firePropertyChange(PROPERTY_METHOD_BODY, oldValue, value);
      return this;
   }

   /**
    * @return a boolean indicating whether this method was modified. For internal use only.
    */
   public boolean getModified()
   {
      return this.modified;
   }

   /**
    * @param value
    *    a boolean indicating whether this method was modified. For internal use only.
    *
    * @return this
    */
   public FMethod setModified(boolean value)
   {
      if (value == this.modified)
      {
         return this;
      }

      final boolean oldValue = this.modified;
      this.modified = value;
      this.firePropertyChange(PROPERTY_MODIFIED, oldValue, value);
      return this;
   }

   // =============== Methods ===============

   /**
    * @param that the method to compare signatures with
    *
    * @return a boolean indicating whether the signatures of this and the given method match.
    * In particular, two method's signatures match if and only if they fullfill all of the following conditions:
    * <ul>
    *    <li>The names of the methods are exactly equal</li>
    *    <li>The methods have the same number of parameters</li>
    *    <li>The parameter types of the methods are exactly equal</li>
    * </ul>
    *
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
