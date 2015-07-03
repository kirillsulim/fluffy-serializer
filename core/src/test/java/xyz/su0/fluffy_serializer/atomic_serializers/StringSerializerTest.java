/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;


public class StringSerializerTest {
  StringSerializer sz;

  @Before
  public void setUp() {
    sz = new StringSerializer();
  }

  @Test
  public void shouldSerializeSimpleString() {
    String serializedString = sz.serialize("This is simple string.");
    assertEquals("This is simple string.", serializedString);
  }

  @Test
  public void shouldDeserializeSimpleString() {
    String deserializedString = sz.deserialize("This is simple string.");
    assertEquals("This is simple string.", deserializedString);
  }

  @Test
  public void shouldSerializeStringContainingQuotes() {
    String serializedString = sz.serialize("String with some \"strange symbols\"");
    assertEquals("String with some \\\"strange symbols\\\"", serializedString);
  }

  @Test
  public void shouldDeserializeStringContainingQuotes() {
    String deserializedString = sz.deserialize("String with some \\\"strange symbols\\\"");
    assertEquals("String with some \"strange symbols\"", deserializedString);
  }
}
