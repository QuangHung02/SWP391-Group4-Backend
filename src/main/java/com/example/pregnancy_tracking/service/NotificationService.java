package com.example.pregnancy_tracking.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.pregnancy_tracking.repository.UserRepository;
import com.example.pregnancy_tracking.entity.User;

@Slf4j
@Service
public class NotificationService {
    @Autowired
    private UserRepository userRepository;
    
    private final FirebaseMessaging firebaseMessaging;

    public NotificationService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    private String getUserFcmToken(Long userId) {
        return userRepository.findById(userId)
            .map(User::getFcmToken)
            .orElse(null);
    }

    public void sendMedicalTaskNotification(Long userId, String title, String body) {
        String userFcmToken = getUserFcmToken(userId);
        
        if (userFcmToken == null) {
            log.warn("No FCM token found for user {}", userId);
            return;
        }
        
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setToken(userFcmToken)
            .build();

        try {
            firebaseMessaging.send(message);
            log.info("Notification sent successfully to user {}", userId);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }

    public void sendHealthAlertNotification(Long userId, String title, String body) {
        String userFcmToken = getUserFcmToken(userId);
        
        if (userFcmToken == null || userFcmToken.isEmpty()) {
            log.warn("No FCM token found for user {}", userId);
            return;
        }

        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setToken(userFcmToken)
            .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }
}