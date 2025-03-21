package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.PregnancyStandardDTO;
import com.example.pregnancy_tracking.entity.PregnancyStandard;
import com.example.pregnancy_tracking.entity.MomStandard;
import com.example.pregnancy_tracking.entity.PregnancyStandardId;
import com.example.pregnancy_tracking.entity.Fetus;
import com.example.pregnancy_tracking.entity.Pregnancy;
import com.example.pregnancy_tracking.entity.FetusStatus;
import com.example.pregnancy_tracking.entity.FetusRecord;
import com.example.pregnancy_tracking.repository.PregnancyStandardRepository;
import com.example.pregnancy_tracking.repository.MomStandardRepository;
import com.example.pregnancy_tracking.repository.StandardMedicalTaskRepository;
import com.example.pregnancy_tracking.repository.FetusRepository;
import com.example.pregnancy_tracking.repository.FetusRecordRepository;
import com.example.pregnancy_tracking.dto.ReminderDTO;
import com.example.pregnancy_tracking.dto.ReminderMedicalTaskDTO;
import com.example.pregnancy_tracking.entity.StandardMedicalTask;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.pregnancy_tracking.exception.MembershipFeatureException;
import java.util.ArrayList;
import java.util.Collections;
import jakarta.transaction.Transactional;

@Service
public class StandardService {
    @Autowired
    private PregnancyStandardRepository pregnancyStandardRepository;

    @Autowired
    private MomStandardRepository momStandardRepository;

    @Autowired
    private StandardMedicalTaskRepository standardMedicalTaskRepository;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private FetusRecordRepository fetusRecordRepository;

    @Autowired
    private FetusRepository fetusRepository;

    @Autowired
    private MembershipService membershipService;
    @Autowired
    private NotificationService notificationService;

    public List<StandardMedicalTask> getAllStandardMedicalTasks() {
        return standardMedicalTaskRepository.findAllByOrderByWeekAsc();
    }

    public List<StandardMedicalTask> getStandardMedicalTasksByWeek(Integer week) {
        return standardMedicalTaskRepository.findByWeek(week);
    }

