package com.example.service;

import static java.util.stream.Collectors.*;

import com.example.dto.AttachmentResponseDto;
import com.example.model.Task;
import com.example.model.TaskAttachment;
import com.example.repository.TaskAttachmentRepository;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskService taskService;
    private static final String NOTFOUND = "Attachment not found: ";
    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public AttachmentResponseDto storeAttachment(Long taskId, MultipartFile file) throws IOException {
        // Проверяем существование задачи
        Task task = taskService.getById(taskId);
        if (task == null) {
            log.error("Task not found with id: {}", taskId);
            throw new IllegalArgumentException("Task not found with id: " + taskId);
        }

        log.info("Found task: {} with ID: {}", task.getTitle(), task.getId());

        // Создаем директорию для загрузок
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Генерируем уникальное имя для файла
        String originalFileName = file.getOriginalFilename();
        String storedFileName = UUID.randomUUID().toString() +
                getFileExtension(originalFileName);

        // Сохраняем файл
        Path filePath = uploadPath.resolve(storedFileName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // Создаем запись в репозитории
        TaskAttachment attachment = new TaskAttachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(originalFileName);
        attachment.setStoredFileName(storedFileName);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());

        TaskAttachment savedAttachment = attachmentRepository.save(attachment);

        log.info("File saved: {} for task {}", storedFileName, taskId);

        return mapToResponseDto(savedAttachment);
    }

    public Resource loadAsResource(Long attachmentId) throws IOException {
        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException(NOTFOUND + attachmentId));

        Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName());

        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("File not found on disk: " + attachment.getStoredFileName());
        }

        return new InputStreamResource(Files.newInputStream(filePath));
    }

    public void deleteAttachment(Long attachmentId) throws IOException {
        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException(NOTFOUND + attachmentId));

        Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName());
        Files.deleteIfExists(filePath);
        attachmentRepository.deleteById(attachmentId);

        log.info("File deleted: {} for attachment {}", attachment.getStoredFileName(), attachmentId);
    }

    public List<AttachmentResponseDto> getAttachmentsByTaskId(Long taskId) {
        List<AttachmentResponseDto> collect;
        List<AttachmentResponseDto> list = new ArrayList<>();
        for (TaskAttachment taskAttachment : attachmentRepository.findByTaskId(taskId)) {
            AttachmentResponseDto attachmentResponseDto = mapToResponseDto(taskAttachment);
            list.add(attachmentResponseDto);
        }
        collect = list;
        return collect;
    }

    public TaskAttachment getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new IllegalArgumentException(NOTFOUND + attachmentId));
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