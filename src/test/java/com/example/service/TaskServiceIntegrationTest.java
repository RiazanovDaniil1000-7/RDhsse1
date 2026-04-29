package com.example.service;

import com.example.exception.TaskNotFoundException;
import com.example.model.Priority;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void cleanUp() {
        taskRepository.deleteAll();
    }

    @Test
    @DisplayName("bulkCompleteTasks должен откатить всю транзакцию, если один ID неверный")
    void shouldRollbackTransactionWhenOneIdIsInvalid() {
        // 1. Создаем две задачи в БД
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setCompleted(false);
        task1.setPriority(Priority.LOW);
        task1 = taskRepository.save(task1);

        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setCompleted(false);
        task2.setPriority(Priority.LOW);
        task2 = taskRepository.save(task2);

        Long validId1 = task1.getId();
        Long validId2 = task2.getId();
        Long invalidId = 999L;
        List<Long> idsToComplete = List.of(validId1, validId2, invalidId);
        assertThatThrownBy(() -> taskService.bulkCompleteTasks(idsToComplete))
                .isInstanceOf(TaskNotFoundException.class);
        Task fetched1 = taskRepository.findById(validId1).orElseThrow();
        Task fetched2 = taskRepository.findById(validId2).orElseThrow();

        assertThat(fetched1.isCompleted()).isFalse();
        assertThat(fetched2.isCompleted()).isFalse();
    }
}