package org.fulib.builder;

/**
 * @since 1.4
 */
public class InvalidClassModelException extends RuntimeException
{
   public InvalidClassModelException(String message)
   {
      super(message);
   }

   public InvalidClassModelException(String message, Throwable cause)
   {
      super(message, cause);
   }
}
