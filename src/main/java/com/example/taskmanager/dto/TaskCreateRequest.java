package com.example.taskmanager.dto;

import com.example.taskmanager.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskCreateRequest(
        @NotBlank
        @Size(max = 100)
        String title,

        @Size(max = 500)
        String description,

        TaskStatus status,

        LocalDate dueDate
) {
}
