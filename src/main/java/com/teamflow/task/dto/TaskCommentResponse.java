package com.teamflow.task.dto;

import java.time.LocalDateTime;

public record TaskCommentResponse(
        Long id,
        String content,
        Long taskId,
        String userEmail,
        LocalDateTime createdAt
) {
}