package com.example.repository;

import com.example.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final AtomicLong curId = new AtomicLong(1);
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();

    @Override
    public Task save(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (task.getId() == null || task.getId() == 0) {
            long newId = curId.getAndIncrement();
            task.setId(newId);
            log.info("Generated new ID: {} for task: {}", newId, task.getTitle());
        }

        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task findById(Long id) {
        if (id == null) {
            return null;
        }
        return tasks.get(id);
    }

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteById(Long id) {
        if (id != null) {
            tasks.remove(id);
        }
    }
}