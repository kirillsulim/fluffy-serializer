/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

import java.util.List;
import java.util.ArrayList;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;


class DemoClass {
  int data;
}

public class ReferenceSerializerTest {
  ReferenceSerializer sz;

  @Test
  public void shouldSerializeObjectsIntoReferences() {
    List<Object> elements = new ArrayList<Object>();
    ReferenceSerializer sz = new ReferenceSerializer(elements);

    DemoClass first = new DemoClass();
    DemoClass second = new DemoClass();

    String strFirst = sz.serialize(first);
    String strSecond = sz.serialize(second);
    String strThird = sz.serialize(first);

    assertEquals("\"&0\"", strFirst);
    assertEquals("\"&1\"", strSecond);
    assertEquals("\"&0\"", strThird);
  }

  @Test
  public void shouldDeserializeObjectsFromReferences() {
    DemoClass first = new DemoClass();
    DemoClass second = new DemoClass();
    DemoClass third = new DemoClass();

    List<Object> elements = new ArrayList<Object>();
    elements.add(first);
    elements.add(second);
    elements.add(third);

    ReferenceSerializer sz = new ReferenceSerializer(elements);

    Object firstDeserialized = sz.deserialize("\"&0\"");
    Object secondDeserialized = sz.deserialize("\"&1\"");
    Object thirdDeserialized = sz.deserialize("\"&2\"");

    assertEquals(first, firstDeserialized);
    assertEquals(second, secondDeserialized);
    assertEquals(third, thirdDeserialized);
  }
}
