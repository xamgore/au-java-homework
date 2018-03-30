package task;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.lang.Math.min;

public class HashMultiset<E> extends AbstractCollection<E> implements Multiset<E> {

  /**
   * Stores pairs (element, count) in the order preserving way
   */
  private LinkedHashMap<E, Long> buckets = new LinkedHashMap<>();

  /**
   * Stores the summed count of all elements
   */
  private long totalVolume = 0L;

  /**
   * Returns the number of occurrences of an element in this multiset.
   * <p>
   * Expected complexity: Same as `contains`
   * <p>
   * If this collection contains more than <tt>Integer.MAX_VALUE</tt>
   * elements, returns <tt>Integer.MAX_VALUE</tt>.
   */
  @Override
  public int count(Object element) {
    long volume = buckets.getOrDefault(element, 0L);
    return (int) min(volume, Integer.MAX_VALUE);
  }

  /**
   * Returns the set of distinct elements contained in this multiset.
   * <p>
   * Expected complexity: O(1)
   */
  @Override
  public Set<E> elementSet() {
    return buckets.keySet();
  }

  /**
   * @return a read-only set of entries representing the data of this multiset
   * (it still can remove elements)
   * <p>
   * Expected complexity: O(1)
   */
  @Override
  public Set<Entry<E>> entrySet() {
    return new AbstractSet<Entry<E>>() {
      @NotNull
      @Override
      public Iterator<Entry<E>> iterator() {
        return new ImmutableEntrySetIterator<>(buckets.entrySet().iterator());
      }

      @Override
      public int size() {
        // number of buckets, not totalVolume!
        return buckets.size();
      }
    };
  }

  /**
   * Element that occur multiple times in the multiset will appear
   * multiple times in this it (together, in sequence).
   * Expected complexity: O(1)
   *
   * @return order preserving it
   */
  @NotNull
  @Override
  public Iterator<E> iterator() {
    return new HashMultisetIterator<>(buckets.entrySet().iterator());
  }


  /**
   * Returns the number of elements in this collection.  If this collection
   * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
   * <tt>Integer.MAX_VALUE</tt>.
   *
   * @return the number of elements in this collection
   */
  @Override
  public int size() {
    return (int) min(totalVolume, Integer.MAX_VALUE);
  }

  /**
   * Returns <tt>true</tt> if this collection contains the specified element.
   */
  @Override
  @Contract(pure = true)
  public boolean contains(Object o) {
    return buckets.containsKey(o);
  }

  /**
   * Ensures that this collection contains the specified element (optional
   * operation).  Returns <tt>true</tt> if this collection changed as a
   * result of the call.  (Returns <tt>false</tt> if this collection does
   * not permit duplicates and already contains the specified element.)<p>
   *
   * @return <tt>true</tt> if this collection changed as a result of the
   * call
   */
  @Override
  public boolean add(E e) {
    buckets.put(e, count(e) + 1L);
    totalVolume += 1L;
    return true; // allows duplicates and null
  }

  /**
   * Removes a single instance of the specified element from this
   * collection, if it is present (optional operation). Returns
   * <tt>true</tt> if this collection contained the specified element (or
   * equivalently, if this collection changed as a result of the call).
   */
  @Override
  public boolean remove(Object o) {
    int volume = count(o);

    switch (volume) {
      case 0:
        return false;

      case 1:
        buckets.remove(o);
        break;

      default:
        // hope on the type erasure
        buckets.replace((E) o, volume - 1L);
    }

    totalVolume -= 1L;
    return true;
  }


  // concurrent modification?
  private final class HashMultisetIterator<T> implements Iterator<T> {
    private final Iterator<Map.Entry<T, Long>> it;
    private Map.Entry<T, Long> viewedBucket;
    private boolean nextWasCalled = false;
    private long bucketVolume = 0;

    private HashMultisetIterator(Iterator<Map.Entry<T, Long>> iterator) {
      this.it = iterator;
    }

    @Override
    public boolean hasNext() {
      return bucketVolume != 0 || it.hasNext();
    }

    @Override
    public T next() {
      if (bucketVolume == 0) {
        // take the next bucket
        bucketVolume = (viewedBucket = it.next()).getValue();
      }

      bucketVolume -= 1L;
      assert bucketVolume >= 0;

      nextWasCalled = true;
      return viewedBucket.getKey();
    }

    // allows modification, according to testIteratorSameRemoveWithoutNext
    @Override
    public void remove() {
      if (!nextWasCalled) {
        throw new IllegalStateException("Must call next() before remove()");
      }

      long count = viewedBucket.getValue();

      if (count > 1) {
        viewedBucket.setValue(count - 1);
      } else {
        it.remove();
      }

      totalVolume -= 1L;
      nextWasCalled = false;
    }
  }

  private final class entryWrapper<K> implements Entry<K> {
    Map.Entry<K, Long> bucket;

    public entryWrapper(Map.Entry<K, Long> bucket) {
      this.bucket = bucket;
    }

    @Override
    public K getElement() {
      return bucket.getKey();
    }

    @Override
    public int getCount() {
      return (int) min(bucket.getValue(), Integer.MAX_VALUE);
    }
  }


  // concurrent modification?
  private final class ImmutableEntrySetIterator<T> implements Iterator<Entry<T>> {
    private final Iterator<Map.Entry<T, Long>> it;
    private long bucketVolume = 0;

    private ImmutableEntrySetIterator(Iterator<Map.Entry<T, Long>> iterator) {
      this.it = iterator;
    }

    @Override
    public boolean hasNext() {
      return it.hasNext();
    }

    @Override
    public Entry<T> next() {
      Map.Entry<T, Long> bucket = it.next();
      bucketVolume = bucket.getValue();
      return new entryWrapper<>(bucket);
    }

    @Override
    public void remove() {
      it.remove();
      totalVolume -= bucketVolume;
    }
  }

}
