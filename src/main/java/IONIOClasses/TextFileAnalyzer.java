package IONIOClasses;

import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class TextFileAnalyzer {

  public static class AnalysisResult {
    private final long lineCount;
    private final long wordCount;
    private final long charCount;

    public AnalysisResult(long lineCount, long wordCount, long charCount) {
      this.lineCount = lineCount;
      this.wordCount = wordCount;
      this.charCount = charCount;
    }

    public long getLineCount() {
      return lineCount;
    }

    public long getWordCount() {
      return wordCount;
    }

    public long getCharCount() {
      return charCount;
    }

    @Override
    public String toString() {
      return "AnalysisResult{" +
          "lineCount=" + lineCount +
          ", wordCount=" + wordCount +
          ", charCount=" + charCount +
          '}';
    }
  }

  public AnalysisResult analyzeFile(String filePath) throws IOException {
    long lineCount = 0;
    long wordCount = 0;
    long charCount = 0;

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lineCount++;
        charCount += line.length() + 1; // +1 для символа новой строки

        // Подсчет слов в строке
        String[] words = line.trim().split("\\s+");
        if (!line.trim().isEmpty()) {
          wordCount += words.length;
        }
      }
      // Корректировка charCount (последняя строка без символа новой строки)
      if (charCount > 0) {
        charCount--;
      }
    }

    return new AnalysisResult(lineCount, wordCount, charCount);
  }

  public void saveAnalysisResult(AnalysisResult result, String outputPath) throws IOException {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
      writer.write("=== Результаты анализа текстового файла ===\n");
      writer.write("Количество строк: " + result.getLineCount() + "\n");
      writer.write("Количество слов: " + result.getWordCount() + "\n");
      writer.write("Количество символов: " + result.getCharCount() + "\n");
      writer.write("===========================================\n");
    }
  }
}