package com.homeservices.notificationservice.service;

import com.homeservices.notificationservice.entity.Notification;
import com.homeservices.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repo;

    public void save(Long userId, String message, String type) {
        Notification notification = Notification.builder()
                .userId(userId)
                .message(message)
                .type(type)
                .build();

        repo.save(notification);
    }

    public List<Notification> getUserNotifications(Long userId) {
        return repo.findByUserId(userId);
    }

    public List<Notification> getAllNotifications() {
        return repo.findAll();
    }

    public List<Notification> getNotificationsByType(String type) {
        return repo.findByType(type);
    }
}