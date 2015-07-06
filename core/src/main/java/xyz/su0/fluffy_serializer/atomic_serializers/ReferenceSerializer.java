package xyz.su0.fluffy_serializer.atomic_serializers;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * This atomic serializer process reference variables
 */
public class ReferenceSerializer implements IAtomicSerializer {
  private List<Object> elements;

  private final static String nullValue = "\"&null\"";

  /**
   * When created reference serializer must share objectsArray with instance
   * of FluffySerializer class so it can define link to right object.
   * @param objectsArray objects list from FluffySerializer instance.
   */
  public ReferenceSerializer(List<Object> objectsArray) {
    elements = objectsArray;
  }

  /**
   * Get object index in objectsArray or add this object to it.
   * @param object Object to serialize
   * @return Serialized string
   */
  public String serialize(Object object) {
    if(object == null) {
      return nullValue;
    }
    int i = elements.indexOf(object);
    if(i == -1) {
      elements.add(object);
      i = elements.size() - 1;
    }
    return String.format("\"&%d\"", i);
  }

  /**
   * Find object in partially deserialized objectsArray and return it.
   * @param input String with "address" of object.
   * @return Object
   */
  public Object deserialize(String input) {
    if(input.equals(nullValue)) {
      return null;
    }

    Pattern pattern = Pattern.compile("\"&(\\d+)\"");
    Matcher matcher = pattern.matcher(input);

    if(!matcher.matches()) {
      System.out.println("Mismatch!");
      return null;
    }

    int i = Integer.parseInt(matcher.group(1));

    return elements.get(i);
  }
}
