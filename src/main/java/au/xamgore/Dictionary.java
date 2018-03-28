package au.xamgore;

import org.jetbrains.annotations.*;

public interface Dictionary<K, V> {

  /**
   * @return the number of keys in the dictionary
   */
  int size();

  /**
   * @return true, if the key is stored in the dictionary
   */
  boolean contains(@NotNull K key);

  /**
   * @return the value associated with the key.
   * If there is no such, null is returned.
   */
  @Nullable V get(@NotNull K key);

  /**
   * Associate the value with the key.
   * Does rehashing if necessary.
   *
   * @return previously stored value,
   * or null if there is no such.
   */
  @Nullable V put(@NotNull K key, @Nullable V value);

  /**
   * Remove the key from the dictionary.
   * Does rehashing if necessary.
   *
   * @return previously stored value,
   * or null if there is no such.
   */
  @Nullable V remove(@NotNull K key);

  /**
   * Removes all data from the dictionary.
   */
  void clear();
}
