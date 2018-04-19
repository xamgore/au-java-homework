package spbau;

/**
 * Function of one argument, returning boolean value.
 * @param <T> input argument
 */
@FunctionalInterface
public interface Predicate<T> extends Function1<T, Boolean> {

  /**
   * A predicate that ignores its argument and returns true
   */
  Predicate<Object> ALWAYS_TRUE = arg -> true;

  /**
   * A predicate that ignores its argument and returns false
   */
  Predicate<Object> ALWAYS_FALSE = ALWAYS_TRUE.not();

  /**
   * Compute the result of predicate, bound with argument <tt>t</tt>
   * @param arg the argument
   * @return the result of computation
   */
  Boolean apply(T arg);

  /**
   * Get lazy disjunction of two predicates this(x), next(x)
   * @param next the second predicate
   * @return new predicate f(x) = this(x) or next(x)
   */
  default Predicate<T> or(Predicate<? super T> next) {
    return (T arg) -> this.apply(arg) || next.apply(arg);
  }

  /**
   * Get lazy conjunction of two predicates this(x), next(x)
   * @param next the second predicate
   * @return new predicate f(x) = this(x) and next(x)
   */
  default Predicate<T> and(Predicate<? super T> next) {
    return (T arg) -> this.apply(arg) && next.apply(arg);
  }

  /**
   * Get negation of the predicate
   * @return new predicate f(x) = not this(x)
   */
  default Predicate<T> not() {
    return (T arg) -> !this.apply(arg);
  }

}
