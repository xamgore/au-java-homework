package au.xamgore;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

import static java.util.Collections.*;

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
  }

  @NotNull
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
   * "Garbage in -> garbage out" policy
   *
   * @param in an input stream to read from
   */
  @Override
  public void deserialize(InputStream in) throws IOException {
    if (in == null) {
      throw new NullPointerException();
    }

    DataInputStream stream = new DataInputStream(in);
    Deque<Vertex> stack = new LinkedList<>();
    Vertex cur = root;

    while (cur != null) {
      int numberOfCharsInCurrentVertex = stream.readInt();
      cur.storedWordsCounter = stream.readInt();
      cur.isTerminal = stream.readBoolean();

      for (int i = 0; i < numberOfCharsInCurrentVertex; i++) {
        char ch = stream.readChar();
        Vertex next = new Vertex();
        cur.dict.put(ch, next);
        stack.push(next);
      }

      cur = stack.poll();
    }
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
