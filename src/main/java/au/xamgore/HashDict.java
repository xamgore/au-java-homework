package au.xamgore;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;


public final class HashDict<K, V> implements Dictionary<K, V> {
  private static final int INITIAL_BUCKETS_COUNT = 10;
  private static final int AVERAGE_BUCKET_SIZE = 8;

  private ArrayList<LinkedList<Pair<K, V>>> buckets;
  private final double loadFactor;
  private int size;


  public HashDict(double loadFactor) {
    this.loadFactor = loadFactor;
  }

  public HashDict() {
    this(0.75d);
  }

  {
    clear();
  }

  /**
   * @return the number of keys in the dictionary
   */
  @Override
  public int size() {
    return size;
  }

  /**
   * @return true, if the key is stored in the dictionary
   */
  @Override
  public boolean contains(@Nullable K key) {
    return get(key) != null;
  }

  /**
   * @return the value associated with the key.
   * If there is no such, null is returned.
   */
  @Nullable
  @Override
  public V get(@Nullable K key) {
    return key == null ? null
      : getPair(key).map(Pair::getValue).orElse(null);
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
    if (key == null || value == null) {
      throw new IllegalArgumentException();
    }

    List<Pair<K, V>> bucket = getBucket(key);

    return getPair(key, bucket)
      .orElseGet(() -> createNewPair(key, bucket))
      .setVal(value);
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
      return null;
    }

    List<Pair<K, V>> bucket = getBucket(key);
    Pair<K, V> dead = getPair(key, bucket).orElse(null);

    if (dead == null) {
      return null;
    }

    bucket.removeIf(p -> p.getKey() == dead.getKey());
    size--;
    shrink();

    return dead.getValue();
  }

  /**
   * Removes all data from the dictionary.
   */
  @Override
  public void clear() {
    changeBucketsCount(INITIAL_BUCKETS_COUNT, false);
  }


  private void changeBucketsCount(int count, boolean moveOld) {
    ArrayList<LinkedList<Pair<K, V>>> oldBuckets = buckets;

    // add more buckets
    buckets = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      buckets.add(new LinkedList<>());
    }

    if (oldBuckets == null || !moveOld) {
      size = 0;
      return;
    }

    // move pairs from old list to new one
    for (List<Pair<K, V>> b : oldBuckets) {
      for (Pair<K, V> pair : b) {
        getBucket(pair.getKey()).add(pair);
      }
    }
  }

  @NotNull
  private List<Pair<K, V>> getBucket(@NotNull K key) {
    return buckets.get(Math.abs(key.hashCode() % buckets.size()));
  }

  @NotNull
  private Optional<Pair<K, V>> getPair(@NotNull K key) {
    return getPair(key, getBucket(key));
  }

  @NotNull
  private Optional<Pair<K, V>> getPair(@NotNull K key, List<Pair<K, V>> bucket) {
    return bucket.stream()
      .filter(pair -> pair.getKey().equals(key))
      .findFirst();
  }

  @NotNull
  private Pair<K, V> createNewPair(@NotNull K key, List<Pair<K, V>> bucket) {
    Pair<K, V> instance = new Pair<>(key, null);
    bucket.add(instance);

    size++;
    expand();
    return instance;
  }

  private double getLoadFactor() {
    return (double) size / (buckets.size() * AVERAGE_BUCKET_SIZE);
  }

  private void expand() {
    if (getLoadFactor() > loadFactor)
      changeBucketsCount(buckets.size() * 2, true);
  }

  private void shrink() {
    int count = Math.max(INITIAL_BUCKETS_COUNT, size / AVERAGE_BUCKET_SIZE);
    if (getLoadFactor() < loadFactor / 2)
      changeBucketsCount(count, true);
  }
}
