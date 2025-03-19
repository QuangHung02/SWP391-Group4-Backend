package com.example.pregnancy_tracking.scheduler;

import com.example.pregnancy_tracking.service.NotificationService;
import com.example.pregnancy_tracking.repository.ReminderRepository;
import com.example.pregnancy_tracking.entity.Reminder;
import com.example.pregnancy_tracking.entity.ReminderStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Component
public class ReminderScheduler {
    private final ReminderRepository reminderRepository;
    private final NotificationService notificationService;

    public ReminderScheduler(ReminderRepository reminderRepository, 
                           NotificationService notificationService) {
        this.reminderRepository = reminderRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void checkAndNotifyReminders() {
        LocalDate today = LocalDate.now();
        List<Reminder> dueReminders = reminderRepository
            .findByReminderDateAndStatus(today, ReminderStatus.NOT_YET);

        for (Reminder reminder : dueReminders) {
            String title = "Nhắc nhở y tế";
            String body = "Bạn có nhiệm vụ y tế cần thực hiện hôm nay";
            notificationService.sendMedicalTaskNotification(
                reminder.getUser().getId(), 
                title, 
                body
            );
        }
    }
}