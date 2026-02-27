package com.example.controller;

import com.example.model.Task;
import com.example.service.TaskService;
import com.example.service.TaskStatisticsService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskStatisticsService taskStatisticsService;

    public TaskController(TaskService taskService, TaskStatisticsService taskStatisticsService) {
        this.taskService = taskService;
        this.taskStatisticsService = taskStatisticsService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAll();
    }

    @GetMapping("/{id}")
    public Task getById(@PathVariable int id) {
        return taskService.getById(id);
    }

    @PostMapping
    public String addTask(@RequestBody Task task) {
        taskService.addTask(task);
        return "Задача успешно добавлена";
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable int id) {
        taskService.deleteTask(id);
        return "Задача удалена.";
    }

    @GetMapping("/stats")
    public String getStatistics() {
        return taskStatisticsService.getComparisonReport();
    }
}
