/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

class IntegerSerializer implements IAtomicSerializer<Integer> {
  public String serialize(Integer i) {
    return i.toString();
  }

  public Integer deserialize(String s) {
    return Integer.parseInt(s);
  }
}
