package com.example.dto;

import com.example.model.Priority;
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
    private String title;
    private String description;
    private LocalDate dueDate;
    private Priority priority;
    private Set<String> tags;
}
