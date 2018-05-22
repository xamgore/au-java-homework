import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spbau.Function1;
import spbau.Function2;
import spbau.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static spbau.Collections.*;

class TestCollection {
  @Test
  void testTakeWhile() {
    List<Integer> empty = Collections.emptyList();

    List<Integer> one = singletonList(1);
    List<Integer> ones = asList(1, 1, 1);
    List<Integer> oneZero = asList(1, 1, 1, 0, 0, 0, 1);

    List<Integer> zero = singletonList(0);
    List<Integer> zeros = asList(0, 0, 0);
    List<Integer> zeroOne = asList(0, 0, 0, 1, 1, 1, 0);

    Predicate<Integer> isOne = i -> i.equals(1);

    assertIterableEquals(empty, takeWhile(isOne, empty));
    assertIterableEquals(one, takeWhile(isOne, one));
    assertIterableEquals(ones, takeWhile(isOne, ones));
    assertIterableEquals(ones, takeWhile(isOne, oneZero));

    assertIterableEquals(empty, takeWhile(isOne, zero));
    assertIterableEquals(empty, takeWhile(isOne, zeros));
    assertIterableEquals(empty, takeWhile(isOne, zeroOne));
  }

  @Test
  void testTakeUntil() {
    List<Integer> empty = Collections.emptyList();

    List<Integer> one = singletonList(1);
    List<Integer> ones = asList(1, 1, 1);
    List<Integer> oneZero = asList(1, 1, 1, 0, 0, 0, 1);

    List<Integer> zero = singletonList(0);
    List<Integer> zeros = asList(0, 0, 0);
    List<Integer> zeroOne = asList(0, 0, 0, 1, 1, 1, 0);

    Predicate<Integer> isZero = i -> i.equals(0);

    assertIterableEquals(empty, takeUntil(isZero, empty));
    assertIterableEquals(one, takeUntil(isZero, one));
    assertIterableEquals(ones, takeUntil(isZero, ones));
    assertIterableEquals(ones, takeUntil(isZero, oneZero));

    assertIterableEquals(empty, takeUntil(isZero, zero));
    assertIterableEquals(empty, takeUntil(isZero, zeros));
    assertIterableEquals(empty, takeUntil(isZero, zeroOne));
  }

  @Test
  void testFilter() {
    List<Integer> empty = Collections.emptyList();

    List<Integer> one = singletonList(1);
    List<Integer> ones = asList(1, 1, 1);
    List<Integer> oneZero = asList(1, 1, 1, 0, 0, 0, 1);

    List<Integer> zero = singletonList(0);
    List<Integer> zeros = asList(0, 0, 0);
    List<Integer> zeroOne = asList(0, 0, 0, 1, 1, 1, 0);

    Predicate<Integer> isOne = i -> i.equals(1);

    assertIterableEquals(empty, filter(isOne, empty));
    assertIterableEquals(one, filter(isOne, one));
    assertIterableEquals(ones, filter(isOne, ones));
    assertIterableEquals(asList(1, 1, 1, 1), filter(isOne, oneZero));

    assertIterableEquals(empty, filter(isOne, zero));
    assertIterableEquals(empty, filter(isOne, zeros));
    assertIterableEquals(ones, filter(isOne, zeroOne));
  }

  @Test
  void testMap() {
    List<Integer> empty = Collections.emptyList();
    List<Integer> zero = singletonList(0);
    List<Integer> zeros = asList(0, 0, 0);
    List<Integer> one = singletonList(1);
    List<Integer> ones = asList(1, 1, 1);
    List<Integer> oneZero = asList(1, 1, 1, 0, 0, 0, 1);

    Function1<Integer, Integer> add1 = i -> i + 1;
    assertIterableEquals(empty, map(add1, empty));
    assertIterableEquals(one, map(add1, zero));
    assertIterableEquals(ones, map(add1, zeros));
    assertIterableEquals(asList(2, 2, 2, 1, 1, 1, 2), map(add1, oneZero));
  }

  @Test
  void testFoldl() {
    List<Integer> empty = Collections.emptyList();
    List<Integer> one = singletonList(1);
    List<Integer> ones = asList(1, 2, 3);

    // structural operation
    Function2<Integer, Integer, Integer> length = (acc, x) -> acc + 1;
    assertEquals(empty.size(), (int) foldl(length, 0, empty));
    assertEquals(one.size(), (int) foldl(length, 0, one));
    assertEquals(ones.size(), (int) foldl(length, 0, ones));

    // non-associative operation
    Function2<Integer, Integer, Integer> diff = (acc, x) -> acc - x;
    assertEquals(0, (int) foldl(diff, 0, empty));
    assertEquals(-1, (int) foldl(diff, 0, one));
    assertEquals(-6, (int) foldl(diff, 0, ones));
  }

  @Test
  void testFoldr() {
    List<Integer> empty = Collections.emptyList();
    List<Integer> one = singletonList(1);
    List<Integer> ones = asList(1, 2, 3);

    // structural operation
    Function2<Integer, Integer, Integer> length = (x, acc) -> acc + 1;
    assertEquals(empty.size(), (int) foldr(length, 0, empty));
    assertEquals(one.size(), (int) foldr(length, 0, one));
    assertEquals(ones.size(), (int) foldr(length, 0, ones));

    // non-associative operation
    Function2<Integer, Integer, Integer> diff = (x, acc) -> x - acc;
    assertEquals(0, (int) foldr(diff, 0, empty));
    assertEquals(1, (int) foldr(diff, 0, one));
    assertEquals(2, (int) foldr(diff, 0, ones));
  }

  @Test
  void testFoldr2() {
    List<Integer> nums = asList(1, 2, 3);
    List<Integer> history = foldr((x, y) -> { y.add(x); return y; }, new ArrayList(), nums);
    assertEquals(asList(3, 2, 1), history);
  }

}
