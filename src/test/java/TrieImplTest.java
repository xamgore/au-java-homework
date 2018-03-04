import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class TrieImplTest {

  @Test
  public void testTrieWithSingleString() {
    final String PRE = "kkk";
    final String IN = PRE + "kkk";

    Trie t = new TrieImpl();
    assertEquals(0, t.size());
    assertEquals(0, t.howManyStartsWithPrefix(PRE));
    assertFalse(t.contains(IN));
    assertFalse(t.remove(IN));

    assertFalse(t.add(IN));
    assertTrue(t.contains(IN));
    assertEquals(1, t.size());
    assertEquals(1, t.howManyStartsWithPrefix(PRE));

    assertTrue(t.remove(IN));
    assertFalse(t.contains(IN));
    assertEquals(0, t.size());
    assertEquals(0, t.howManyStartsWithPrefix(PRE));

    assertFalse(t.remove(IN));
  }


  @Test
  public void testTrieWithMultipleStrings() {
    final String PRE = "pre";
    final String IN1 = PRE + "fix";
    final String IN2 = PRE + "commit";
    final String IN3 = PRE + "commitka";

    Trie t = new TrieImpl();
    List<String> ls = Arrays.asList(IN1, IN2, IN3);

    assertTrue(ls.stream().noneMatch(t::add));
    assertTrue(ls.stream().allMatch(t::contains));
    assertEquals(3, t.size());
    assertEquals(3, t.howManyStartsWithPrefix(PRE));
    assertEquals(2, t.howManyStartsWithPrefix(PRE + "commit"));
  }

  @Test
  public void testPrefixIsAddedAfterInsertingString() {
    Trie t = new TrieImpl();

    t.add("test");
    assertEquals(1, t.size());
    assertTrue(t.contains("test"));

    assertEquals(1, t.howManyStartsWithPrefix("t"));
    assertEquals(1, t.howManyStartsWithPrefix("te"));
    assertEquals(1, t.howManyStartsWithPrefix("tes"));
    assertEquals(1, t.howManyStartsWithPrefix("test"));
  }

  @Test
  public void testPrefixIsRemovedAfterDeletingString() {
    Trie t = new TrieImpl();

    t.add("first");
    t.add("first-and");
    t.add("first-snd");

    assertEquals(3, t.size());
    assertTrue(t.remove("first"));
    assertEquals(2, t.howManyStartsWithPrefix("f"));
    assertEquals(2, t.howManyStartsWithPrefix("first"));
    assertEquals(0, t.howManyStartsWithPrefix("first!"));
    assertEquals(1, t.howManyStartsWithPrefix("first-and"));
    assertEquals(1, t.howManyStartsWithPrefix("first-snd"));

    t.remove("first-and");
    t.remove("first-snd");
    assertTrue(t.size() == 0);
    assertEquals(0, t.howManyStartsWithPrefix("f"));
    assertEquals(0, t.howManyStartsWithPrefix("first"));
    assertEquals(0, t.howManyStartsWithPrefix("first!"));
  }

  @Test
  public void testDoubleAdditionDontBreakSize() {
    Trie t = new TrieImpl();

    t.add("kek");
    assertEquals(1, t.size());
    assertEquals(1, t.howManyStartsWithPrefix("kek"));
    assertEquals(1, t.howManyStartsWithPrefix("k"));

    assertTrue(t.add("kek"));
    assertEquals(1, t.size());
    assertEquals(1, t.howManyStartsWithPrefix("kek"));
    assertEquals(1, t.howManyStartsWithPrefix("k"));
  }

  @Test
  public void testContainsOnPartOfWords() {
    Trie t = new TrieImpl();

    t.add("test");
    assertFalse(t.contains("t"));
    assertFalse(t.contains("te"));
    assertFalse(t.contains("tes"));
    assertTrue(t.contains("test"));
    assertFalse(t.contains("test!"));
  }

  @Test
  public void testDoubleDeletionDoesntCorruptContains() {
    Trie t = new TrieImpl();

    assertFalse(t.add("test"));
    assertTrue(t.add("test"));
    assertFalse(t.add("testik"));

    assertTrue(t.contains("test"));
    assertTrue(t.contains("testik"));

    assertTrue(t.remove("test"));

    assertFalse(t.contains("test"));
    assertTrue(t.contains("testik"));
    assertEquals(1, t.size());
    assertEquals(1, t.howManyStartsWithPrefix("tes"));
  }

}
