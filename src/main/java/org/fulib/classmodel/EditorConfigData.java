package org.fulib.classmodel;

import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class EditorConfigData
{
   public static final String DEFAULT_INDENT = "   ";
   public static final String DEFAULT_EOL = "\n";
   public static final boolean DEFAULT_EOF_NEWLINE = true;
   public static final String DEFAULT_CHARSET = "UTF-8";

   public static final String PROPERTY_INDENT = "indent";
   public static final String PROPERTY_EOL = "eol";
   public static final String PROPERTY_EOF_NEWLINE = "eofNewline";
   public static final String PROPERTY_CHARSET = "charset";

   private String indent = DEFAULT_INDENT;
   private String eol = DEFAULT_EOL;
   private boolean eofNewline = DEFAULT_EOF_NEWLINE;
   private String charset = DEFAULT_CHARSET;

   protected PropertyChangeSupport listeners;

   public String getIndent()
   {
      return this.indent;
   }

   public EditorConfigData setIndent(String value)
   {
      if (Objects.equals(value, this.indent))
      {
         return this;
      }

      final String oldValue = this.indent;
      this.indent = value;
      this.firePropertyChange(PROPERTY_INDENT, oldValue, value);
      return this;
   }

   public String getEol()
   {
      return this.eol;
   }

   public EditorConfigData setEol(String value)
   {
      if (Objects.equals(value, this.eol))
      {
         return this;
      }

      final String oldValue = this.eol;
      this.eol = value;
      this.firePropertyChange(PROPERTY_EOL, oldValue, value);
      return this;
   }

   public boolean isEofNewline()
   {
      return this.eofNewline;
   }

   public EditorConfigData setEofNewline(boolean value)
   {
      if (value == this.eofNewline)
      {
         return this;
      }

      final boolean oldValue = this.eofNewline;
      this.eofNewline = value;
      this.firePropertyChange(PROPERTY_EOF_NEWLINE, oldValue, value);
      return this;
   }

   public String getCharset()
   {
      return this.charset;
   }

   public EditorConfigData setCharset(String value)
   {
      if (Objects.equals(value, this.charset))
      {
         return this;
      }

      final String oldValue = this.charset;
      this.charset = value;
      this.firePropertyChange(PROPERTY_CHARSET, oldValue, value);
      return this;
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

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getIndent());
      result.append(' ').append(this.getEol());
      result.append(' ').append(this.getCharset());
      return result.substring(1);
   }
}
