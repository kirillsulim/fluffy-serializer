package xyz.su0.fluffy_serializer;

import org.junit.*;
import static org.junit.Assert.*;

import xyz.su0.fluffy_serializer.exceptions.*;
import xyz.su0.fluffy_serializer.annotations.*;

@FluffySerializable
class SimpleObject {
  public int data1;
  public String data2;
}

@FluffySerializable
class DemoClass {
  public DemoClass ref;
  public String data1;
  public int data2;
}

class NotAnnotated {
  public int data;
}

@FluffySerializable
class WithLinkToNotAnnotated {
  public NotAnnotated link;
}

@FluffySerializable
class Base {
  private String data1;

  public String getData1() {
    return data1;
  }

  public void setData1(String s) {
    data1 = s;
  }
}

@FluffySerializable
class Extend extends Base {
  public String data2;
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
  public void shouldFailWhenDataIsNotJsonArray() throws FluffyParseException {
    String data = "87123nkjnasd7as7dh34jh234";
    sz.deserialize(data);
  }

  @Test(expected=FluffyNotSerializableException.class)
  public void shouldFailIfClassNotAnnotatedAsFluffySerializable() throws FluffyNotSerializableException, FluffySerializationException {
    NotAnnotated notAnnotated = new NotAnnotated();
    sz.serialize(notAnnotated);
  }

  @Test(expected=FluffyNotSerializableException.class)
  public void shouldFailIfClassContainReferenceToNotAnnotated() throws FluffyNotSerializableException, FluffySerializationException {
    WithLinkToNotAnnotated withLink = new WithLinkToNotAnnotated();
    sz.serialize(withLink);
  }

  @Test
  public void shouldSerializeWithNullReferences() throws FluffyNotSerializableException, FluffySerializationException {
    DemoClass noRef = new DemoClass();
    String serialized = sz.serialize(noRef);
    assertEquals("[{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&null\",\"data1\":\"\",\"data2\":0}]", serialized);
  }

  @Test
  public void shouldDeserializeWithNullReferences() throws FluffyParseException, FluffySerializationException {
    String data = "[{\"@class\":\"xyz.su0.fluffy_serializer.DemoClass\",\"ref\":\"&null\",\"data1\":\"\",\"data2\":0}]";
    DemoClass result = (DemoClass)sz.deserialize(data);
    assertNull(result.ref);
    assertEquals("", result.data1);
    assertEquals(0, result.data2);
  }

  @Test
  public void shouldSerializeExtendedClass() throws FluffyNotSerializableException, FluffySerializationException {
    Extend ex = new Extend();
    ex.setData1("From base");
    ex.data2 = "From child";

    assertEquals("[{\"@class\":\"xyz.su0.fluffy_serializer.Extend\",\"data2\":\"From child\",\"data1\":\"From base\"}]", sz.serialize(ex));
  }

  @Test
  public void shouldDeserializeExtendedClass() throws FluffyParseException, FluffySerializationException {
    String data = "[{\"@class\":\"xyz.su0.fluffy_serializer.Extend\",\"data2\":\"From child\",\"data1\":\"From base\"}]";
    Extend ex = (Extend)sz.deserialize(data);
    assertEquals("From base", ex.getData1());
    assertEquals("From child", ex.data2);
  }
}