    public StandardMedicalTask createStandardMedicalTask(StandardMedicalTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Nhắc nhở không được để trống");
        }
        if (task.getWeek() == null || task.getTaskName() == null) {
            throw new IllegalArgumentException("Tuần thai và tên nhắc nhở là bắt buộc");
        }
        return standardMedicalTaskRepository.save(task);
    }

    public void deleteStandardMedicalTask(Long taskId) {
        standardMedicalTaskRepository.deleteById(taskId);
    }

    public List<PregnancyStandardDTO> getAllPregnancyStandards() {
        List<PregnancyStandard> standards = pregnancyStandardRepository.findAllByOrderByIdWeekAsc();
        return standards.stream().map(PregnancyStandardDTO::new).collect(Collectors.toList());
    }

    public Optional<MomStandard> getMomStandard(Integer week) {
        return momStandardRepository.findByWeek(week);
    }

    public void createReminderFromStandardTasks(Long userId, Long pregnancyId, Integer currentWeek) {
        if (userId == null || pregnancyId == null || currentWeek == null) {
            throw new IllegalArgumentException("ID người dùng, ID thai kỳ và tuần thai hiện tại không được để trống");
        }

        try {
            List<StandardMedicalTask> standardTasks = standardMedicalTaskRepository.findByWeek(currentWeek);

            if (!standardTasks.isEmpty()) {
                ReminderDTO reminderDTO = new ReminderDTO();
                reminderDTO.setUserId(userId);
                reminderDTO.setPregnancyId(pregnancyId);
                reminderDTO.setType("MEDICAL_TASK");
                reminderDTO.setReminderDate(LocalDate.now());

                List<ReminderMedicalTaskDTO> tasks = standardTasks.stream()
                        .map(standardTask -> new ReminderMedicalTaskDTO(
                                null,
                                null,
                                standardTask.getWeek(),
                                standardTask.getTaskType(),
                                standardTask.getTaskName(),
                                standardTask.getNotes(),
                                null))
                        .collect(Collectors.toList());

                reminderDTO.setTasks(tasks);
                reminderService.createReminderWithTasks(reminderDTO);

                String title = "Nhắc nhở mới";
                String body = String.format("Bạn có nhắc nhở mới cần thực hiện ở tuần %d", currentWeek);
                notificationService.sendMedicalTaskNotification(userId, title, body);
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo nhắc nhở từ nhiệm vụ tiêu chuẩn: " + e.getMessage());
        }
    }

    public void checkAndCreateWeeklyTasks(Long userId, Long pregnancyId, Integer currentWeek) {
        List<ReminderDTO> existingReminders = reminderService.getRemindersByPregnancyId(pregnancyId);

        boolean reminderExists = existingReminders.stream()
                .anyMatch(reminder -> reminder.getType().equals("MEDICAL") &&
                        reminder.getTasks().stream()
                                .anyMatch(task -> currentWeek.equals(task.getWeek())));

        if (!reminderExists) {
            createReminderFromStandardTasks(userId, pregnancyId, currentWeek);
        }
    }

    public Optional<PregnancyStandardDTO> getPregnancyStandard(Integer week, Integer fetusNumber) {
        PregnancyStandardId id = new PregnancyStandardId(week, fetusNumber);
        return pregnancyStandardRepository.findById(id)
                .map(PregnancyStandardDTO::new);
    }

    public void checkFetusStatus(Long fetusId) {
        Fetus fetus = fetusRepository.findById(fetusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai nhi"));

        List<FetusRecord> records = fetusRecordRepository.findByFetusFetusIdOrderByWeekAsc(fetusId);
        if (records.isEmpty()) {
            return;
        }
        FetusRecord latestRecord = records.get(records.size() - 1);

        Pregnancy pregnancy = fetus.getPregnancy();
        Integer currentWeek = latestRecord.getWeek();
        Integer totalFetuses = pregnancy.getTotalFetuses();

        PregnancyStandardId id = new PregnancyStandardId(currentWeek, totalFetuses);
        PregnancyStandard standard = pregnancyStandardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tiêu chuẩn cho tuần " + currentWeek));

        boolean isIssue = false;

        if (latestRecord.getFetalWeight() != null) {
            BigDecimal weight = latestRecord.getFetalWeight();
            if (weight.compareTo(standard.getMinWeight()) < 0 ||
                    weight.compareTo(standard.getMaxWeight()) > 0) {
                isIssue = true;
            }
        }

        if (latestRecord.getFemurLength() != null) {
            BigDecimal length = latestRecord.getFemurLength();
            if (length.compareTo(standard.getMinLength()) < 0 ||
                    length.compareTo(standard.getMaxLength()) > 0) {
                isIssue = true;
            }
        }

        if (latestRecord.getHeadCircumference() != null) {
            BigDecimal headCirc = latestRecord.getHeadCircumference();
            if (headCirc.compareTo(standard.getMinHeadCircumference()) < 0 ||
                    headCirc.compareTo(standard.getMaxHeadCircumference()) > 0) {
                isIssue = true;
            }
        }

        if (isIssue && fetus.getStatus() == FetusStatus.ACTIVE) {
            fetus.setStatus(FetusStatus.ISSUE);
            fetusRepository.save(fetus);
        }
    }

    public List<PregnancyStandardDTO> getPregnancyStandardsByFetusNumber(Integer fetusNumber) {
        List<PregnancyStandard> standards = pregnancyStandardRepository
                .findByIdFetusNumberOrderByIdWeekAsc(fetusNumber);
        return standards.stream()
                .map(PregnancyStandardDTO::new)
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStandardsWithPrediction(Integer fetusNumber, Integer currentWeek, Long userId) {
        if (!membershipService.canViewFetusRecord(userId)) {
            throw new MembershipFeatureException("Tính năng này yêu cầu gói Basic hoặc Premium");
        }

        List<PregnancyStandard> standards = pregnancyStandardRepository
                .findByIdFetusNumberOrderByIdWeekAsc(fetusNumber);
        List<PregnancyStandardDTO> currentStandards = standards.stream()
                .filter(s -> s.getId().getWeek() <= currentWeek)
                .map(PregnancyStandardDTO::new)
                .collect(Collectors.toList());

        List<Map<String, Object>> predictions = standards.stream()
                .filter(s -> s.getId().getWeek() > currentWeek)
                .map(standard -> {
                    Map<String, Object> prediction = new HashMap<>();
                    prediction.put("week", standard.getId().getWeek());
                    prediction.put("avgWeight", standard.getAvgWeight());
                    prediction.put("avgLength", standard.getAvgLength());
                    prediction.put("avgHeadCircumference", standard.getAvgHeadCircumference());
                    return prediction;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("standards", currentStandards);
        result.put("predictions", predictions);
        return result;
    }

    @Transactional
    public void createInitialMedicalTasks(Long userId, Long pregnancyId, Integer currentWeek) {
        if (userId == null || pregnancyId == null || currentWeek == null) {
            throw new IllegalArgumentException("ID người dùng, ID thai kỳ và tuần thai hiện tại không được để trống");
        }

        try {
            List<StandardMedicalTask> allStandardTasks = new ArrayList<>();
            for (int week = 1; week <= currentWeek; week++) {
                List<StandardMedicalTask> weekTasks = standardMedicalTaskRepository.findByWeek(week);
                allStandardTasks.addAll(weekTasks);
            }

            if (!allStandardTasks.isEmpty()) {
                for (StandardMedicalTask standardTask : allStandardTasks) {
                    ReminderDTO reminderDTO = new ReminderDTO();
                    reminderDTO.setUserId(userId);
                    reminderDTO.setPregnancyId(pregnancyId);
                    reminderDTO.setType("MEDICAL_TASK");
                    reminderDTO.setReminderDate(LocalDate.now());

                    List<ReminderMedicalTaskDTO> tasks = Collections.singletonList(
                            new ReminderMedicalTaskDTO(
                                    null,
                                    null,
                                    standardTask.getWeek(),
                                    standardTask.getTaskType(),
                                    standardTask.getTaskName(),
                                    standardTask.getNotes(),
                                    null));

                    reminderDTO.setTasks(tasks);
                    reminderService.createReminderWithTasks(reminderDTO);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Không thể tạo nhắc nhở từ nhiệm vụ tiêu chuẩn: " + e.getMessage());
        }
    }


public Integer getMaxWeekByFetusNumber(Integer fetusNumber) {
    return pregnancyStandardRepository.findByIdFetusNumberOrderByIdWeekDesc(fetusNumber)
        .stream()
        .findFirst()
        .map(standard -> standard.getId().getWeek())
        .orElse(40);
}
}

