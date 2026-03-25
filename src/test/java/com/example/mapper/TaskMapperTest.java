package com.example.mapper;

import com.example.dto.TaskCreateDto;
import com.example.dto.TaskResponseDto;
import com.example.dto.TaskUpdateDto;
import com.example.model.Priority;
import com.example.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void toEntity_ShouldMapCreateDtoToTask() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Test Task");
        dto.setDescription("Test Description");
        dto.setDueDate(LocalDate.now().plusDays(1));  // ← LocalDate
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("test", "important"));

        Task task = taskMapper.toEntity(dto);

        assertThat(task.getId()).isNull();
        assertThat(task.getTitle()).isEqualTo("Test Task");
        assertThat(task.getDescription()).isEqualTo("Test Description");
        assertThat(task.isCompleted()).isFalse();
        assertThat(task.getCreatedAt()).isNotNull();
        assertThat(task.getDueDate()).isEqualTo(LocalDate.now().plusDays(1));  // ← LocalDate
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task.getTags()).containsExactlyInAnyOrder("test", "important");
    }

    @Test
    void updateEntity_ShouldUpdateOnlyNonNullFields() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(1L, "Old Title", "Old Description", false,
                now, null, Priority.LOW, new HashSet<>(Set.of("old")));

        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("New Title");
        updateDto.setPriority(Priority.HIGH);
        updateDto.setTags(new HashSet<>(Set.of("new", "updated")));

        taskMapper.updateEntity(updateDto, task);

        assertThat(task.getTitle()).isEqualTo("New Title");
        assertThat(task.getDescription()).isEqualTo("Old Description");
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task.isCompleted()).isFalse();
        assertThat(task.getCreatedAt()).isEqualTo(now);
        assertThat(task.getDueDate()).isNull();
        assertThat(task.getTags()).containsExactlyInAnyOrder("new", "updated");
    }

    @Test
    void updateEntity_ShouldIgnoreNullFields() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate dueDate = now.plusDays(1).toLocalDate();  // ← LocalDate
        Set<String> tags = new HashSet<>(Set.of("tag"));

        Task task = new Task(1L, "Original", "Original Desc", false,
                now, dueDate, Priority.MEDIUM, tags);

        TaskUpdateDto updateDto = new TaskUpdateDto();
        updateDto.setTitle("Updated");

        taskMapper.updateEntity(updateDto, task);

        assertThat(task.getTitle()).isEqualTo("Updated");
        assertThat(task.getDescription()).isEqualTo("Original Desc");
        assertThat(task.isCompleted()).isFalse();
        assertThat(task.getDueDate()).isEqualTo(dueDate);
        assertThat(task.getPriority()).isEqualTo(Priority.MEDIUM);
        assertThat(task.getTags()).containsExactly("tag");
    }

    @Test
    void updateEntity_WithEmptyDto_ShouldNotChangeTask() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate dueDate = now.plusDays(1).toLocalDate();  // ← LocalDate
        Task original = new Task(1L, "Original", "Desc", true,
                now, dueDate, Priority.HIGH, new HashSet<>(Set.of("tag")));

        Task task = new Task(1L, "Original", "Desc", true,
                now, dueDate, Priority.HIGH, new HashSet<>(Set.of("tag")));

        TaskUpdateDto updateDto = new TaskUpdateDto();

        taskMapper.updateEntity(updateDto, task);

        assertThat(task.getTitle()).isEqualTo(original.getTitle());
        assertThat(task.getDescription()).isEqualTo(original.getDescription());
        assertThat(task.isCompleted()).isEqualTo(original.isCompleted());
        assertThat(task.getDueDate()).isEqualTo(original.getDueDate());
        assertThat(task.getPriority()).isEqualTo(original.getPriority());
        assertThat(task.getTags()).containsExactlyElementsOf(original.getTags());
    }

    @Test
    void toResponseDto_ShouldMapTaskToResponseDto() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate dueDate = now.plusDays(1).toLocalDate();  // ← LocalDate
        Task task = new Task(1L, "Task Title", "Task Description", true,
                now, dueDate, Priority.HIGH, new HashSet<>(Set.of("tag1", "tag2")));

        TaskResponseDto dto = taskMapper.toResponseDto(task);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Task Title");
        assertThat(dto.getDescription()).isEqualTo("Task Description");
        assertThat(dto.isCompleted()).isTrue();
        assertThat(dto.getCreatedAt()).isEqualTo(now);
        assertThat(dto.getDueDate()).isEqualTo(dueDate);
        assertThat(dto.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(dto.getTags()).containsExactlyInAnyOrder("tag1", "tag2");
    }

    @Test
    void toResponseDto_WithNullFields_ShouldHandleNulls() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Only Title");

        TaskResponseDto dto = taskMapper.toResponseDto(task);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Only Title");
        assertThat(dto.getDescription()).isNull();
        assertThat(dto.isCompleted()).isFalse();
        assertThat(dto.getCreatedAt()).isNull();
        assertThat(dto.getDueDate()).isNull();
        assertThat(dto.getPriority()).isNull();
        assertThat(dto.getTags()).isNull();
    }
}