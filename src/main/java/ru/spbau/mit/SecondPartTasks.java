package ru.spbau.mit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SecondPartTasks {

  private SecondPartTasks() {
  }

  // Найти строки из переданных файлов, в которых встречается указанная подстрока.
  public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
    return paths.stream()
      .flatMap(p -> {
        try {
          return Files.lines(Paths.get(p));
        } catch (IOException e) {
          return Stream.empty();
        }
      })
      .filter(s -> s.contains(sequence))
      .collect(Collectors.toList());
  }

  // В квадрат с длиной стороны 1 вписана мишень.
  // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
  // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать, какова вероятность попасть в мишень.
  public static double piDividedBy4() {
    Random rnd = new Random();
    double r = 0.5;

    return Stream
      .generate(() -> new Point(rnd.nextDouble() - r, rnd.nextDouble() - r))
      .limit(10000000)
      .mapToInt(p -> (p.x * p.x + p.y * p.y) <= r * r ? 1 : 0)
      .average()
      .orElse(0);
  }

  // Дано отображение из имени автора в список с содержанием его произведений.
  // Надо вычислить, чья общая длина произведений наибольшая.
  public static String findPrinter(Map<String, List<String>> compositions) {
    return compositions.entrySet().stream()
      .collect(Collectors.toMap(
        Entry::getKey,
        e -> e.getValue().stream().mapToInt(String::length).sum(),
        Integer::sum
      ))
      .entrySet().stream()
      .max(Comparator.comparingInt(Entry<String, Integer>::getValue))
      .map(Entry::getKey)
      .orElse(null);
  }

  // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
  // Необходимо вычислить, какой товар и в каком количестве надо поставить.
  public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
    return orders.stream()
      .flatMap(order -> order.entrySet().stream())
      .collect(Collectors.groupingBy(
        Entry::getKey,
        Collectors.mapping(
          Entry::getValue,
          Collectors.summingInt(val -> val)
        )
      ));
  }
}