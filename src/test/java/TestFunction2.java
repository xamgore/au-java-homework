import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbau.Function1;
import spbau.Function2;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestFunction2 {
  private void add(int i) {
    history.add(i);
  }

  private void clear() {
    history.clear();
  }

  private List<Integer> history;

  private Function2<Integer, Integer, Integer> add1 = (x, y) -> {
    add(1);
    return x + y;
  };

  private Function2<Integer, Integer, Integer> mul2 = (x, y) -> {
    add(2);
    return x * y;
  };

  private Function1<Integer, Integer> call3 = i -> {
    add(3);
    return null;
  };

  private Function1<Integer, Integer> call4 = i -> {
    add(4);
    return null;
  };

  private Function2<Integer, Integer, Integer> call5 = (x, y) -> {
    add(5);
    return null;
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
    assertEquals(0, (int) add1.apply(0, 0));
    assertEquals(5, (int) add1.apply(0, 5));
    assertEquals(3, (int) add1.apply(3, 0));
    assertEquals(8, (int) add1.apply(5, 3));
    assertHistory(1, 1, 1, 1);
    clear();
  }

  @Test
  void testBindLeft() {
    // assert is lazy
    call5.bind1(0);
    assertHistory();

    // assert calls the function
    call5.bind1(0).apply(2);
    assertHistory(5);
  }

  @Test
  void testBindRight() {
    // assert is lazy
    call5.bind2(0);
    assertHistory();

    // assert calls the function
    call5.bind2(0).apply(2);
    assertHistory(5);
  }

  @Test
  void testCompositionWorks() {
    // assert is lazy
    call5.compose(call3);
    assertHistory();

    // assert calls the function
    call5.compose(call3).apply(0, 0);
    assertHistory(5, 3);
  }

  @Test
  void testFlip() {
    // assert is lazy
    call5.flip();
    assertHistory();

    // assert calls the function
    call5.flip().apply(0, 0);
    assertHistory(5);
    clear();

    // assert flip flips
    Function2<Integer, Integer, Integer> left = (a, b) -> a;
    Function2<Integer, Integer, Integer> right = (a, b) -> b;

    assertEquals(1, (int) left.apply(1, 2));
    assertEquals(2, (int) left.flip().apply(1, 2));

    assertEquals(2, (int) right.apply(1, 2));
    assertEquals(1, (int) right.flip().apply(1, 2));
  }

  @Test
  void testCurry() {
    // assert is lazy
    call5.curry();
    assertHistory();

    // assert one call is lazy
    call5.curry().apply(1);
    assertHistory();

    // assert two calls compute result
    call5.curry().apply(1).apply(2);
    assertHistory(5);

    // assert curring doesn't spoil the arguments
    Function2<Integer, Integer, Integer> left = (a, b) -> a;
    Function2<Integer, Integer, Integer> right = (a, b) -> b;

    assertEquals(1, (int) left.curry().apply(1).apply(2));
    assertEquals(2, (int) right.curry().apply(1).apply(2));
  }
}
