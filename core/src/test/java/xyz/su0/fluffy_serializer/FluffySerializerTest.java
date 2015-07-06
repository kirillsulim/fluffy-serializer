package xyz.su0.fluffy_serializer;

import org.junit.*;
import static org.junit.Assert.*;

import xyz.su0.fluffy_serializer.exceptions.*;


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
  public void shouldSerializeSimpleObject() throws FluffySerializationException, FluffyNotSerializableException {
    SimpleObject o = new SimpleObject();
    o.data1 = 42;
    o.data2 = "Hello World!";

    String result = sz.serialize(o);

    assertEquals("[{\"@class\":\"xyz.su0.fluffy_serializer.SimpleObject\",\"data1\":42,\"data2\":\"Hello World!\"}]", result);
  }

  @Test
  public void shouldDeserializeSimpleObject() throws FluffyParseException {
    String data = "[{\"@class\":\"xyz.su0.fluffy_serializer.SimpleObject\",\"data1\":42,\"data2\":\"Hello World!\"}]";

    SimpleObject so = (SimpleObject) sz.deserialize(data);

    assertEquals(42, so.data1);
    assertEquals("Hello World!", so.data2);
  }

  @Test
  public void shouldSerializeLinked() throws FluffySerializationException, FluffyNotSerializableException {
    DemoClass o1 = new DemoClass();
    o1.data1 = "Hello";
    o1.data2 = 42;

    DemoClass o2 = new DemoClass();
    o2.data1 = "World";
    o2.data2 = 123;

    DemoClass o3 = new DemoClass();
    o3.data1 = "Today";
    o3.data2 = 2015;

    o1.ref = o2;
    o2.ref = o3;
    o3.ref = o1;

    String serializedString = sz.serialize(o1);

    assertEquals(
      "["
        + "{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&1\",\"data1\":\"Hello\",\"data2\":42},"
        + "{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&2\",\"data1\":\"World\",\"data2\":123},"
        + "{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&0\",\"data1\":\"Today\",\"data2\":2015}"
        + "]",
      serializedString
    );
  }

  @Test
  public void shouldDeserializeLinked() throws FluffyParseException {
    String data = "["
      + "{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&1\",\"data1\":\"Hello\",\"data2\":42},"
      + "{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&2\",\"data1\":\"World\",\"data2\":123},"
      + "{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&0\",\"data1\":\"Today\",\"data2\":2015}"
      + "]";

    DemoClass startPoint = (DemoClass)sz.deserialize(data);
    assertEquals("Hello", startPoint.data1);
    assertEquals(42, startPoint.data2);

    DemoClass second = startPoint.ref;
    assertEquals("World", second.data1);
    assertEquals(123, second.data2);

    DemoClass third = second.ref;
    assertEquals("Today", third.data1);
    assertEquals(2015, third.data2);
    assertEquals(startPoint, third.ref);
  }

  @Test(expected=FluffyParseException.class)
  public void shoudFailWhenDataIsNotJsonArray() throws FluffyParseException {
    String data = "87123nkjnasd7as7dh34jh234";
    sz.deserialize(data);
  }
}
