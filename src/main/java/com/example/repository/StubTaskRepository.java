package com.example.repository;

import com.example.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StubTaskRepository implements TaskRepository {

    private final Map<Integer, Task> tasks = new HashMap<>();

    public StubTaskRepository() {
        tasks.put(1, new Task(1, "Задача 1", "Сделать домашнее задание", false));
        tasks.put(2, new Task(2, "Задание 2", "Починить холодильник", false));
    }

    @Override
    public Task save(Task task) {

        return task;
    }

    @Override
    public Task findById(int id) {
        return tasks.get(id);
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteById(int id) {

    }
}
