package com.teamflow.notification.service;

import com.teamflow.notification.entity.Notification;
import com.teamflow.notification.entity.NotificationType;
import com.teamflow.notification.repository.NotificationRepository;
import com.teamflow.task.entity.Task;
import com.teamflow.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.teamflow.notification.dto.NotificationResponse;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(
            User user,
            Task task,
            NotificationType type,
            String message) {

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTask(task);
        notification.setType(type);
        notification.setMessage(message);

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(String email) {

        List<Notification> notifications =
                notificationRepository.findByUserEmailOrderByCreatedAtDesc(email);

        return notifications.stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getType(),
                        notification.isRead(),
                        notification.getTask() != null
                                ? notification.getTask().getId()
                                : null,
                        notification.getCreatedAt()
                ))
                .toList();
    }

    @Transactional
    public void markAsRead(Long notificationId, String email) {

        Notification notification =
                notificationRepository.findByIdAndUserEmail(notificationId, email)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Notification not found"
                        ));

        notification.setRead(true);

        notificationRepository.save(notification);
    }
}