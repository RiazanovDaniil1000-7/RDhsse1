package com.example.service;

import com.example.dto.AttachmentResponseDto;
import com.example.model.Priority;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AttachmentServiceIntegrationTest {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private TaskRepository taskRepository;

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Test
    @DisplayName("Должен сохранять файл на диск и запись в БД, а затем успешно удалять")
    void shouldStoreAndDeleteAttachment() throws IOException {
        Task task = new Task();
        task.setTitle("Task for Attachment");
        task.setCompleted(false);
        task.setPriority(Priority.MEDIUM);
        task = taskRepository.save(task);
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );
        AttachmentResponseDto response = attachmentService.storeAttachment(task.getId(), mockFile);
        assertThat(response.getFileName()).isEqualTo("test.txt");
        assertThat(response.getId()).isNotNull();
        var attachment = attachmentService.getAttachment(response.getId());
        Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName());
        assertThat(Files.exists(filePath)).isTrue();
        attachmentService.deleteAttachment(response.getId());
        assertThat(Files.exists(filePath)).isFalse();
    }
}