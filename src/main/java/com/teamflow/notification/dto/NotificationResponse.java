package com.teamflow.notification.dto;

import com.teamflow.notification.entity.NotificationType;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String message,
        NotificationType type,
        boolean read,
        Long taskId,
        LocalDateTime createdAt
) {
}