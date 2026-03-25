package com.example.validation;

import com.example.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DueDateValidator implements ConstraintValidator<ValidDueDate, TaskUpdateDto> {

    @Override
    public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
        // Если dueDate не передан (null), то валидация не нужна
        if (dto.getDueDate() == null) {
            return true;
        }

        // Здесь мы не можем получить createdDate из Task, так как у нас только DTO
        // Поэтому эту проверку лучше делать в сервисе
        // Или передавать createdDate через другой механизм

        // Для простоты пока вернем true, а реальную проверку сделаем в сервисе
        // Но для демонстрации кастомного валидатора оставим базовую проверку
        return dto.getDueDate().isAfter(LocalDate.now());
    }
}