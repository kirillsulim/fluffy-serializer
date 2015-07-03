/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

class StringSerializer implements IAtomicSerializer<String> {
  public String serialize(String s) {
    return s.replace("\"", "\\\"");
  }

  public String deserialize(String s) {
    return s.replace("\\\"", "\"");
  }
}
