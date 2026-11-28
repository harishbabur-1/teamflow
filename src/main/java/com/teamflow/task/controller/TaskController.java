package com.teamflow.task.controller;

import com.teamflow.task.dto.*;
import com.teamflow.task.entity.TaskPriority;
import com.teamflow.task.entity.TaskStatus;
import com.teamflow.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTaskRequest request,
            Authentication authentication) {

        TaskResponse task = taskService.createTask(
                projectId,
                request,
                authentication
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @GetMapping
    public Page<TaskResponse> getProjectTasks(
            @PathVariable Long projectId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return taskService.getProjectTasks(
                projectId,
                authentication,
                pageable
        );
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            Authentication authentication) {

        TaskResponse task = taskService.getTaskById(
                projectId,
                taskId,
                authentication
        );

        return ResponseEntity.ok(task);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            Authentication authentication) {

        TaskResponse task = taskService.updateTask(
                projectId,
                taskId,
                request,
                authentication
        );

        return ResponseEntity.ok(task);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request,
            Authentication authentication) {

        TaskResponse task = taskService.updateTaskStatus(
                projectId,
                taskId,
                request,
                authentication
        );

        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            Authentication authentication) {

        taskService.deleteTask(projectId, taskId, authentication);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<Void> assignTask(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request,
            Authentication authentication) {

        taskService.assignTask(
                projectId,
                taskId,
                request,
                authentication
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/assigned-to-me")
    public ResponseEntity<List<TaskResponse>> getMyAssignedTasks(
            Authentication authentication) {

        return ResponseEntity.ok(
                taskService.getMyAssignedTasks(authentication)
        );
    }

    @GetMapping("/created-by-me")
    public ResponseEntity<List<TaskResponse>> getMyCreatedTasks(
            Authentication authentication) {

        return ResponseEntity.ok(
                taskService.getMyCreatedTasks(authentication)
        );
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponse>> getTasksByStatus(
            @PathVariable Long projectId,
            @PathVariable TaskStatus status,
            Authentication authentication) {

        return ResponseEntity.ok(
                taskService.getTasksByStatus(
                        projectId,
                        status,
                        authentication
                )
        );
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskResponse>> getTasksByPriority(
            @PathVariable Long projectId,
            @PathVariable TaskPriority priority,
            Authentication authentication) {

        return ResponseEntity.ok(
                taskService.getTasksByPriority(
                        projectId,
                        priority,
                        authentication
                )
        );
    }

    @GetMapping("/assignee/{userId}")
    public ResponseEntity<List<TaskResponse>> getTasksByAssignee(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            Authentication authentication) {

        return ResponseEntity.ok(
                taskService.getTasksByAssignee(
                        projectId,
                        userId,
                        authentication
                )
        );
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks(
            @PathVariable Long projectId,
            Authentication authentication) {

        return ResponseEntity.ok(
                taskService.getOverdueTasks(projectId, authentication)
        );
    }

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<TaskCommentResponse> addComment(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                taskService.addComment(
                        projectId,
                        taskId,
                        request,
                        authentication
                )
        );
    }

    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<TaskCommentResponse>> getTaskComments(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            Authentication authentication) {

        return ResponseEntity.ok(
                taskService.getTaskComments(
                        projectId,
                        taskId,
                        authentication
                )
        );
    }

    @PutMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<TaskCommentResponse> updateComment(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {

        return ResponseEntity.ok(
                taskService.updateComment(
                        projectId,
                        taskId,
                        commentId,
                        request,
                        authentication
                )
        );
    }

    @DeleteMapping("/{taskId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long projectId,
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            Authentication authentication) {

        taskService.deleteComment(
                projectId,
                taskId,
                commentId,
                authentication
        );

        return ResponseEntity.noContent().build();
    }
}