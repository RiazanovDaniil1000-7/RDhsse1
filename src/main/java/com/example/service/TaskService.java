package com.example.service;

import com.example.model.Task;
import com.example.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TaskService {

    private final Map<Integer, Task> taskCache = new ConcurrentHashMap<>(); // Thread-safe
    private final TaskRepository taskRepository;

    @Value("${app.name}")
    private String applicationName;

    @Value("${app.version}")
    private String applicationVersion;

    @PostConstruct
    public void initCache() {
        System.out.println(">>> [TaskService]: Выполняется @PostConstruct - загрузка кэша...");
        List<Task> tasks = taskRepository.findAll();
        tasks.forEach(task -> taskCache.put(task.getId(), task));
        System.out.println(">>> [TaskService]: Кэш инициализирован. Количество задач: " + taskCache.size());
    }

    @PreDestroy
    public void cleanup() {
        System.out.println("<<< [TaskService]: Выполняется @PreDestroy...");
        System.out.println("<<< [TaskService]: Всего задач в кэше перед удалением: " + taskCache.size());
        taskCache.clear();
        System.out.println("<<< [TaskService]: Ресурсы очищены.");
    }

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task getById(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        return taskRepository.findById(id);
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be empty");
        }
        Task savedTask = taskRepository.save(task);
        taskCache.put(savedTask.getId(), savedTask); // Синхронизируем кэш
    }

    public Task updateTask(int id, Task taskDetails) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        if (taskDetails == null) {
            throw new IllegalArgumentException("Task details cannot be null");
        }
        taskDetails.setId(id);
        Task updatedTask = taskRepository.save(taskDetails);
        taskCache.put(id, updatedTask); // Обновляем кэш
        return updatedTask;
    }

    public void deleteTask(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        taskRepository.deleteById(id);
        taskCache.remove(id); // Удаляем из кэша
    }

    public void printAppInfo() {
        System.out.println(">>> Running App: " + applicationName + " v" + applicationVersion);
    }
}