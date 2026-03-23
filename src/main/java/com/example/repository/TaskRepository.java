package com.example.repository;

import com.example.model.Task;
import java.util.List;

public interface TaskRepository {

    Task save(Task task);

    Task findById(Long id);

    List<Task> findAll();

    void deleteById(Long id);
}
