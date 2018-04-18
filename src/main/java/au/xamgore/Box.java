package au.xamgore;

import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The instance of the class holds the true value of type T.
 * So now it it is easy to distinguish the following states:
 *
 * <li> There is no T instance at all</li>
 * <li> There is T instance, null</li>
 * <li>There is T instance, not null</li>
 * @param <T>
 */
public class Box<T> {
  private T value = null;
  private boolean isPresent = false;

  public Box() { }

  public Box(@Nullable T value) {
    this.value = value;
    isPresent = true;
  }

  @Nullable
  public T unbox() {
    return value;
  }

  /**
   *
   * @param newValue
   * @return contained value
   */
  @Nullable
  public T set(T newValue) {
    isPresent = true;
    T previous = value;
    value = newValue;
    return previous;
  }

  /**
   * @return contained value
   */
  @Nullable
  public T remove() {
    isPresent = false;
    T previous = value;
    value = null;
    return previous;
  }

  public boolean isPresent() {
    return isPresent;
  }

  public static <T> Box<T> empty() {
    return new Box<>();
  }
}
