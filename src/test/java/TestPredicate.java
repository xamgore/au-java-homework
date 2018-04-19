import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbau.Function1;
import spbau.Predicate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static spbau.Predicate.ALWAYS_FALSE;
import static spbau.Predicate.ALWAYS_TRUE;

class TestPredicate {
  private void add(int i) {
    history.add(i);
  }

  private void clear() {
    history.clear();
  }

  private List<Integer> history;

  private Predicate<Integer> isSix = i -> {
    add(6);
    return i == 6;
  };

  private Predicate<Integer> isTwo = i -> {
    add(2);
    return i == 2;
  };

  private Predicate<Integer> isEven = i -> {
    add(1);
    return i == 3;
  };

  @BeforeEach
  void createArray() {
    history = new ArrayList<>();
  }

  private void assertHistory(Integer... idxes) {
    for (Integer i : idxes) {
      assertTrue(history.contains(i), "Idx " + i + " not in history");
    }

    assertEquals(idxes.length, history.size());
  }

  @Test
  void testApplyReturnsResult() {
    // basic operations
    assertTrue(isSix.apply(6));
    assertFalse(isSix.apply(0));

    assertHistory(6, 6);
  }

  @Test
  void testNot() {
    // is lazy
    isSix.not();

    // works
    assertFalse(isSix.not().apply(6));
    assertTrue(isSix.not().apply(0));

    assertHistory(6, 6);
  }

  @Test
  void testAlwaysConstants() {
    assertTrue(Predicate.ALWAYS_TRUE.apply(0));
    assertTrue(Predicate.ALWAYS_TRUE.apply(1));

    assertFalse(ALWAYS_FALSE.apply(0));
    assertFalse(ALWAYS_FALSE.apply(1));

    assertFalse(Predicate.ALWAYS_TRUE.not().apply(0));
    assertTrue(ALWAYS_FALSE.not().apply(0));

    assertTrue(ALWAYS_FALSE.or(Predicate.ALWAYS_TRUE).apply(0));
    assertFalse(ALWAYS_FALSE.and(Predicate.ALWAYS_TRUE).apply(0));
  }

  @Test
  void testOr() {
    // is lazy
    isSix.or(ALWAYS_TRUE);
    assertHistory();

    isSix.or(isTwo).apply(6);
    assertHistory(6);
    clear();

    // works
    isSix.or(isTwo).apply(2);
    assertHistory(6, 2);
    clear();

    // table
    assertFalse(ALWAYS_FALSE.or(ALWAYS_FALSE).apply(0));
    assertTrue(ALWAYS_FALSE.or(ALWAYS_TRUE).apply(0));
    assertTrue(ALWAYS_TRUE.or(ALWAYS_FALSE).apply(0));
    assertTrue(ALWAYS_TRUE.or(ALWAYS_TRUE).apply(0));
  }

  @Test
  void testAnd() {
    // is lazy
    isSix.and(ALWAYS_TRUE);
    assertHistory();

    isSix.and(isTwo).apply(2);
    assertHistory(6);
    clear();

    // works
    isSix.and(isTwo).apply(6);
    assertHistory(6, 2);
    clear();

    // table
    assertFalse(ALWAYS_FALSE.and(ALWAYS_FALSE).apply(0));
    assertFalse(ALWAYS_FALSE.and(ALWAYS_TRUE).apply(0));
    assertFalse(ALWAYS_TRUE.and(ALWAYS_FALSE).apply(0));
    assertTrue(ALWAYS_TRUE.and(ALWAYS_TRUE).apply(0));
  }
}
