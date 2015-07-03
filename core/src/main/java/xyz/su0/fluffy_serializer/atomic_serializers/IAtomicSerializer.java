/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

/**
 *
 */
public interface IAtomicSerializer<T> {
  /**
   *
   */
  public abstract String serialize(T c);

  /**
   *
   */
  public abstract T deserialize(String s);
}
