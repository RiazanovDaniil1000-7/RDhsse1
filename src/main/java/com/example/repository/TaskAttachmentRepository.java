package com.example.repository;

import com.example.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskAttachmentRepository {
    private final Map<Long, TaskAttachment> attachments = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(idGenerator.getAndIncrement());
        }
        attachments.put(attachment.getId(), attachment);
        return attachment;
    }

    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(attachments.get(id));
    }

    public List<TaskAttachment> findByTaskId(Long taskId) {
        List<TaskAttachment> list = new ArrayList<>();
        for (TaskAttachment attachment : attachments.values()) {
            if (attachment.getTaskId().equals(taskId)) {
                list.add(attachment);
            }
        }
        return list;
    }

    public void deleteById(Long id) {
        attachments.remove(id);
    }

    public void deleteByTaskId(Long taskId) {
        attachments.values().removeIf(attachment -> attachment.getTaskId().equals(taskId));
    }
}