package org.fulib.util;

import org.ec4j.core.*;
import org.ec4j.core.model.PropertyType;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;

class EditorConfigData
{
   private static final Charset EDITORCONFIG_FILE_ENCODING = StandardCharsets.UTF_8;

   private static final PropertyType.IndentStyleValue DEFAULT_INDENT_STYLE = PropertyType.IndentStyleValue.space;
   private static final int DEFAULT_INDENT_SIZE = 3;
   private static final PropertyType.EndOfLineValue DEFAULT_EOL = PropertyType.EndOfLineValue.lf;
   private static final boolean DEFAULT_EOF_NEWLINE = true;
   private static final String DEFAULT_CHARSET = "UTF-8";

   final String indent;
   final String eol;
   final boolean eofNewline;
   final String charset;

   public EditorConfigData(String fileName) throws IOException
   {
      final ResourcePropertiesService propService = ResourcePropertiesService
         .builder()
         .cache(Cache.Caches.permanent())
         .loader(EditorConfigLoader.default_())
         .rootDirectory(ResourcePath.ResourcePaths.ofPath(Paths.get("."), EDITORCONFIG_FILE_ENCODING))
         .build();

      final ResourceProperties props = propService.queryProperties(
         Resource.Resources.ofPath(Paths.get(fileName), EDITORCONFIG_FILE_ENCODING));

      this.indent = getIndent(props);
      this.eol = getEOL(props);
      this.eofNewline = props.getValue(PropertyType.insert_final_newline, DEFAULT_EOF_NEWLINE, true);
      this.charset = props.getValue(PropertyType.charset, DEFAULT_CHARSET, true);
   }

   private static String getIndent(ResourceProperties props)
   {
      final PropertyType.IndentStyleValue indentStyle = props.getValue(PropertyType.indent_style, DEFAULT_INDENT_STYLE,
                                                                       true);
      if (indentStyle == PropertyType.IndentStyleValue.tab)
      {
         return "\t";
      }

      final int numSpaces = props.getValue(PropertyType.indent_size, DEFAULT_INDENT_SIZE, true);
      final char[] chars = new char[numSpaces];
      Arrays.fill(chars, ' ');
      return new String(chars);
   }

   private static String getEOL(ResourceProperties props)
   {
      final PropertyType.EndOfLineValue value = props.getValue(PropertyType.end_of_line, DEFAULT_EOL, true);
      switch (value)
      {
      case cr:
         return "\r";
      case crlf:
         return "\r\n";
      case lf:
         return "\n";
      default:
         throw new RuntimeException("unknown end_of_line type: " + value);
      }
   }
}
