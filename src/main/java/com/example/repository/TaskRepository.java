package com.example.repository;

import com.example.model.Task;
import java.util.List;

public interface TaskRepository {

    Task save(Task task);

    Task findById(int id);

    List<Task> findAll();

    void deleteById(int id);
}
