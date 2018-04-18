import au.xamgore.HashDict;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.HashMap;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


public class TestHashDict {

  private HashDict<Integer, String> d;

  @BeforeEach
  void setUp() {
    d = new HashDict<>();
  }

  @Test
  void dictDoesNotContainElementsWhenEmpty() {
    assertFalse(d.contains(0));
    assertFalse(d.contains(10));
    assertFalse(d.contains(-10));
  }

  @Test
  void dictContainsAddedElements() {
    assertNull(d.put(0, "kek"));
    assertTrue(d.contains(0));

    assertEquals("kek", d.put(0, "new"));
    assertTrue(d.contains(0));
  }

  @Test
  void dictDoesNotContainsElementsAfterRemoval() {
    assertNull(d.put(0, "kek"));
    assertTrue(d.contains(0));

    assertEquals("kek", d.remove(0));
    assertFalse(d.contains(0));
  }

  @Test
  void dictReturnsNullIfNullIsPassedToGet() {
    assertNull(d.get(null));
  }

  @Test
  void dictReturnsNullIfNullIsPassedToRemove() {
    assertNull(d.remove(null));
  }

  @Test
  void dictDoesNotContainsNullElements() {
    assertFalse(d.contains(null));
  }

  @Test
  void dictAllowsToPutInvalidValues() {
    assertEquals(0, d.size());

    assertNull(d.put(null, "val"));
    assertEquals(1, d.size());

    assertEquals("val", d.put(null, null));
    assertEquals(1, d.size());

    assertNull(d.put(0, null));
    assertEquals(2, d.size());

    assertNull(d.put(0, "kek"));
    assertEquals(2, d.size());

    assertEquals("kek", d.put(0, null));
    assertEquals(2, d.size());
  }

  @Test
  void dictCorrectlyChangesSize() {
    assertEquals(0, d.size());

    // reaction on put
    d.put(0, "kek");
    assertEquals(1, d.size());

    d.put(1, "val");
    assertEquals(2, d.size());

    d.put(null, "kek");
    assertEquals(3, d.size());

    // reaction on delete
    d.remove(0);
    assertEquals(2, d.size());

    d.remove(null);
    assertEquals(1, d.size());

    d.remove(1);
    assertEquals(0, d.size());

    // and again just to ensure buckets are recreated
    d.put(0, "kek");
    assertEquals(1, d.size());
    d.put(1, "val");
    assertEquals(2, d.size());
    d.remove(0);
    assertEquals(1, d.size());
    d.remove(1);
    assertEquals(0, d.size());
  }

  @Test
  void clearChangesSize() {
    final int count = 10;

    for (int i = 0; i < count; i++) {
      d.put(i, "val");
    }

    for (int i = 0; i < count; i++) {
      assertTrue(d.contains(i), "iteration " + i);
    }

    assertEquals(count, d.size());

    d.clear();
    assertEquals(0, d.size());

    for (int i = 0; i < count; i++) {
      assertFalse(d.contains(i), "iteration " + i);
    }
  }

  @Test
  void canAddToMapAfterClean() {
    d.clear();
    d.put(0, "kek");
    d.clear();

    assertEquals(0, d.size());
    assertNull(d.put(0, "val"));
    assertEquals(1, d.size());
    assertTrue(d.contains(0));
    assertEquals("val", d.get(0));
  }

  @Test
  void removeInexistingElementReturnsNull() {
    assertNull(d.remove(2));
    assertEquals(0, d.size());

    d.put(0, "kek");
    d.put(1, "val");
    assertNull(d.remove(2));
    assertEquals(2, d.size());

    assertNull(d.remove(2));
    assertEquals(2, d.size());
    assertEquals("kek", d.get(0));
    assertEquals("val", d.get(1));
  }

  @Test
  void removeDoesNotBreakTheOtherKeys() {
    d.put(0, "kek");
    d.put(1, "val");

    assertNull(d.remove(2));
    assertEquals(2, d.size());

    assertEquals("kek", d.get(0));
    assertEquals("val", d.get(1));

    assertEquals("kek", d.remove(0));
    assertNull(d.remove(2));
    assertEquals(1, d.size());
  }

  @Test
  void testOnObjectWithHashEqualToMinValue() {
    HashDict<Object, Integer> map = new HashDict<>();

    Object problem = new Object() {
      public int hashCode() {
        return Integer.MIN_VALUE;
      }
    };

    map.put(problem, 42);
    assertTrue(map.contains(problem));
  }

  private void smokeTest(int threshold) {
    for (int i = 0; i < 1000; i++) {
      HashMap<Integer, String> r = new HashMap<>();
      d = new HashDict<>();
      Random rnd = new Random(i);

      for (int ops = 0; ops < 10000; ops++) {
        if (rnd.nextInt(10) < threshold) {
          int sz = d.size();
          int k = rnd.nextInt(100);
          String v = d.get(k);
          String nv = "" + ops;

          assertEquals(v, r.put(k, nv));
          assertEquals(v, d.put(k, nv));

          assertEquals(r.size(), d.size());
          assertEquals(v == null ? sz + 1 : sz, d.size());

          assertTrue(r.containsKey(k));
          assertTrue(d.contains(k));
        } else {
          int sz = d.size();
          int k = rnd.nextInt(100);
          String v = d.get(k);
          assertEquals(r.get(k), v);

          assertEquals(v, d.remove(k));
          assertEquals(v, r.remove(k));

          assertEquals(r.size(), d.size());
          assertEquals(v == null ? sz : sz - 1, d.size());

          assertFalse(d.contains(k));
          assertFalse(r.containsKey(k));
        }
      }

    }
  }

  @TestFactory
  Stream<DynamicTest> runSmokeTestsOnAllThresholdsFrom0To10() {
    return IntStream
      .rangeClosed(0, 10)
      .mapToObj(i ->
        dynamicTest("threshold " + i,
          () -> smokeTest(i)));
  }
}
