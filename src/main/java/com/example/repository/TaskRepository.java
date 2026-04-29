package com.example.repository;

import com.example.model.Priority;
import com.example.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    List<Task> findByCompleted(boolean completed);

    List<Task> findByTitleContainingIgnoreCase(String title);

    List<Task> findByDueDateBefore(java.time.LocalDate date);
    @Query(value = "SELECT * FROM tasks WHERE due_date >= CURRENT_DATE " +
            "AND due_date <= (CURRENT_DATE + INTERVAL '7 days')",
            nativeQuery = true)
    List<Task> findTasksDueInNextSevenDays();
    @EntityGraph(attributePaths = {"attachments"})
    @Query("SELECT t FROM Task t")
    List<Task> findAllWithAttachmentsGraph();
}