package com.teamflow.task.dto;

import com.teamflow.task.entity.TaskPriority;
import com.teamflow.task.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Long projectId;
    private String createdBy;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private String assignedTo;
}