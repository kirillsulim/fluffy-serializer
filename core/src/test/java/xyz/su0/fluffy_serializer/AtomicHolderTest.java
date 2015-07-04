/*
 *
 */
package xyz.su0.fluffy_serializer;

import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;

import xyz.su0.fluffy_serializer.atomic_serializers.*;


public class AtomicHolderTest {
  AtomicHolder hd;

  @Before
  public void setUp() {
    hd = new AtomicHolder(null);
  }

  @Test
  public void shouldContainClasses() {
    Class intSz = hd.getAtomicSerializerClass(int.class);
    Class integerSz = hd.getAtomicSerializerClass(Integer.class);
    Class stringSz = hd.getAtomicSerializerClass(String.class);

    assertEquals(IntegerSerializer.class, intSz);
    assertEquals(IntegerSerializer.class, integerSz);
    assertEquals(StringSerializer.class, stringSz);
  }

  @Test
  public void shouldReturnSerializers() {
    IAtomicSerializer intSz = hd.getAtomicSerializerInstance(int.class);
    IAtomicSerializer integerSz = hd.getAtomicSerializerInstance(Integer.class);
    IAtomicSerializer stringSz = hd.getAtomicSerializerInstance(String.class);

    assertThat(intSz, instanceOf(IntegerSerializer.class));
  }
}
