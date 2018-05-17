import au.xamgore.TrieImpl;
import com.sun.media.sound.InvalidFormatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class TrieSerializeTest {
  private TrieImpl t;

  private static TrieImpl get() {
    return new TrieImpl();
  }

  @BeforeEach
  void setUp() {
    t = get();
  }

  TrieImpl goback(TrieImpl t) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    t.serialize(out);
    t = get();
    t.deserialize(new ByteArrayInputStream(out.toByteArray()));
    return t;
  }

  @Test
  void testWithInvalidCountOfNodes() throws IOException {
    t.add("k");
    t.add("o");
    t.add("p");

    assertEquals(3, t.size());
    assertTrue(t.contains("k"));
    assertTrue(t.contains("o"));
    assertTrue(t.contains("p"));

    // spoil bytes
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    DataOutputStream dout = new DataOutputStream(out);
    dout.writeInt(-5); // some negative count
    assertThrows(IllegalArgumentException.class,
      () -> t.deserialize(new ByteArrayInputStream(out.toByteArray())));

    // the old trie was not changed
    assertEquals(t.size(), t.size(), "Size of deserialized doesn't equal to original");
    assertTrue(t.contains("k"));
    assertTrue(t.contains("o"));
    assertTrue(t.contains("p"));
  }

  @Test
  void testEmpty() throws IOException {
    TrieImpl p = goback(t);
    assertEquals(t.size(), p.size(), "Size of deserialized empty trie is not 0");
  }

  @Test
  void testOneLayer() throws IOException {
    t.add("k");
    t.add("o");
    t.add("p");

    assertEquals(3, t.size());
    assertTrue(t.contains("k"));
    assertTrue(t.contains("o"));
    assertTrue(t.contains("p"));

    TrieImpl p = goback(t);
    assertEquals(t.size(), p.size(), "Size of deserialized doesn't equal to original");
    assertTrue(p.contains("k"));
    assertTrue(p.contains("o"));
    assertTrue(p.contains("p"));
  }

  @Test
  void testInnerLayers() throws IOException {
    final String s1 = "some simple test";
    final String s2 = "some simple test must be passed";
    final String s3 = "some simple test must be passed!!!";

    t.add(s1);
    t.add(s1);
    t.add(s2);
    t.add(s3);

    TrieImpl p = goback(t);
    assertEquals(t.size(), p.size(), "Size of deserialized doesn't equal to original");
    assertTrue(p.contains(s1));
    assertTrue(p.contains(s2));
    assertTrue(p.contains(s3));

    assertEquals(t.howManyStartsWithPrefix(s1), p.howManyStartsWithPrefix(s1));
    assertEquals(t.howManyStartsWithPrefix(s2), p.howManyStartsWithPrefix(s2));
    assertEquals(t.howManyStartsWithPrefix(s3), p.howManyStartsWithPrefix(s3));
  }

  @Test
  void testPrefixStrings() throws IOException {
    assertTrue(t.add("abc"));
    assertTrue(t.add("abcd"));
    assertTrue(t.add("e"));

    TrieImpl newStringSet = goback(t);
    assertTrue(newStringSet.contains("abc"));
    assertTrue(newStringSet.contains("abcd"));
    assertTrue(newStringSet.contains("e"));

    assertEquals(2, newStringSet.howManyStartsWithPrefix("a"));
    assertEquals(2, newStringSet.howManyStartsWithPrefix("abc"));
    assertEquals(1, newStringSet.howManyStartsWithPrefix("abcd"));
    assertEquals(0, newStringSet.howManyStartsWithPrefix("abcde"));
    assertEquals(1, newStringSet.howManyStartsWithPrefix("e"));
  }

  @Test
  void testFromEmptyStream() throws IOException {
    byte[] buffer = new byte[0];
    ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
    TrieImpl p = get();
    assertThrows(IOException.class, () -> p.deserialize(inputStream));
  }

  @Test
  void testMailformedStream() throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    t.add("kekekekke");
    t.add("some non important strings");
    t.serialize(out);

    byte[] byteArray = out.toByteArray();
    byte[] buf = new byte[13];

    System.arraycopy(byteArray, 0, buf, 0, 10);
    TrieImpl p = get();
    assertThrows(IOException.class, () -> p.deserialize(new ByteArrayInputStream(buf)));
  }
}