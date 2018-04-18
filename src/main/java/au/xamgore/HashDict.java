package au.xamgore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.Math.floorMod;


public final class HashDict<K, V> implements Dictionary<K, V> {

  private static final double DEFAULT_LOAD_FACTOR = 0.75d;
  private static final int INITIAL_BUCKETS_COUNT = 8;
  private static final int RESIZE_FACTOR = 2;

  private ArrayList<LinkedList<Pair<K, V>>> buckets;
  private final double loadFactor;
  private int size;

  private Box<V> nullKeyInstance = Box.empty();


  public HashDict(double loadFactor) {
    assert loadFactor > 0 && loadFactor <= 1;
    this.loadFactor = loadFactor;
  }

  public HashDict() {
    this(DEFAULT_LOAD_FACTOR);
  }

  {
    allocateBuckets(INITIAL_BUCKETS_COUNT);
  }

  /**
   * @return the number of keys in the dictionary
   */
  @Override
  public int size() {
    int numberOfKeysEqualToNull = nullKeyInstance.isPresent() ? 1 : 0;
    return size + numberOfKeysEqualToNull;
  }

  /**
   * @return true, if the key is stored in the dictionary
   */
  @Override
  public boolean contains(@Nullable K key) {
    return key == null
      ? nullKeyInstance.isPresent()
      : getPairBy(key, chooseBucket(key)) != null;
  }

  /**
   * @return the value associated with the key.
   * If there is no such, null is returned.
   */
  @Nullable
  @Override
  public V get(@Nullable K key) {
    if (key == null) {
      return nullKeyInstance.unbox();
    }

    Pair<K, V> pair = getPairBy(key, chooseBucket(key));
    return pair == null ? null : pair.getValue();
  }

  /**
   * Associate the value with the key.
   * Does rehashing if necessary.
   *
   * @return previously stored value,
   * or null if there is no such.
   */
  @Nullable
  @Override
  public V put(@Nullable K key, @Nullable V value) {
    if (key == null) {
      return nullKeyInstance.set(value);
    }

    List<Pair<K, V>> bucket = chooseBucket(key);
    Pair<K, V> pair = getPairBy(key, bucket);

    if (pair == null) {
      size += 1;
      bucket.add(new Pair<>(key, value));
      expandIfNeeded();
      return null;
    } else {
      return pair.setVal(value);
    }
  }

  /**
   * Remove the key from the dictionary.
   * Does rehashing if necessary.
   *
   * @return previously stored value,
   * or null if there is no such.
   */
  @Nullable
  @Override
  public V remove(@Nullable K key) {
    if (key == null) {
      return nullKeyInstance.remove();
    }

    List<Pair<K, V>> bucket = chooseBucket(key);
    Pair<K, V> pair = getPairBy(key, bucket);

    if (pair == null) {
      return null;
    } else {
      size -= 1;
      bucket.removeIf(p -> p.getKey().equals(key));
      shrinkIfNeeded();
      return pair.getValue();
    }
  }

  /**
   * Removes all data from the dictionary.
   */
  @Override
  public void clear() {
    nullKeyInstance.remove();
    buckets.forEach(List::clear);
    size = 0;
  }

  private void allocateBuckets(int count) {
    buckets = new ArrayList<>(count);

    Stream.generate(LinkedList<Pair<K, V>>::new)
      .limit(count).forEach(buckets::add);
  }

  private void reallocateBuckets(int count) {
    List<LinkedList<Pair<K, V>>> outdated = buckets;
    allocateBuckets(count);

    // put() increases the size counter
    size = 0;

    outdated.forEach(list -> list.forEach(pair -> {
      put(pair.getKey(), pair.getValue());
    }));
  }


  /**
   * @param key
   * @return return the bucket where the element with <tt>key</tt>
   * may be stored
   */
  @NotNull
  private List<Pair<K, V>> chooseBucket(@NotNull K key) {
    return buckets.get(floorMod(key.hashCode(), buckets.size()));
  }


  /**
   * @param key
   * @param bucket is place to search
   * @return return the pair, that stores the value associated with key.
   * Or null if it was not found.
   */
  @Nullable
  private Pair<K, V> getPairBy(@NotNull K key, List<Pair<K, V>> bucket) {
    for (Pair<K, V> pair : bucket) {
      if (pair.getKey().equals(key)) {
        return pair;
      }
    }

    return null;
  }


  private void expandIfNeeded() {
    // the fullness is too big for the dict
    if (buckets.size() * loadFactor < size)
      reallocateBuckets(buckets.size() * RESIZE_FACTOR);
  }

  private void shrinkIfNeeded() {
    int newBucketsSize = buckets.size() / RESIZE_FACTOR;

    // the fullness must be ok after shrinking
    if (newBucketsSize * loadFactor > size && buckets.size() > 1)
      reallocateBuckets(newBucketsSize);
  }
}
