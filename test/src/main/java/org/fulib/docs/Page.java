package org.fulib.docs;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

// start_code_fragment: docs.Page
public class Page
{
   public static final String PROPERTY_LINES = "lines";
   private List<String> lines;

   public List<String> getLines()
   {
      return this.lines != null ? Collections.unmodifiableList(this.lines) : Collections.emptyList();
   }

   public Page withLines(String value)
   {
      if (this.lines == null)
      {
         this.lines = new ArrayList<>();
      }
      this.lines.add(value);
      return this;
   }

   public Page withLines(String... value)
   {
      this.withLines(Arrays.asList(value));
      return this;
   }

   public Page withLines(Collection<? extends String> value)
   {
      if (this.lines == null)
      {
         this.lines = new ArrayList<>(value);
      }
      else
      {
         this.lines.addAll(value);
      }
      return this;
   }

   public Page withoutLines(String value)
   {
      this.lines.removeAll(Collections.singleton(value));
      return this;
   }

   public Page withoutLines(String... value)
   {
      this.withoutLines(Arrays.asList(value));
      return this;
   }

   public Page withoutLines(Collection<? extends String> value)
   {
      if (this.lines != null)
      {
         this.lines.removeAll(value);
      }
      return this;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getLines());
      return result.substring(1);
   }
}
// end_code_fragment:
