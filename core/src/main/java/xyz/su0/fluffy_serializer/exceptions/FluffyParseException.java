package xyz.su0.fluffy_serializer.exceptions;

/**
 * Fluffy serializer parse exception
 *
 * TODO: Add char index after switching to lexical analysis
 */
public class FluffyParseException extends Exception {
  public FluffyParseException() {
  }

  public FluffyParseException(String message) {
    super(message);
  }

  public FluffyParseException(Throwable cause) {
    super(cause);
  }

  public FluffyParseException(String message, Throwable cause) {
    super(message, cause);
  }
}
