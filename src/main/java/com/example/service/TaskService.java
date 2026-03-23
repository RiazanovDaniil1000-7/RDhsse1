package com.example.service;

import com.example.model.Task;
import com.example.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();
    private final TaskRepository taskRepository;

    @Value("${app.name}")
    private String applicationName;

    @Value("${app.version}")
    private String applicationVersion;

    @PostConstruct
    public void initCache() {
        log.info(">>> [TaskService]: Выполняется @PostConstruct - загрузка кэша...");
        List<Task> tasks = taskRepository.findAll();
        tasks.forEach(task -> taskCache.put(task.getId(), task));
        log.info(">>> [TaskService]: Кэш инициализирован. Количество задач: " + taskCache.size());
    }

    @PreDestroy
    public void cleanup() {
        log.info("<<< [TaskService]: Выполняется @PreDestroy...");
        log.info("<<< [TaskService]: Всего задач в кэше перед удалением: " + taskCache.size());
        taskCache.clear();
        log.info("<<< [TaskService]: Ресурсы очищены.");
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task getById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        return taskRepository.findById(id);
    }

    public Task addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }

        Task savedTask = taskRepository.save(task);
        taskCache.put(savedTask.getId(), savedTask);

        log.info("Task saved with ID: {}", savedTask.getId());

        return savedTask;
    }

    public Task updateTask(Long id, Task taskDetails) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        if (taskDetails == null) {
            throw new IllegalArgumentException("Task details cannot be null");
        }
        taskDetails.setId(id);
        Task updatedTask = taskRepository.save(taskDetails);
        taskCache.put(id, updatedTask);
        return updatedTask;
    }

    public void deleteTask(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        taskRepository.deleteById(id);
        taskCache.remove(id);
    }

    public void printAppInfo() {
        log.info(">>> Running App: {} v{}", applicationName, applicationVersion);
    }
}