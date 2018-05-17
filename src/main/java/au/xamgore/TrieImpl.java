package au.xamgore;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class TrieImpl implements Trie, StreamSerializable {

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

    private boolean isValidStoredWordsCounter() {
      int counter = isTerminal ? 1 : 0;

      for (Vertex child : dict.values()) {
        counter += child.storedWordsCounter;
        if (!child.isValidStoredWordsCounter()) {
          return false;
        }
      }

      return counter == storedWordsCounter;
    }
  }

  @NotNull
  private Vertex root;

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


  @Override
  public void serialize(OutputStream out) throws IOException {
    DataOutputStream stream = new DataOutputStream(out);
    Deque<Vertex> stack = new LinkedList<>();
    Vertex v = root;

    while (v != null) {
      stream.writeInt(v.dict.size());
      stream.writeInt(v.storedWordsCounter);
      stream.writeBoolean(v.isTerminal);

      for (Entry<Character, Vertex> entry : v.dict.entrySet()) {
        stream.writeChar(entry.getKey());
        stack.push(entry.getValue());
      }

      v = stack.poll();
    }
  }

  /**
   * Replace current state with data from input stream
   *
   * @param in an input stream to read from
   * @throws IllegalArgumentException if stream is malformed
   * @throws IOException on io problems
   */
  @Override
  public void deserialize(InputStream in) throws IOException {
    DataInputStream stream = new DataInputStream(requireNonNull(in));
    Deque<Vertex> stack = new LinkedList<>();
    Vertex newRoot = new Vertex();
    Vertex cur = newRoot;

    while (cur != null) {
      int numberOfCharsInCurrentVertex = assureIsPositive(stream.readInt());
      cur.storedWordsCounter = assureIsPositive(stream.readInt());
      cur.isTerminal = stream.readBoolean();

      for (int i = 0; i < numberOfCharsInCurrentVertex; i++) {
        char ch = stream.readChar();
        Vertex next = new Vertex();
        cur.dict.put(ch, next);
        stack.push(next);
      }

      cur = stack.poll();
    }

    if (newRoot.isValidStoredWordsCounter()) {
      root = newRoot;
    }
  }

  private int assureIsPositive(int x) {
    if (x < 0) {
      throw new IllegalArgumentException();
    }
    return x;
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
