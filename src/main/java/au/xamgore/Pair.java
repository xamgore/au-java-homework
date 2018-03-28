package au.xamgore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Pair<K, V> {
  private final K key;
  private V val;

  Pair(@NotNull K key, @Nullable V val) {
    this.key = key;
    this.val = val;
  }

  @NotNull
  public K getKey() {
    return key;
  }

  @Nullable
  public V getValue() {
    return val;
  }

  /**
   * @return the previous value stored in the pair
   */
  @Nullable
  public V setVal(V newValue) {
    V previous = val;
    val = newValue;
    return previous;
  }
}
