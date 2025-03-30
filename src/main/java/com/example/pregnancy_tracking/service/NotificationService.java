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
import java.util.HashMap;
import java.util.Map;
import com.example.pregnancy_tracking.entity.NotificationType;
import java.util.List;

@Slf4j
@Service
public class NotificationService {
    @Autowired
    private UserRepository userRepository;
    
    private final FirebaseMessaging firebaseMessaging;
    private final int MAX_RETRIES = 3;

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
        
        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.MEDICAL_TASK.getValue());
        data.put("userId", userId.toString());
        
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .putAllData(data)
            .setToken(userFcmToken)
            .build();

        sendWithRetry(message, userId, MAX_RETRIES);
    }

    private void sendWithRetry(Message message, Long userId, int retriesLeft) {
        try {
            firebaseMessaging.send(message);
            log.debug("Notification sent successfully to user {}", userId);
        } catch (FirebaseMessagingException e) {
            if (retriesLeft > 0) {
                log.warn("Retrying notification for user {}. Attempts left: {}", userId, retriesLeft - 1);
                sendWithRetry(message, userId, retriesLeft - 1);
            } else {
                log.error("Failed to send notification to user {} after all retries: {}", userId, e.getMessage());
            }
        }
    }

    public void sendBatchHealthAlertNotifications(Long userId, List<String> messages) {
        String userFcmToken = getUserFcmToken(userId);
        if (userFcmToken == null || userFcmToken.isEmpty()) {
            log.warn("No FCM token found for user {}", userId);
            return;
        }

        // Combine all messages
        String title = "Cảnh báo sức khỏe";
        String body = String.join("\n", messages);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.HEALTH_ALERT.getValue());
        data.put("userId", userId.toString());
        data.put("count", String.valueOf(messages.size()));

        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .putAllData(data)
            .setToken(userFcmToken)
            .build();

        sendWithRetry(message, userId, MAX_RETRIES);
    }

    public void sendBatchMedicalTaskNotifications(Long userId, List<String> messages) {
        String userFcmToken = getUserFcmToken(userId);
        if (userFcmToken == null) {
            log.warn("No FCM token found for user {}", userId);
            return;
        }

        // Combine all messages
        String title = "Nhắc nhở y tế mới";
        String body = String.join("\n", messages);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.MEDICAL_TASK.getValue());
        data.put("userId", userId.toString());
        data.put("count", String.valueOf(messages.size()));

        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .putAllData(data)
            .setToken(userFcmToken)
            .build();

        sendWithRetry(message, userId, MAX_RETRIES);
    }

    public void sendHealthAlertNotification(Long userId, String title, String body) {
        String userFcmToken = getUserFcmToken(userId);
        if (userFcmToken == null || userFcmToken.isEmpty()) {
            log.warn("No FCM token found for user {}", userId);
            return;
        }
    
        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.HEALTH_ALERT.getValue());
        data.put("userId", userId.toString());
    
        Message message = Message.builder()
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .putAllData(data)
            .setToken(userFcmToken)
            .build();
    
        sendWithRetry(message, userId, MAX_RETRIES);
    }
}