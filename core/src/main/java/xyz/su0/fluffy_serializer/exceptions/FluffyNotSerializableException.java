package xyz.su0.fluffy_serializer.exceptions;

/**
 * Fluffy serializer not serializable exception.
 * Thrown when object is not serizlizable by fluffy-serializer.
 */
public class FluffyNotSerializableException extends Exception {
  public FluffyNotSerializableException() {
  }

  public FluffyNotSerializableException(String message) {
    super(message);
  }

  public FluffyNotSerializableException(Throwable cause) {
    super(cause);
  }

  public FluffyNotSerializableException(String message, Throwable cause) {
    super(message, cause);
  }
}
