package com.example.controller;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.dto.TaskUpdateDto;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.service.TaskService;
import com.example.service.TaskStatisticsService;
import com.example.validation.OnCreate;
import com.example.validation.OnUpdate;
import jakarta.validation.Valid;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskStatisticsService taskStatisticsService;
    private final TaskMapper taskMapper;

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<Task> tasks = taskService.getAll();
        List<TaskResponseDto> responseDtos = new ArrayList<>();
        for (Task task : tasks) {
            TaskResponseDto responseDto = taskMapper.toResponseDto(task);
            responseDtos.add(responseDto);
        }
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getById(@PathVariable Long id) {
        Task task = taskService.getById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(taskMapper.toResponseDto(task));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> addTask(
            @Validated(OnCreate.class) @RequestBody TaskCreateDto createDto) {

        Task task = taskMapper.toEntity(createDto);
        Task savedTask = taskService.addTask(task);  // ← получаем сохраненную задачу с ID

        log.info("Task created with ID: {}", savedTask.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskMapper.toResponseDto(savedTask));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody TaskUpdateDto updateDto) {

        Task existingTask = taskService.getById(id);
        if (existingTask == null) {
            return ResponseEntity.notFound().build();
        }

        // Валидация dueDate относительно createdDate
        if (updateDto.getDueDate() != null &&
                updateDto.getDueDate().isBefore(existingTask.getCreatedAt().toLocalDate())) {
            return ResponseEntity.badRequest().build();
        }

        taskMapper.updateEntity(updateDto, existingTask);
        Task updatedTask = taskService.updateTask(id, existingTask);

        return ResponseEntity.ok(taskMapper.toResponseDto(updatedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        Task existingTask = taskService.getById(id);
        if (existingTask == null) {
            return ResponseEntity.notFound().build();
        }

        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<String> getStatistics() {
        return ResponseEntity.ok(taskStatisticsService.getComparisonReport());
    }
}