package com.example.repository;

import com.example.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final AtomicInteger curId = new AtomicInteger(1);
    private final Map<Integer, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (task.getId() == 0) {
            // Новая задача
            task.setId(curId.getAndIncrement());
        } else {
            // Обновление существующей задачи
            // Проверяем, не пытаемся ли мы использовать ID больше текущего счетчика
            if (task.getId() >= curId.get()) {
                curId.set(task.getId() + 1);
            }
        }
        tasks.put(task.getId(), task);
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
        tasks.remove(id);
    }
}