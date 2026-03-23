package com.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(exclude = {"createdAt", "dueDate", "priority", "tags"})
@ToString(exclude = {"createdAt", "dueDate", "priority", "tags"})
public class Task {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDate dueDate;
    private Priority priority;
    private Set<String> tags;
}
