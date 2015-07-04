/*
 *
 */
package xyz.su0.fluffy_serializer;

import org.junit.*;
import static org.junit.Assert.*;



public class FluffySerializerTest {
  FluffySerializer sz;

  @Before
  public void setUp() {
    sz = new FluffySerializer();
  }

  @Test
  public void shouldSerializeSimpleObject() {
    class SimpleObject {
      public int data1;
      public String data2;
    }

    SimpleObject o = new SimpleObject();
    o.data1 = 42;
    o.data2 = "Hello World!";

    String result = sz.serialize(o);

    assertEquals("[{\"data1\":42,\"data2\":\"Hello World!\"}]", result);
  }

  @Test
  public void shouldSerializeLinked() {
    class DemoClass {
      public DemoClass ref;
      public String data1;
      public int data2;
    }

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
      "[{\"ref\":\"&1\",\"data1\":\"Hello\",\"data2\":42},{\"ref\":\"&0\",\"data1\":\"World\",\"data2\":123}]",
      serializedString
    );
  }
}
