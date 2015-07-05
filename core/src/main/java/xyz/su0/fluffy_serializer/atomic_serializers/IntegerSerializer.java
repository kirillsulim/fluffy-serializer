package xyz.su0.fluffy_serializer.atomic_serializers;

/**
 * Serialize integer data
 */
public class IntegerSerializer implements IAtomicSerializer {
  /**
   * @param i Integer value
   * @return Serialized value
   */
  public String serialize(Object i) {
    return ((Integer)i).toString();
  }

  /**
   * @param s String with serialized integer
   * @return Deserialized value
   */
  public Integer deserialize(String s) {
    return Integer.parseInt(s);
  }
}
