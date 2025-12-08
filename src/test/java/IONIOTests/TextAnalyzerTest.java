package IONIOTests;

import IONIOClasses.TextFileAnalyzer;
import IONIOClasses.TextFileAnalyzer.AnalysisResult;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TextFileAnalyzerTest {

  @Test
  void testAnalyzeFile() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();
    Path testFile = Files.createTempFile("test", ".txt");
    Files.write(testFile, Arrays.asList("Hello world!", "This is test."));
    AnalysisResult result = analyzer.analyzeFile(testFile.toString());
    assertEquals(2, result.getLineCount());
    assertEquals(5, result.getWordCount());
    assertEquals(26, result.getCharCount());
    Files.delete(testFile);
  }

  @Test
  void testSaveAnalysisResult() throws IOException {
    TextFileAnalyzer analyzer = new TextFileAnalyzer();
    AnalysisResult result = new AnalysisResult(2, 5, 20);
    Path outputFile = Files.createTempFile("analysis", ".txt");
    analyzer.saveAnalysisResult(result, outputFile.toString());
    assertTrue(Files.size(outputFile) > 0);
    String content = Files.readString(outputFile);
    assertTrue(content.contains("Количество строк: 2"));
    assertTrue(content.contains("Количество слов: 5"));
    assertTrue(content.contains("Количество символов: 20"));
    Files.delete(outputFile);
  }
}