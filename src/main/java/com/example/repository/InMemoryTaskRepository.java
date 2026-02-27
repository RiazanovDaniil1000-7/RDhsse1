package com.example.repository;

import com.example.model.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    int curId = 1;
    Map<Integer, Task> tasks = new HashMap<>();

    @Override
    public Task save(Task task) {
        if (task.getId() == 0) {
            task.setId(curId);
            curId++;
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
