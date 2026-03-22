package com.example.dto;

import com.example.model.Priority;
import com.example.validation.OnUpdate;
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
public class TaskUpdateDto {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters", groups = OnUpdate.class)
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters", groups = OnUpdate.class)
    private String description;

    private Boolean completed;  // Boolean - может быть null, значит поле не передано

    private LocalDate dueDate;  // Без @FutureOrPresent пока, проверим в кастомном валидаторе

    private Priority priority;

    @Size(max = 5, message = "Cannot have more than 5 tags", groups = OnUpdate.class)
    private Set<String> tags;
}
