package com.example.service;

import com.example.dto.TaskResponseDto;
import com.example.exception.TaskNotFoundException;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // По умолчанию все методы только для чтения
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    public Task getById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        // JpaRepository возвращает Optional. Используем orElseThrow,
        // чтобы сразу выбросить исключение, если задачи нет.
        return taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    @Transactional // Этот метод изменяет данные, поэтому нужна транзакция
    public Task addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        log.info("Saving new task: {}", task.getTitle());
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, Task taskDetails) {
        Task existingTask = getById(id);
        taskDetails.setId(id);

        log.info("Updating task with ID: {}", id);
        return taskRepository.save(taskDetails);
    }
    public List<TaskResponseDto> getTasksDueSoon() {
        return taskRepository.findTasksDueInNextSevenDays()
                .stream()
                .map(taskMapper::toResponseDto)
                .toList();
    }
    @Transactional(
            readOnly = false,
            propagation = Propagation.REQUIRED,
            rollbackFor = TaskNotFoundException.class
    )
    public void bulkCompleteTasks(List<Long> ids) {
        log.info("Запуск массового завершения для задач: {}", ids);

        for (Long id : ids) {
            Task task = taskRepository.findById(id)
                    .orElseThrow(() -> new TaskNotFoundException(
                            "Ошибка массового обновления: Задача с ID " + id + " не найдена."
                    ));

            task.setCompleted(true);
            taskRepository.save(task);
            log.debug("Задача с ID {} помечена как выполненная в контексте транзакции", id);
        }
    }
    public List<Task> getAllTasksWithAttachments() {
        log.info("Загрузка всех задач с их вложениями одним запросом");
        return taskRepository.findAllWithAttachmentsGraph();
    }
    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
        log.info("Task deleted with ID: {}", id);
    }
}