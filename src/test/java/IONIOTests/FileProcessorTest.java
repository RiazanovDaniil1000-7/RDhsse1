package IONIOTests;

import IONIOClasses.FileProcessor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.Random;

class FileProcessorTest {

  @Test
  void testSplitAndMergeFile() throws IOException {
    FileProcessor processor = new FileProcessor();
    Path testFile = Files.createTempFile("test", ".dat");
    byte[] testData = new byte[1500]; // 1.5KB данных
    new Random().nextBytes(testData);
    Files.write(testFile, testData);
    String outputDir = Files.createTempDirectory("parts").toString();
    List<Path> parts = processor.splitFile(testFile.toString(), outputDir, 500);
    assertEquals(3, parts.size(), "Должно быть 3 части для 1.5KB файла с размером части 500 байт");
    for (int i = 0; i < parts.size(); i++) {
      Path part = parts.get(i);
      assertTrue(Files.exists(part), "Часть файла " + (i + 1) + " должна существовать: " + part);
      assertTrue(Files.isRegularFile(part),
          "Часть " + (i + 1) + " должна быть обычным файлом: " + part);
    }
    for (int i = 0; i < parts.size(); i++) {
      assertEquals(500, Files.size(parts.get(i)),
          "Часть " + (i + 1) + " должна быть 500 байт");
    }
    for (int i = 0; i < parts.size(); i++) {
      String partName = parts.get(i).getFileName().toString();
      assertTrue(partName.contains(".part" + (i + 1)),
          "Имя части " + (i + 1) + " должно содержать '.part" + (i + 1) + "': " + partName);
    }
    long totalPartsSize = parts.stream()
        .mapToLong(path -> {
          try {
            return Files.size(path);
          } catch (IOException e) {
            return 0;
          }
        })
        .sum();
    long originalSize = Files.size(testFile);
    assertEquals(originalSize, totalPartsSize,
        "Суммарный размер всех частей должен равняться размеру исходного файла");
    assertTrue(Files.exists(Paths.get(outputDir)),
        "Директория для частей должна существовать: " + outputDir);
    assertTrue(Files.isDirectory(Paths.get(outputDir)),
        outputDir + " должна быть директорией");
    byte[] firstPartBytes = Files.readAllBytes(parts.get(0));
    byte[] first500Original = Arrays.copyOfRange(testData, 0, 500);
    assertArrayEquals(first500Original, firstPartBytes,
        "Первая часть должна содержать первые 500 байт исходного файла");
    byte[] secondPartBytes = Files.readAllBytes(parts.get(1));
    byte[] second500Original = Arrays.copyOfRange(testData, 500, 1000);
    assertArrayEquals(second500Original, secondPartBytes,
        "Вторая часть должна содержать следующие 500 байт исходного файла");
    byte[] thirdPartBytes = Files.readAllBytes(parts.get(2));
    byte[] third500Original = Arrays.copyOfRange(testData, 1000, 1500);
    assertArrayEquals(third500Original, thirdPartBytes,
        "Третья часть должна содержать последние 500 байт исходного файла");
    Path mergedFile = Files.createTempFile("merged", ".dat");
    processor.mergeFiles(parts, mergedFile.toString());
    assertTrue(Files.exists(mergedFile), "Объединенный файл должен существовать: " + mergedFile);
    assertTrue(Files.isRegularFile(mergedFile),
        "Объединенный файл должен быть обычным файлом: " + mergedFile);
    long mergedSize = Files.size(mergedFile);
    assertEquals(originalSize, mergedSize,
        "Размер объединенного файла должен равняться размеру исходного файла");
    assertArrayEquals(Files.readAllBytes(testFile), Files.readAllBytes(mergedFile),
        "Исходный и объединенный файлы должны быть идентичными");
    byte[] mergedBytes = Files.readAllBytes(mergedFile);
    assertArrayEquals(testData, mergedBytes,
        "Содержимое объединенного файла должно совпадать с исходными тестовыми данными");
    Files.delete(testFile);
    Files.delete(mergedFile);
    for (Path part : parts) {
      Files.delete(part);
    }
    Files.delete(Paths.get(outputDir));

    System.out.println("Тест testSplitAndMergeFile пройден успешно!");
    System.out.println("✓ Создано частей: " + parts.size());
    System.out.println("✓ Размер исходного файла: " + originalSize + " байт");
    System.out.println("✓ Размер объединенного файла: " + mergedSize + " байт");
    System.out.println("✓ Все проверки пройдены");
  }
}