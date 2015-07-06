package xyz.su0.fluffy_serializer.atomic_serializers;

import java.lang.StringBuilder;

/**
 * Serialize strings. Mostly do nothing =)
 */
public class StringSerializer implements IAtomicSerializer {

  /**
   * Basicly escape "bad" characters.
   * @param s String to seriazlize
   * @return Serialized string
   */
  public String serialize(Object s) {
    if(s == null || ((String)s).isEmpty()) {
      return "\"\"";
    }
    StringBuilder sb = new StringBuilder(((String)s).replace("\"", "\\\""));
    sb.insert(0, '\"');
    sb.append('\"');
    return sb.toString();
  }

  /**
   * Deserialize string
   * @param s Serialized string
   * @return String
   */
  public String deserialize(String s) {
    return s.replace("\\\"", "\"").replaceAll("^\"|\"$", "");
  }
}
