package spbau;

import java.util.*;

public class Collections {

  /**
   * Reduce the collection with function <tt>func</tt>.
   *
   * @return result of the fold
   * @see <a href="https://en.wikipedia.org/wiki/Fold_(higher-order_function)">wikipedia/fold</a>
   */
  public static <A, B> B foldr(Function2<? super A, ? super B, ? extends B> func, B zero, Collection<? extends A> coll) {
    Deque<A> storage = new LinkedList<>();

    for (A elem : coll) {
      storage.addLast(elem);
    }

    // send from call to storage in reversed order
    return foldl(func.flip(), zero, storage);
  }

  /**
   * Reduce the collection with function <tt>func</tt>.
   *
   * @return result of the fold
   * @see <a href="https://en.wikipedia.org/wiki/Fold_(higher-order_function)">wikipedia/fold</a>
   */
  public static <A, B> A foldl(Function2<? super A, ? super B, ? extends A> func, A zero, Collection<? extends B> coll) {
    A acc = zero;

    for (B b : coll) {
      acc = func.apply(acc, b);
    }

    return acc;
  }

  /**
   * Apply the transformation function to elements of the collection.
   */
  public static <T, R> Collection<R> map(Function1<? super T, ? extends R> func, Collection<? extends T> coll) {
    List<R> acc = new ArrayList<>();

    for (T t : coll) {
      acc.add(func.apply(t));
    }

    return acc;
  }

  /**
   * Grab elements for which the predicate holds.
   */
  public static <T> Collection<T> filter(Predicate<? super T> predicate, Collection<? extends T> coll) {
    List<T> acc = new ArrayList<>();

    for (T t : coll) {
      if (predicate.apply(t)) {
        acc.add(t);
      }
    }

    return acc;
  }


  /**
   * Grab elements while the predicate holds.
   */
  public static <T> Collection<T> takeWhile(Predicate<? super T> predicate, Collection<? extends T> coll) {
    List<T> acc = new ArrayList<>();

    for (T t : coll) {
      if (!predicate.apply(t))
        break;

      acc.add(t);
    }

    return acc;
  }

  /**
   * Grab elements while the predicate not holds.
   */
  public static <T> Collection<T> takeUntil(Predicate<? super T> predicate,
                                            Collection<? extends T> coll) {
    return takeWhile(predicate.not(), coll);
  }

}
