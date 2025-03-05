package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.service.ReminderHealthAlertService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;

class ReminderHealthAlertControllerTest {

    @InjectMocks
    private ReminderHealthAlertController reminderHealthAlertController;

    @Mock
    private ReminderHealthAlertService reminderHealthAlertService;

    public ReminderHealthAlertControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendHealthAlert() {
        Long pregnancyId = 1L;
        ResponseEntity<String> response = reminderHealthAlertController.sendHealthAlert(pregnancyId);
        
        verify(reminderHealthAlertService, times(1)).sendHealthAlert(pregnancyId);
        assert(response.getStatusCode().is2xxSuccessful());
    }
}
