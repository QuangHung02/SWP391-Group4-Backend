package com.example.pregnancy_tracking.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

class ReminderHealthAlertServiceTest {

    @InjectMocks
    private ReminderHealthAlertService reminderHealthAlertService;

    public ReminderHealthAlertServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendHealthAlert() {
        Long pregnancyId = 1L;
        // Add logic to test the sendHealthAlert method
        reminderHealthAlertService.sendHealthAlert(pregnancyId);
        // Verify that the method behaves as expected
    }
}
