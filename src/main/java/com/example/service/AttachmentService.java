package com.example.service;

import com.example.dto.AttachmentResponseDto;
import com.example.model.Task;
import com.example.model.TaskAttachment;
import com.example.repository.TaskAttachmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskService taskService;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Сохраняет файл на диск и создает запись в БД.
     */
    @Transactional
    public AttachmentResponseDto storeAttachment(Long taskId, MultipartFile file) throws IOException {
        // 1. Проверяем, существует ли задача
        Task task = taskService.getById(taskId);

        // 2. Генерируем уникальное имя для хранения на диске
        String originalFileName = file.getOriginalFilename();
        String extension = getFileExtension(originalFileName);
        String storedFileName = UUID.randomUUID().toString() + extension;

        // 3. Сохраняем физический файл
        Path rootLocation = Paths.get(uploadDir);
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }

        Files.copy(file.getInputStream(), rootLocation.resolve(storedFileName), StandardCopyOption.REPLACE_EXISTING);

        // 4. Создаем сущность для БД
        TaskAttachment attachment = new TaskAttachment();
        attachment.setTask(task); // Предполагаем, что в сущности поле Task task
        attachment.setFileName(originalFileName);
        attachment.setStoredFileName(storedFileName);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());

        TaskAttachment saved = attachmentRepository.save(attachment);
        log.info("File saved: {} for Task ID {}", originalFileName, taskId);

        return mapToResponseDto(saved);
    }

    /**
     * Возвращает метаданные вложения из БД.
     */
    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Вложение с ID " + attachmentId + " не найдено"));
    }

    /**
     * Загружает файл с диска как Resource для скачивания.
     */
    public Resource loadAsResource(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);
        try {
            Path file = Paths.get(uploadDir).resolve(attachment.getStoredFileName());
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Файл не найден или недоступен для чтения: " + attachment.getFileName());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка при формировании пути к файлу", e);
        }
    }

    /**
     * Удаляет файл с диска и запись из БД.
     */
    @Transactional
    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment attachment = getAttachment(attachmentId);

        // Удаляем физический файл
        Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName());
        Files.deleteIfExists(filePath);

        // Удаляем запись из БД
        attachmentRepository.delete(attachment);
        log.info("Attachment with ID {} deleted from disk and DB", attachmentId);
    }

    /**
     * Получает список всех вложений для конкретной задачи.
     */
    public List<AttachmentResponseDto> getAttachmentsByTaskId(Long taskId) {
        return attachmentRepository.findByTaskId(taskId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private AttachmentResponseDto mapToResponseDto(TaskAttachment attachment) {
        return new AttachmentResponseDto(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getSize(),
                attachment.getUploadedAt()
        );
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}