
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbau.Function1;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TestFunction1 {
  private void add(int i) {
    history.add(i);
  }

  private void clear() {
    history.clear();
  }

  private List<Integer> history;

  private Function1<Integer, Integer> add1 = i -> { add(1); return i + 1; };

  private Function1<Integer, Integer> mul2 = i -> { add(2); return i * 2; };


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
    assertEquals(1, (int) add1.apply(0));
    assertEquals(43, (int) add1.apply(42));

    assertHistory(1, 1);
  }

  @Test
  void testCompositionWorks() {
    // basic operations
    assertEquals(1, (int) mul2.compose(add1).apply(0));
    assertHistory(2, 1);
    clear();

    assertEquals(2, (int) add1.compose(mul2).apply(0));
    assertHistory(1, 2);
    clear();

    assertEquals(2, (int) add1.compose(add1).apply(0));
    assertHistory(1, 1);
    clear();
  }
}
