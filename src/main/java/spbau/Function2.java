package spbau;

/**
 * Function of two arguments
 *
 * @param <T> first input argument
 * @param <V> second input argument
 * @param <R> output argument
 */
@FunctionalInterface
public interface Function2<T, V, R> {

  /**
   * Compute the function with the bound argument <tt>t</tt>
   *
   * @param arg1 first argument
   * @param arg2 second argument
   * @return the result of computation
   */
  R apply(T arg1, V arg2);

  /**
   * @param g   the second function to apply
   * @param <E> the result type of the <tt>g</tt> function
   * @return function-composition, g(f(x, y))
   */
  default <E> Function2<T, V, E> compose(Function1<? super R, ? extends E> g) {
    return (T t, V v) -> g.apply(apply(t, v));
  }

  /**
   * @param left the argument to bind
   * @return function with the only argument, f(_, y)
   */
  default Function1<V, R> bind1(T left) {
    return (V arg) -> apply(left, arg);
  }

  /**
   * @param right the argument to bind
   * @return function with the only argument, f(x, _)
   */
  default Function1<T, R> bind2(V right) {
    return (T arg) -> apply(arg, right);
  }

  /**
   * Turn a function of two arguments into functions of one argument.
   *
   * @return curried function
   * @see <a href="https://en.wikipedia.org/wiki/Currying">wikipedia/currying</a>
   */
  default Function1<T, Function1<V, R>> curry() {
    return (T t) -> (V v) -> apply(t, v);
  }

  /**
   * Flip the arguments of function, f(a, b) -> f(b, a)
   *
   * @return function with flipped arguments
   */
  default Function2<V, T, R> flip() {
    return (V v, T t) -> apply(t, v);
  }

}
