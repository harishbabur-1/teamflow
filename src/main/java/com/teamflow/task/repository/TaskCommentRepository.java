package com.teamflow.task.repository;

import com.teamflow.task.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    List<TaskComment> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    Optional<TaskComment> findByIdAndTaskId(Long id, Long taskId);

    Optional<TaskComment> findByIdAndUserEmail(Long id, String email);

    Optional<TaskComment> findByIdAndTaskIdAndUserEmail(
            Long id,
            Long taskId,
            String email
    );

}