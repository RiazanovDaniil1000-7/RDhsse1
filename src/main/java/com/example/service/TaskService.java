package com.example.service;


import com.example.model.Task;
import com.example.repository.TaskRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

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
