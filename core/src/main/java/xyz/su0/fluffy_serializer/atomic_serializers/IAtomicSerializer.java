/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

/**
 *
 */
public interface IAtomicSerializer {
  // TODO: Add parse exceptions

  /**
   *
   */
  public abstract String serialize(Object o);

  /**
   *
   */
  public abstract Object deserialize(String s);
}
