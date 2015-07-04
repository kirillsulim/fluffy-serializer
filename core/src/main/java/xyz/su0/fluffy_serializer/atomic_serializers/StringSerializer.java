/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

import java.lang.StringBuilder;


public class StringSerializer implements IAtomicSerializer {
  public String serialize(Object s) {
    StringBuilder sb = new StringBuilder(((String)s).replace("\"", "\\\""));
    sb.insert(0, '\"');
    sb.append('\"');
    return sb.toString();
  }

  public String deserialize(String s) {
    return s.replace("\\\"", "\"").replaceAll("^\"|\"$", "");
  }
}
