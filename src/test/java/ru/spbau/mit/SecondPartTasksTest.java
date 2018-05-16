package ru.spbau.mit;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SecondPartTasksTest {

  @Rule
  public TemporaryFolder dir = new TemporaryFolder();

  @Test
  public void testFindQuotes() throws IOException {

    try (Writer w = new FileWriter(dir.newFile("aaa"))) {
      w.write("aaa bbb\naaa\nbbb ccc\n");
    }

    try (Writer w = new FileWriter(dir.newFile("bbb"))) {
      w.write("bbb kek\n\n");
    }

    List<String> paths = Arrays.stream(dir.getRoot().listFiles())
      .map(File::getAbsolutePath).sorted().collect(Collectors.toList());

    assertEquals(
      Arrays.asList("aaa bbb", "aaa"),
      SecondPartTasks.findQuotes(paths, "aaa"));

    assertEquals(
      Arrays.asList("aaa bbb", "bbb ccc", "bbb kek"),
      SecondPartTasks.findQuotes(paths, "bbb"));
  }

  @Test
  public void testPiDividedBy4() {
    double pi = SecondPartTasks.piDividedBy4() * 4;
    assertEquals(Math.PI, pi, 0.001);
  }

  @Test
  public void testFindPrinter() {
    Map<String, List<String>> authorsWithBooks = ImmutableMap.of(
      "aa", Arrays.asList("a", "b", "c", "d", "ef", "", "ghi"),
      "bb", Arrays.asList("a", "b", "c"),
      "cc", Arrays.asList("abcd", "efgh")
    );

    assertEquals("aa", SecondPartTasks.findPrinter(authorsWithBooks));
  }

  @Test
  public void testCalculateGlobalOrder() {
    String a = "aa";
    String b = "bb";
    String c = "cc";
    String d = "dd";
    String e = "ee";
    String f = "ff";
    String g = "gg";

    Map<String, Integer> map1 = ImmutableMap.of(a, 1, b, 2, c, 3);
    Map<String, Integer> map2 = ImmutableMap.of(a, 1, b, 3, d, 4);
    Map<String, Integer> map3 = ImmutableMap.of(e, 7, f, 8, g, 9);

    ImmutableMap<String, Integer> expected = ImmutableMap.<String, Integer>builder()
      .put(a, 2).put(b, 5).put(c, 3).put(d, 4).put(e, 7).put(f, 8).put(g, 9).build();

    assertEquals(expected, SecondPartTasks.calculateGlobalOrder(Arrays.asList(map1, map2, map3)));
  }
}