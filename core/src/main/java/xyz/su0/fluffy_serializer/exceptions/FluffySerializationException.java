package xyz.su0.fluffy_serializer.exceptions;

/**
 * Fluffy serialization exception thrown when serialization failed.
 */
public class FluffySerializationException extends Exception {
  public FluffySerializationException() {
  }

  public FluffySerializationException(String message) {
    super(message);
  }

  public FluffySerializationException(Throwable cause) {
    super(cause);
  }

  public FluffySerializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
