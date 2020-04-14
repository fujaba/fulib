package org.fulib.classmodel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CompoundFragment extends Fragment
{
   // =============== Constants ===============

   public static final String PROPERTY_children = "children";

   // =============== Fields ===============

   protected PropertyChangeSupport listeners;

   private List<Fragment> children;

   // =============== Properties ===============

   public List<Fragment> getChildren()
   {
      return this.children != null ? Collections.unmodifiableList(this.children) : Collections.emptyList();
   }

   public CompoundFragment withChildren(int index, Fragment value)
   {
      if (this.children == null)
      {
         this.children = new ArrayList<>();
      }
      if (!this.children.contains(value))
      {
         this.children.add(index, value);
         value.setParent(this);
         this.firePropertyChange(PROPERTY_children, null, value);
      }
      return this;
   }

   public CompoundFragment withChildren(Fragment value)
   {
      if (this.children == null)
      {
         this.children = new ArrayList<>();
      }
      if (!this.children.contains(value))
      {
         this.children.add(value);
         value.setParent(this);
         this.firePropertyChange(PROPERTY_children, null, value);
      }
      return this;
   }

   public CompoundFragment withChildren(Fragment... value)
   {
      for (final Fragment item : value)
      {
         this.withChildren(item);
      }
      return this;
   }

   public CompoundFragment withChildren(Collection<? extends Fragment> value)
   {
      for (final Fragment item : value)
      {
         this.withChildren(item);
      }
      return this;
   }

   public CompoundFragment withoutChildren(Fragment value)
   {
      if (this.children != null && this.children.remove(value))
      {
         value.setParent(null);
         this.firePropertyChange(PROPERTY_children, value, null);
      }
      return this;
   }

   public CompoundFragment withoutChildren(Fragment... value)
   {
      for (final Fragment item : value)
      {
         this.withoutChildren(item);
      }
      return this;
   }

   public CompoundFragment withoutChildren(Collection<? extends Fragment> value)
   {
      for (final Fragment item : value)
      {
         this.withoutChildren(item);
      }
      return this;
   }

   // =============== Methods ===============

   @Override
   public void write(Writer writer) throws IOException
   {
      for (final Fragment child : this.children)
      {
         child.write(writer);
      }
   }

   public Fragment getChild(String key)
   {
      for (final Fragment child : this.children)
      {
         if (key.equals(child.getKey()))
         {
            return child;
         }
      }
      return null;
   }

   public Fragment getAncestor(String... path)
   {
      Fragment ctx = this;
      for (final String key : path)
      {
         if (ctx instanceof CompoundFragment)
         {
            ctx = ((CompoundFragment) ctx).getChild(key);
         }
         else
         {
            return null;
         }
      }
      return ctx;
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

   @Override
   public void removeYou()
   {
      super.removeYou();
      this.withoutChildren(new ArrayList<>(this.getChildren()));
   }
}
