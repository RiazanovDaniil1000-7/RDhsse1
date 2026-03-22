package com.example.dto;

import com.example.model.Priority;
import com.example.validation.OnCreate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TaskCreateDto {

    @NotBlank(message = "Title cannot be null", groups = OnCreate.class)
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters", groups = OnCreate.class)
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters", groups = OnCreate.class)
    private String description;

    @FutureOrPresent(message = "Due date cannot be in the past", groups = OnCreate.class)
    private LocalDate dueDate;

    @NotNull(message = "Priority cannot be null", groups = OnCreate.class)
    private Priority priority;

    @Size(max = 5, message = "Cannot have more than 5 tags", groups = OnCreate.class)
    private Set<String> tags;
}
