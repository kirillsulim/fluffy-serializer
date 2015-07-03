/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

/**
 *
 */
public interface IAtomicSerializer<T> {
  // TODO: Add parse exceptions

  /**
   *
   */
  public abstract String serialize(T c);

  /**
   *
   */
  public abstract T deserialize(String s);
}
