import au.xamgore.Trie;
import au.xamgore.TrieImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Random;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class TrieImplTest {

  private Trie t;

  @BeforeEach
  void setUp() {
    t = new TrieImpl();
  }

  @Test
  void testTrieAddResultSatisfiesTheInterfaceDescription() {
    final String fstStr = "string";
    final String sndStr = "another string";
    final String subStr = fstStr.substring(0, 3);

    assertTrue(t.add(fstStr),
      "Insertion to an empty trie");

    assertTrue(t.add(sndStr),
      "Insertion of an element not added to trie previously");

    assertTrue(t.add(subStr),
      "Insertion of substring of previously added string");

    assertFalse(t.add(fstStr),
      "Insertion of previously added string");

    assertFalse(t.add(sndStr),
      "Insertion of previously added string");

    assertFalse(t.add(subStr),
      "Insertion of previously added string");
  }

  @Test
  void testTrieThrowsExceptionOnNullOrEmptyStringArgument() {
    assertThrows(IllegalArgumentException.class, () -> t.add(null),
      "Exception for null arguments must be thrown");

    assertThrows(IllegalArgumentException.class, () -> t.add(""),
      "Exception for empty arguments must be thrown");
  }

  @Test
  void testSizeOfEmptyTrieIsZero() {
    assertEquals(0, t.size(), "Size of empty trie");
  }

  @TestFactory
  Stream<DynamicTest> testEmptyTrieDoesNotContainsAnyElement() {
    final Random rand = new Random();

    Stream<DynamicTest> randomData = Stream
      .generate(() -> UUID.randomUUID().toString().replaceAll("-", ""))
      .map(str -> str.substring(0, rand.nextInt(20)))
      .map(str -> dynamicTest("\"" + str + "\"",
        () -> assertFalse(t.contains(str))))
      .limit(50);

    Stream<DynamicTest> specialCases = Stream.of(
      dynamicTest("null",
        () -> assertFalse(t.contains(null))),
      dynamicTest("empty string",
        () -> assertFalse(t.contains(null)))
    );

    return Stream.concat(specialCases, randomData);
  }

  @Test
  void testRemoveDoesNotRemoveAnotherString() {
    assertTrue(t.add("abc"));
    assertTrue(t.contains("abc"));
    assertFalse(t.contains("abcd"));

    assertTrue(t.add("abcd"));
    assertTrue(t.contains("abc"));
    assertTrue(t.contains("abcd"));

    // may change terminality of "abc"
    assertTrue(t.remove("abcd"));
    assertTrue(t.contains("abc"));
    assertFalse(t.contains("abcd"));

    assertTrue(t.remove("abc"));
    assertFalse(t.contains("abc"));
    assertFalse(t.contains("abcd"));
  }

  @Test
  void testTrieWithSingleString() {
    final String PRE = "kkk";
    final String IN = PRE + "kkk";

    Trie t = new TrieImpl();
    assertEquals(0, t.size());
    assertEquals(0, t.howManyStartsWithPrefix(PRE));
    assertFalse(t.contains(IN));
    assertFalse(t.remove(IN));

    assertTrue(t.add(IN));
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
  void testTrieWithMultipleStrings() {
    final String PRE = "pre";
    final String IN1 = PRE + "fix";
    final String IN2 = PRE + "commit";
    final String IN3 = PRE + "commitka";

    Trie t = new TrieImpl();
    List<String> ls = Arrays.asList(IN1, IN2, IN3);

    assertTrue(ls.stream().allMatch(t::add));
    assertTrue(ls.stream().allMatch(t::contains));
    assertEquals(3, t.size());
    assertEquals(3, t.howManyStartsWithPrefix(PRE));
    assertEquals(2, t.howManyStartsWithPrefix(PRE + "commit"));
  }

  @Test
  void testPrefixIsAddedAfterInsertingString() {
    Trie t = new TrieImpl();

    assertTrue(t.add("test"));
    assertEquals(1, t.size());
    assertTrue(t.contains("test"));

    assertEquals(1, t.howManyStartsWithPrefix("t"));
    assertEquals(1, t.howManyStartsWithPrefix("te"));
    assertEquals(1, t.howManyStartsWithPrefix("tes"));
    assertEquals(1, t.howManyStartsWithPrefix("test"));
  }

  @Test
  void testPrefixIsRemovedAfterDeletingString() {
    Trie t = new TrieImpl();

    assertTrue(t.add("first"));
    assertTrue(t.add("first-and"));
    assertTrue(t.add("first-snd"));

    assertEquals(3, t.size());
    assertTrue(t.remove("first"));
    assertEquals(2, t.howManyStartsWithPrefix("f"));
    assertEquals(2, t.howManyStartsWithPrefix("first"));
    assertEquals(0, t.howManyStartsWithPrefix("first!"));
    assertEquals(1, t.howManyStartsWithPrefix("first-and"));
    assertEquals(1, t.howManyStartsWithPrefix("first-snd"));

    assertTrue(t.remove("first-and"));
    assertTrue(t.remove("first-snd"));
    assertTrue(t.size() == 0);
    assertEquals(0, t.howManyStartsWithPrefix("f"));
    assertEquals(0, t.howManyStartsWithPrefix("first"));
    assertEquals(0, t.howManyStartsWithPrefix("first!"));
  }

  @Test
  void testDoubleAdditionDontBreakSize() {
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
  void testContainsOnPartOfWords() {
    Trie t = new TrieImpl();

    assertTrue(t.add("test"));
    assertFalse(t.contains("t"));
    assertFalse(t.contains("te"));
    assertFalse(t.contains("tes"));
    assertTrue(t.contains("test"));
    assertFalse(t.contains("test!"));
  }

  @Test
  void testDoubleDeletionDoesntCorruptContains() {
    Trie t = new TrieImpl();

    assertTrue(t.add("test"));
    assertFalse(t.add("test"));
    assertTrue(t.add("testik"));

    assertTrue(t.contains("test"));
    assertTrue(t.contains("testik"));

    assertTrue(t.remove("test"));

    assertFalse(t.contains("test"));
    assertTrue(t.contains("testik"));
    assertEquals(1, t.size());
    assertEquals(1, t.howManyStartsWithPrefix("tes"));
  }

}
