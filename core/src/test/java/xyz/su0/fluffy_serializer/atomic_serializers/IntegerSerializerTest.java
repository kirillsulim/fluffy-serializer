/**
 *
 */

package xyz.su0.fluffy_serializer.atomic_serializers;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;


public class IntegerSerializerTest {
  IntegerSerializer sz;

  @Before
  public void setUp() {
    sz = new IntegerSerializer();
  }

  @Test
  public void shouldSerializeInt() {
    String serializedString = sz.serialize(42);
    assertEquals("42", serializedString);
  }

  @Test
  public void shouldDeserializeInt() {
    int value = sz.deserialize("42");
    assertEquals(42, value);
  }
}
