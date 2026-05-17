package com.homeservices.notificationservice.controller;

import com.homeservices.notificationservice.entity.Notification;
import com.homeservices.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;


    @GetMapping("/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return service.getUserNotifications(userId);
    }

    @GetMapping("/all")
    public List<Notification> getAllNotifications() {
        return service.getAllNotifications();
    }

    @GetMapping("/admin/alerts")
    public List<Notification> getAdminAlerts() {
        return service.getNotificationsByType("ADMIN");
    }
}