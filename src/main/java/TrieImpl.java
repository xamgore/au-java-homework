import java.util.HashMap;
import java.util.Map;

public class TrieImpl implements Trie {

  protected class Vertex {
    boolean isTerminal = false;
    int storedWordsCounter = 0;
    Map<Character, Vertex> next = new HashMap<>();

    Vertex getNext(Character ch) {
      return next.get(ch);
    }

    Vertex getNextOrCreate(Character ch) {
      Vertex res = next.get(ch);

      if (res == null) {
        res = new Vertex();
        next.put(ch, res);
      }

      return res;
    }

    void cut(Character ch) {
      next.remove(ch);
    }
  }

  private final Vertex root = new Vertex();


  /**
   * Complexity: O(|str|)
   *
   * @return <tt>true</tt> if this set did not already contain the specified
   * element
   */
  public boolean add(String str) {
    if (contains(str))
      return true;

    root.storedWordsCounter++;
    Vertex cur = root;

    for (int idx = 0; idx < str.length(); idx++) {
      Character ch = str.charAt(idx);
      cur = cur.getNextOrCreate(ch);
      cur.storedWordsCounter++;
    }

    boolean exists = cur.isTerminal;
    cur.isTerminal = true;
    return exists;
  }

  /**
   * Complexity: O(|str|)
   */
  public boolean contains(String str) {
    Vertex last = goThrough(str);
    return last != null && last.isTerminal;
  }

  /**
   * Expected complexity: O(|element|)
   *
   * @return <tt>true</tt> if this set contained the specified element
   */
  public boolean remove(String str) {
    if (!contains(str))
      return false;

    root.storedWordsCounter--;
    Vertex cur = root;

    for (int idx = 0; idx < str.length(); idx++) {
      Character ch = str.charAt(idx);
      Vertex next = cur.getNext(ch);

      if (--next.storedWordsCounter == 0) {
        cur.cut(ch);
        break;
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
  public int howManyStartsWithPrefix(String prefix) {
    Vertex last = goThrough(prefix);
    return (last == null) ? 0 : last.storedWordsCounter;
  }


  private Vertex goThrough(String str) {
    Vertex cur = root;

    for (int idx = 0; idx < str.length() && cur != null; idx++)
      cur = cur.getNext(str.charAt(idx));

    return cur;
  }
}
