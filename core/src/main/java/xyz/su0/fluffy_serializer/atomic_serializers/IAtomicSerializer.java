package xyz.su0.fluffy_serializer.atomic_serializers;

/**
 * Atomic serializer is base block of fluffy serializer. Atomic serializer
 * serialize and deserialize data of specific type. If you want to customize
 * serialization and deserialization of custom types, impement this interface.
 * <b>Important:</b> you need to implement type checking at your own.
 */
public interface IAtomicSerializer {
  // TODO: Add parse exceptions

  /**
   * Serialize object to string
   * @param object Object to seriazlize
   * @return String with serialized data
   */
  public abstract String serialize(Object object);

  /**
   * Deserialize string to object
   * @param string String with serialized data
   * @return deserialized object
   */
  public abstract Object deserialize(String string);
}
