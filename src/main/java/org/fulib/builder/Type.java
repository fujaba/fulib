package org.fulib.builder;

public class Type
{
   // value types
   // for attributes
   public static final String BOOLEAN = "boolean";
   public static final String INT     = "int";
   public static final String LONG    = "long";
   public static final String FLOAT   = "float";
   public static final String DOUBLE  = "double";
   public static final String STRING  = "String";

   // cardinalities
   // for associations
   public static final int ONE  = 1;
   public static final int MANY = 42;

   // collection types
   // for to-n associations and collection attributes
   public static final String COLLECTION_ARRAY_LIST      = "java.util.ArrayList<%s>";
   public static final String COLLECTION_LINKED_HASH_SET = "java.util.LinkedHashSet<%s>";

   // property styles
   // for attributes and associations
   public static final String POJO    = "POJO";
   public static final String JAVA_FX = "JavaFX";
}
