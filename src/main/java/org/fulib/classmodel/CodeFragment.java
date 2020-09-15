package org.fulib.classmodel;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

public class CodeFragment extends Fragment
{
   // =============== Constants ===============

   /** @deprecated since 1.2; use {@link Fragment#PROPERTY_key} instead */
   @Deprecated
   public static final String PROPERTY_key = "key";
   public static final String PROPERTY_text = "text";
   public static final String PROPERTY_TEXT = "text";

   // =============== Fields ===============

   private String text;

   // =============== Properties ===============

   /**
    * @since 1.2
    */
   @Override
   public CodeFragment setKey(String value)
   {
      super.setKey(value);
      return this;
   }

   public String getText()
   {
      return this.text;
   }

   public CodeFragment setText(String value)
   {
      if (Objects.equals(value, this.text))
      {
         return this;
      }

      final String oldValue = this.text;
      this.text = value;
      this.firePropertyChange(PROPERTY_TEXT, oldValue, value);
      return this;
   }

   // =============== Methods ===============

   /** @since 1.2 */
   @Override
   public void write(Writer writer) throws IOException
   {
      writer.write(this.getText());
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder(super.toString());
      result.append(' ').append(this.getText());
      return result.toString();
   }
}
