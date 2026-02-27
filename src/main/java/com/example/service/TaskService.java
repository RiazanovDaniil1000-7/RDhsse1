package com.example.service;


import com.example.model.Task;
import com.example.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final Map<Integer, Task> taskCache = new HashMap<>();
    private final TaskRepository taskRepository;

    @PostConstruct
    public void initCache() {
        System.out.println(">>> [TaskService]: Выполняется @PostConstruct - загрузка кэша...");

        // Загружаем существующие задачи из репозитория в кэш
        List<Task> tasks = taskRepository.findAll();
        tasks.forEach(task -> taskCache.put(task.getId(), task));
        System.out.println(
            ">>> [TaskService]: Кэш инициализирован. Количество задач: " + taskCache.size());
    }

    // 2. Очистка ресурсов перед уничтожением бина
    @PreDestroy
    public void cleanup() {
        System.out.println("<<< [TaskService]: Выполняется @PreDestroy...");
        System.out.println(
            "<<< [TaskService]: Сохранение статистики... Всего задач в кэше перед удалением: "
                + taskCache.size());
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
        return taskRepository.findById(id);
    }

    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(int id) {
        taskRepository.deleteById(id);
    }
}
