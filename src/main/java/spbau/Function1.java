package spbau;

/**
 * Function of one argument
 * @param <T> input argument
 * @param <R> output argument
 */
@FunctionalInterface
public interface Function1<T, R> {

  /**
   * Compute the function with the bound argument <tt>t</tt>
   * @param arg the argument
   * @return the result of computation
   */
  R apply(T arg);

  /**
   * @param g the second function to apply
   * @param <E> the result type of the <tt>g</tt> function
   * @return function-composition, g(f(x))
   */
  default <E> Function1<T, E> compose(Function1<? super R, ? extends E> g) {
    return (T arg) -> g.apply(this.apply(arg));
  }

}
