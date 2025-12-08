package IONIOClasses;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.*;

public class FileProcessor {

  public List<Path> splitFile(String sourcePath, String outputDir, int partSize)
      throws IOException {
    List<Path> partPaths = new ArrayList<>();
    Path sourceFile = Paths.get(sourcePath);
    Path outputDirectory = Paths.get(outputDir);
    Files.createDirectories(outputDirectory);
    String fileName = sourceFile.getFileName().toString();
    long fileSize = Files.size(sourceFile);
    int partCount = (int) Math.ceil((double) fileSize / partSize);
    try (FileChannel sourceChannel = FileChannel.open(sourceFile, StandardOpenOption.READ)) {
      ByteBuffer buffer = ByteBuffer.allocate(partSize);
      for (int i = 0; i < partCount; i++) {
        String partName = String.format("%s.part%d", fileName, i + 1);
        Path partPath = outputDirectory.resolve(partName);
        try (FileChannel partChannel = FileChannel.open(partPath,
            StandardOpenOption.CREATE,
            StandardOpenOption.WRITE,
            StandardOpenOption.TRUNCATE_EXISTING)) {
          buffer.clear();
          int bytesRead = sourceChannel.read(buffer);
          if (bytesRead > 0) {
            buffer.flip();
            partChannel.write(buffer);
          }
          Files.writeString(
              outputDirectory.resolve(partName + ".meta"),
              String.format("partNumber=%d\noriginalFile=%s\ntotalParts=%d",
                  i + 1, fileName, partCount)
          );
        }
        partPaths.add(partPath);
      }
    }
    return partPaths;
  }

  public void mergeFiles(List<Path> partPaths, String outputPath) throws IOException {
    if (partPaths == null || partPaths.isEmpty()) {
      throw new IllegalArgumentException("Список частей пуст");
    }
    for (Path part : partPaths) {
      if (!Files.exists(part)) {
        throw new FileNotFoundException("Часть файла не найдена: " + part);
      }
    }
    Path outputFile = Paths.get(outputPath);
    try (FileChannel outputChannel = FileChannel.open(outputFile,
        StandardOpenOption.CREATE,
        StandardOpenOption.WRITE,
        StandardOpenOption.TRUNCATE_EXISTING)) {
      for (Path partPath : partPaths) {
        try (FileChannel partChannel = FileChannel.open(partPath, StandardOpenOption.READ)) {
          long size = partChannel.size();
          long position = 0;
          while (position < size) {
            long transferred = partChannel.transferTo(position, size - position, outputChannel);
            position += transferred;
          }
        }
      }
    }
  }
}
