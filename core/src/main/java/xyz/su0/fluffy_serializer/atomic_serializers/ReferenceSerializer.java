/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ReferenceSerializer implements IAtomicSerializer {
  private List<Object> elements;

  public ReferenceSerializer() {
    elements = new ArrayList<Object>();
  }

  public void setElements(List<Object> elements) {
    this.elements = elements;
  }

  public String serialize(Object obj) {
    int i = elements.indexOf(obj);
    if(i == -1) {
      elements.add(obj);
      i = elements.size() - 1;
    }
    return String.format("&%d", i);
  }

  public Object deserialize(String input) {
    Pattern pattern = Pattern.compile("&(\\d+)");
    Matcher matcher = pattern.matcher(input);

    if(!matcher.matches()) {
      return null;
    }

    int i = Integer.parseInt(matcher.group(1));

    return elements.get(i);
  }
}
