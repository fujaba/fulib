package org.fulib.classmodel;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class OutputFile
{
   public static final String PROPERTY_FILE_NAME = "fileName";
   public static final String PROPERTY_EDITOR_CONFIG = "editorConfig";
   public static final String PROPERTY_FRAGMENT_MAP = "fragmentMap";
   private String fileName;
   private EditorConfigData editorConfig;
   private FileFragmentMap fragmentMap;
   protected PropertyChangeSupport listeners;

   public String getFileName()
   {
      return this.fileName;
   }

   public OutputFile setFileName(String value)
   {
      if (Objects.equals(value, this.fileName))
      {
         return this;
      }

      final String oldValue = this.fileName;
      this.fileName = value;
      this.firePropertyChange(PROPERTY_FILE_NAME, oldValue, value);
      return this;
   }

   public EditorConfigData getEditorConfig()
   {
      return this.editorConfig;
   }

   public OutputFile setEditorConfig(EditorConfigData value)
   {
      if (this.editorConfig == value)
      {
         return this;
      }

      final EditorConfigData oldValue = this.editorConfig;
      this.editorConfig = value;
      this.firePropertyChange(PROPERTY_EDITOR_CONFIG, oldValue, value);
      return this;
   }

   public FileFragmentMap getFragmentMap()
   {
      return this.fragmentMap;
   }

   public OutputFile setFragmentMap(FileFragmentMap value)
   {
      if (this.fragmentMap == value)
      {
         return this;
      }

      final FileFragmentMap oldValue = this.fragmentMap;
      this.fragmentMap = value;
      this.firePropertyChange(PROPERTY_FRAGMENT_MAP, oldValue, value);
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
      result.append(' ').append(this.getFileName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setEditorConfig(null);
      this.setFragmentMap(null);
   }
}
