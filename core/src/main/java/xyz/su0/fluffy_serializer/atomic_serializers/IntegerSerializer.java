/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

public class IntegerSerializer implements IAtomicSerializer {
  public String serialize(Object i) {
    return ((Integer)i).toString();
  }

  public Integer deserialize(String s) {
    return Integer.parseInt(s);
  }
}
