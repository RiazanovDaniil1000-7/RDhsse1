package com.example.repository;

import com.example.model.Priority;
import com.example.model.Task;
import com.example.model.TaskAttachment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskAttachmentRepository taskAttachmentRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setTitle("Тестовая задача");
        testTask.setDescription("Описание тестовой задачи");
        testTask.setCompleted(false);
        testTask.setPriority(Priority.HIGH);
        testTask.setDueDate(LocalDate.now().plusDays(3));
        testTask.setTags(Set.of("test", "spring"));
    }

    @Test
    @DisplayName("Проверка кастомного запроса: findByCompletedAndPriority")
    void testFindByCompletedAndPriority() {
        taskRepository.save(testTask);
        Task anotherTask = new Task();
        anotherTask.setTitle("Другая задача");
        anotherTask.setCompleted(true); // Завершена
        anotherTask.setPriority(Priority.HIGH);
        taskRepository.save(anotherTask);
        List<Task> foundTasks = taskRepository.findByCompletedAndPriority(false, Priority.HIGH);
        assertThat(foundTasks).hasSize(1);
        assertThat(foundTasks.get(0).getTitle()).isEqualTo("Тестовая задача");
        assertThat(foundTasks.get(0).isCompleted()).isFalse();
    }

    @Test
    @DisplayName("Проверка сохранения задачи со связанным вложением (OneToMany / ManyToOne)")
    void testSaveTaskWithAttachment() {
        Task savedTask = taskRepository.save(testTask);
        TaskAttachment attachment = new TaskAttachment();
        attachment.setFileName("test_document.pdf");
        attachment.setStoredFileName("uuid-1234.pdf");
        attachment.setContentType("application/pdf");
        attachment.setSize(1024L);
        attachment.setTask(savedTask);
        taskAttachmentRepository.save(attachment);
        entityManager.flush();
        entityManager.clear();
        Task fetchedTask = taskRepository.findById(savedTask.getId()).orElseThrow();
        List<TaskAttachment> fetchedAttachments = taskAttachmentRepository.findByTaskId(fetchedTask.getId());
        assertThat(fetchedTask).isNotNull();
        assertThat(fetchedAttachments).hasSize(1);
        assertThat(fetchedAttachments.get(0).getFileName()).isEqualTo("test_document.pdf");
        assertThat(fetchedAttachments.get(0).getTask().getId()).isEqualTo(fetchedTask.getId());
    }

    @Test
    @DisplayName("Проверка кастомного запроса по дате (в течение 7 дней)")
    void testFindTasksDueInNextSevenDays() {
        taskRepository.save(testTask);
        LocalDate deadline = LocalDate.now().plusDays(7);
        List<Task> tasksDueSoon = taskRepository.findTasksDueBefore(deadline);
        assertThat(tasksDueSoon).hasSize(1);
        assertThat(tasksDueSoon.get(0).getTitle()).isEqualTo("Тестовая задача");
    }
}