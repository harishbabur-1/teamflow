package com.teamflow.task.dto;

import com.teamflow.task.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateTaskRequest {

    @NotBlank(message = "Task title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    private String description;

    private TaskPriority priority;

    private LocalDate dueDate;
}