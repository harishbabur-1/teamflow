package com.teamflow.task.repository;

import com.teamflow.task.entity.Task;
import com.teamflow.task.entity.TaskPriority;
import com.teamflow.task.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectId(Long projectId);

    Optional<Task> findByIdAndProjectId(Long id, Long projectId);

    List<Task> findByAssignedToEmail(String email);

    List<Task> findByCreatedByEmail(String email);

    List<Task> findByProjectIdAndStatus(Long projectId, TaskStatus status);

    List<Task> findByProjectIdAndPriority(Long projectId, TaskPriority priority);

    List<Task> findByProjectIdAndAssignedToId(Long projectId, Long userId);

    List<Task> findByProjectIdAndDueDateBeforeAndStatusNot(
            Long projectId,
            LocalDate today,
            TaskStatus status
    );
}