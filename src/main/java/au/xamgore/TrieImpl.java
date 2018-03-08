package au.xamgore;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class TrieImpl implements Trie {

  class Vertex {
    boolean isTerminal = false;
    int storedWordsCounter = 0;
    final Map<Character, Vertex> dict = mapSupplier.get();

    Vertex getNext(Character ch) {
      return dict.get(ch);
    }

    Vertex getNextOrCreate(Character ch) {
      Vertex res = dict.get(ch);

      if (res == null) {
        res = new Vertex();
        dict.put(ch, res);
      }

      return res;
    }

    void cut(Character ch) {
      dict.remove(ch);
    }
  }

  private final Vertex root;

  private final Supplier<HashMap<Character, Vertex>> mapSupplier;


  public TrieImpl() {
    this.mapSupplier = HashMap::new;
    root = new Vertex();
  }

  public TrieImpl(Supplier<HashMap<Character, Vertex>> mapSupplier) {
    if (mapSupplier == null) {
      throw new IllegalArgumentException("mapSupplier must be a non-null value");
    }

    this.mapSupplier = mapSupplier;
    root = new Vertex();
  }

  /**
   * Complexity: O(|str|)
   *
   * @return <tt>false</tt> if this set has already
   * contained the specified element
   */
  public boolean add(@NotNull String str) {
    assertIsValid(str);

    if (contains(str)) {
      return false;
    }

    root.storedWordsCounter++;
    Vertex cur = root;

    for (char ch : str.toCharArray()) {
      cur = cur.getNextOrCreate(ch);
      cur.storedWordsCounter++;
    }

    assert !cur.isTerminal;
    return cur.isTerminal = true;
  }

  /**
   * Complexity: O(|str|)
   */
  public boolean contains(@Nullable String str) {
    if (isNotValid(str)) return false;
    Vertex last = goThrough(str);
    return last != null && last.isTerminal;
  }

  /**
   * Expected complexity: O(|element|)
   *
   * @return <tt>true</tt> if this set contained the specified element
   */
  public boolean remove(@Nullable String str) {
    if (isNotValid(str) || !contains(str)) {
      return false;
    }

    root.storedWordsCounter--;
    Vertex cur = root;

    for (char ch : str.toCharArray()) {
      Vertex next = cur.getNext(ch);
      int counter = next.storedWordsCounter -= 1;

      if (counter == 0) {
        cur.cut(ch);
        return true;
      }

      cur = next;
    }

    cur.isTerminal = false;
    return true;
  }

  /**
   * Expected complexity: O(1)
   */
  public int size() {
    return root.storedWordsCounter;
  }

  /**
   * Expected complexity: O(|prefix|)
   */
  public int howManyStartsWithPrefix(@Nullable String prefix) {
    if (isNotValid(prefix)) return 0;
    Vertex last = goThrough(prefix);
    return last == null ? 0 : last.storedWordsCounter;
  }


  private Vertex goThrough(@NotNull String str) {
    assert str != null;

    Vertex cur = root;
    int idx = 0;

    while (cur != null && idx < str.length()) {
      cur = cur.getNext(str.charAt(idx++));
    }

    return cur;
  }

  private void assertIsValid(@Nullable String arg) {
    if (arg == null) {
      throw new IllegalArgumentException("Null strings are not supported");
    }

    if (arg.isEmpty()) {
      throw new IllegalArgumentException("String argument must be non empty");
    }
  }

  private boolean isNotValid(@Nullable String arg) {
    return arg == null || arg.isEmpty();
  }
}
