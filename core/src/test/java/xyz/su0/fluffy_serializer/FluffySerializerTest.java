/*
 *
 */
package xyz.su0.fluffy_serializer;

import org.junit.*;
import static org.junit.Assert.*;


class SimpleObject {
  public int data1;
  public String data2;
}

class DemoClass {
  public DemoClass ref;
  public String data1;
  public int data2;
}

public class FluffySerializerTest {
  FluffySerializer sz;

  @Before
  public void setUp() {
    sz = new FluffySerializer();
  }

  @Test
  public void shouldSerializeSimpleObject() {
    SimpleObject o = new SimpleObject();
    o.data1 = 42;
    o.data2 = "Hello World!";

    String result = sz.serialize(o);

    assertEquals("[{\"@class\":\"xyz.su0.fluffy_serializer.SimpleObject\",\"data1\":42,\"data2\":\"Hello World!\"}]", result);
  }

  @Test
  public void shouldDeserializeSimpleObject() {
    String data = "[{\"@class\":\"xyz.su0.fluffy_serializer.SimpleObject\",\"data1\":42,\"data2\":\"Hello World!\"}]";

    SimpleObject so = (SimpleObject) sz.deserialize(data);

    assertEquals(42, so.data1);
    assertEquals("Hello World!", so.data2);
  }

  @Test
  public void shouldSerializeLinked() {
    DemoClass o1 = new DemoClass();
    o1.data1 = "Hello";
    o1.data2 = 42;

    DemoClass o2 = new DemoClass();
    o2.data1 = "World";
    o2.data2 = 123;

    o1.ref = o2;
    o2.ref = o1;

    String serializedString = sz.serialize(o1);

    assertEquals(
      "["
        + "{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&1\",\"data1\":\"Hello\",\"data2\":42},"
        + "{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&0\",\"data1\":\"World\",\"data2\":123}"
        + "]",
      serializedString
    );
  }
}
