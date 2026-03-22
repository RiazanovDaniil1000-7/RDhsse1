package com.example.controller;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.dto.TaskUpdateDto;
import com.example.mapper.TaskMapper;
import com.example.model.Task;
import com.example.service.TaskService;
import com.example.service.TaskStatisticsService;
import com.example.validation.OnUpdate;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskStatisticsService taskStatisticsService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskStatisticsService taskStatisticsService,
            TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskStatisticsService = taskStatisticsService;
        this.taskMapper = taskMapper;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        List<Task> tasks = taskService.getAll();
        List<TaskResponseDto> responseDtos = tasks.stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getById(@PathVariable int id) {
        Task task = taskService.getById(id);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }
        TaskResponseDto responseDto = taskMapper.toResponseDto(task);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable int id,
            @Validated(OnUpdate.class) @RequestBody TaskUpdateDto updateDto) {

        Task existingTask = taskService.getById(id);
        if (existingTask == null) {
            return ResponseEntity.notFound().build();
        }
        // Валидация dueDate относительно createdDate
        if (updateDto.getDueDate() != null &&
                updateDto.getDueDate().isBefore(existingTask.getCreatedAt().toLocalDate())) {
            return ResponseEntity.badRequest()
                    .body(null);
        }

        taskMapper.updateEntity(updateDto, existingTask);
        Task updatedTask = taskService.updateTask(id, existingTask);

        return ResponseEntity.ok(taskMapper.toResponseDto(updatedTask));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> addTask(@Validated(OnUpdate.class) @RequestBody TaskCreateDto createDto) {
        if (createDto.getTitle() == null || createDto.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Task task = taskMapper.toEntity(createDto);
        taskService.addTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toResponseDto(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable int id) {
        if (taskService.getById(id) == null) {
            return ResponseEntity.notFound().build();
        }
        taskService.deleteTask(id);
        return ResponseEntity.ok("Задача удалена.");
    }

    @GetMapping("/stats")
    public ResponseEntity<String> getStatistics() {
        return ResponseEntity.ok(taskStatisticsService.getComparisonReport());
    }
}