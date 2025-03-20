package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.dto.FetusRecordDTO;
import com.example.pregnancy_tracking.entity.*;
import com.example.pregnancy_tracking.repository.*;
import com.example.pregnancy_tracking.exception.MembershipFeatureException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.temporal.ChronoUnit;
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.pregnancy_tracking.exception.HealthAlertException;

@Service
public class FetusRecordService {
    @Autowired
    private MembershipService membershipService;

    @Autowired
    private FetusRecordRepository fetusRecordRepository;

    @Autowired
    private FetusRepository fetusRepository;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private PregnancyStandardRepository pregnancyStandardRepository;

    @Autowired
    private ReminderHealthAlertRepository reminderHealthAlertRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GrowthChartShareRepository growthChartShareRepository;

    @Autowired
    private CommunityPostRepository postRepository;

    @Autowired
    private NotificationService notificationService;

    public Map<String, Object> prepareGrowthChartData(Long fetusId, Set<ChartType> chartTypes, Long userId) {
        if (!membershipService.canViewFetusRecord(userId)) {
            throw new MembershipFeatureException("Requires active subscription to view fetus records");
        }
        Map<String, Object> chartData = new HashMap<>();
        List<FetusRecord> records = fetusRecordRepository.findByFetusFetusIdOrderByWeekAsc(fetusId);
        
        if (chartTypes.contains(ChartType.WEIGHT)) {
            chartData.put("fetalWeight", records.stream()
                .filter(r -> r.getFetalWeight() != null)
                .map(r -> new Object[]{r.getWeek(), r.getFetalWeight()})
                .collect(Collectors.toList()));
        }
        if (chartTypes.contains(ChartType.LENGTH)) {
            chartData.put("femurLength", records.stream()
                .filter(r -> r.getFemurLength() != null)
                .map(r -> new Object[]{r.getWeek(), r.getFemurLength()})
                .collect(Collectors.toList()));
        }
        if (chartTypes.contains(ChartType.HEAD_CIRCUMFERENCE)) {
            chartData.put("headCircumference", records.stream()
                .filter(r -> r.getHeadCircumference() != null)
                .map(r -> new Object[]{r.getWeek(), r.getHeadCircumference()})
                .collect(Collectors.toList()));
        }
        
        return chartData;
    }

    private SeverityLevel checkThreshold(BigDecimal value, BigDecimal min, BigDecimal max, SeverityLevel currentSeverity) {
        if (min == null || max == null) return currentSeverity;
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return SeverityLevel.MEDIUM;
        }
        if (value.compareTo(min) <= 0 || value.compareTo(max) >= 0) {
            return currentSeverity == SeverityLevel.LOW ? SeverityLevel.LOW : SeverityLevel.MEDIUM;
        }
        return currentSeverity;
    }
    
    public Map<String, List<Object[]>> getAllGrowthData(Long fetusId) {
        List<FetusRecord> records = fetusRecordRepository.findByFetusFetusIdOrderByWeekAsc(fetusId);
        
        Map<String, List<Object[]>> growthData = new HashMap<>();
        growthData.put("headCircumference", records.stream()
            .map(r -> new Object[]{r.getWeek(), r.getHeadCircumference()})
            .collect(Collectors.toList()));
        growthData.put("fetalWeight", records.stream()
            .map(r -> new Object[]{r.getWeek(), r.getFetalWeight()})
            .collect(Collectors.toList()));
        growthData.put("femurLength", records.stream()
            .map(r -> new Object[]{r.getWeek(), r.getFemurLength()})
            .collect(Collectors.toList()));
            
        return growthData;
    }

    @Transactional
    public void updateRecordsForPregnancy(Long pregnancyId, LocalDate newExamDate, 
                                        LocalDate lastExamDate, int newWeeks, int oldWeeks) {
        List<FetusRecord> records = fetusRecordRepository.findByFetusPregnancyPregnancyId(pregnancyId);
        int weekDifference = newWeeks - oldWeeks;
        
        for (FetusRecord record : records) {
            int adjustedWeek = record.getWeek() + weekDifference;
            record.setWeek(adjustedWeek);
            fetusRecordRepository.save(record);
        }
    }

    public GrowthChartShare getGrowthChartByPostId(Long postId) {
        return growthChartShareRepository.findByPostPostId(postId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biểu đồ chia sẻ"));
    }

    public List<GrowthChartShare> getGrowthChartsByFetusId(Long fetusId) {
        return growthChartShareRepository.findByFetusFetusId(fetusId);
    }

    public GrowthChartShare createGrowthChartShare(Long fetusId, Set<ChartType> chartTypes, 
                                                  String title, String content, Long userId,
                                                  Boolean isAnonymous) {
            if (!membershipService.canShareGrowthChart(userId)) {
                throw new MembershipFeatureException("Gói thành viên của bạn không cho phép chia sẻ biểu đồ");
            }
    
            Fetus fetus = fetusRepository.findById(fetusId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thai nhi"));
                
            Map<String, Object> chartData = prepareGrowthChartData(fetusId, chartTypes, userId);
            
            CommunityPost post = new CommunityPost();
            post.setTitle(title);
            post.setContent(content);
            post.setAuthor(fetus.getPregnancy().getUser());
            post.setIsAnonymous(isAnonymous != null ? isAnonymous : false);
            post.setCreatedAt(LocalDateTime.now());
            post = postRepository.save(post);
            
            GrowthChartShare share = new GrowthChartShare();
            share.setPost(post);
            share.setFetus(fetus);
            share.setSharedTypes(chartTypes);
            try {
                share.setChartData(objectMapper.writeValueAsString(chartData));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Lỗi khi xử lý dữ liệu biểu đồ", e);
            }
            share.setCreatedAt(LocalDateTime.now());
            
            return growthChartShareRepository.save(share);
        }

        @Transactional
        public FetusRecord createRecord(Long fetusId, FetusRecordDTO recordDTO, Long userId) {
            if (!membershipService.canCreateFetusRecord(userId)) {
                throw new MembershipFeatureException("Gói thành viên của bạn không cho phép tạo chỉ số thai nhi");
            }
    
            Fetus fetus = fetusRepository.findById(fetusId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thai nhi"));
    
            if (fetus.getStatus() == FetusStatus.COMPLETED || 
                fetus.getStatus() == FetusStatus.CANCEL) {
                throw new IllegalStateException("Không thể thêm chỉ số cho thai nhi đã hoàn thành hoặc đã hủy");
            }
    
            Pregnancy pregnancy = fetus.getPregnancy();
            if (pregnancy == null) {
                throw new RuntimeException("Thai nhi không có thông tin thai kỳ");
            }
    
            if (pregnancy.getStatus() == PregnancyStatus.COMPLETED || 
                pregnancy.getStatus() == PregnancyStatus.CANCEL) {
                throw new IllegalStateException("Không thể thêm chỉ số cho thai kỳ đã hoàn thành hoặc đã hủy");
            }
    
            int currentWeek = pregnancy.getGestationalWeeks();
            if (currentWeek <= 0) {
                throw new RuntimeException("Tuần thai không hợp lệ");
            }
    
            if (fetusRecordRepository.existsByFetusFetusIdAndWeek(fetusId, currentWeek)) {
                throw new IllegalArgumentException("Chỉ số cho tuần thai này đã tồn tại");
            }
    
            FetusRecord record = new FetusRecord();
            record.setFetus(fetus);
            record.setWeek(currentWeek);
            record.setCreatedAt(LocalDateTime.now());
            
            if (recordDTO.getFetalWeight() != null) {
                record.setFetalWeight(recordDTO.getFetalWeight());
            }
            if (recordDTO.getFemurLength() != null) {
                record.setFemurLength(recordDTO.getFemurLength());
            }
            if (recordDTO.getHeadCircumference() != null) {
                record.setHeadCircumference(recordDTO.getHeadCircumference());
            }
        
            FetusRecord savedRecord = fetusRecordRepository.save(record);
            
            try {
                checkFetusGrowth(savedRecord);
            } catch (HealthAlertException e) {
            }
            
            return savedRecord;
        }

        public Map<String, Object> getChartDataForDisplay(Long postId) {
            try {
                GrowthChartShare share = growthChartShareRepository.findByPostPostId(postId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy biểu đồ"));
                        
                Map<String, Object> response = new HashMap<>();
                @SuppressWarnings("unchecked")
                Map<String, Object> chartData = objectMapper.readValue(share.getChartData(), Map.class);
                response.put("chartData", chartData);
                response.put("sharedTypes", share.getSharedTypes());
                Map<String, Object> postInfo = new HashMap<>();
                postInfo.put("title", share.getPost().getTitle());
                postInfo.put("content", share.getPost().getContent());
                postInfo.put("authorId", share.getPost().getAuthor().getId());
                postInfo.put("createdAt", share.getPost().getCreatedAt());
                response.put("post", postInfo);
                
                return response;
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Lỗi khi xử lý dữ liệu biểu đồ", e);
            }
        }

        @Autowired
        private StandardService standardService;

        private Map<String, List<Object[]>> calculatePredictionLine(Long fetusId) {
            Map<String, List<Object[]>> predictionData = new HashMap<>();
            List<FetusRecord> records = fetusRecordRepository.findByFetusFetusIdOrderByWeekAsc(fetusId);
            
            if (records.isEmpty()) {
                return predictionData;
            }

            Integer fetusNumber = records.get(0).getFetus().getFetusIndex();
            Integer maxWeek = standardService.getMaxWeekByFetusNumber(fetusNumber);

            List<Object[]> weightPrediction = calculateMetricPrediction(records, 
                FetusRecord::getWeek, 
                FetusRecord::getFetalWeight,
                maxWeek);
            predictionData.put("weightPrediction", weightPrediction);

            List<Object[]> lengthPrediction = calculateMetricPrediction(records, 
                FetusRecord::getWeek, 
                FetusRecord::getFemurLength,
                maxWeek);
            predictionData.put("lengthPrediction", lengthPrediction);

            List<Object[]> headPrediction = calculateMetricPrediction(records, 
                FetusRecord::getWeek, 
                FetusRecord::getHeadCircumference,
                maxWeek);
            predictionData.put("headPrediction", headPrediction);

            return predictionData;
        }

        private List<Object[]> calculateMetricPrediction(List<FetusRecord> records,
                                                       Function<FetusRecord, Integer> weekExtractor,
                                                       Function<FetusRecord, BigDecimal> valueExtractor,
                                                       Integer maxWeek) {
            List<Object[]> prediction = new ArrayList<>();
            
            List<FetusRecord> validRecords = records.stream()
                .filter(r -> valueExtractor.apply(r) != null)
                .collect(Collectors.toList());

            if (validRecords.size() < 2) {
                return prediction;
            }

            double growthRate = calculateAverageGrowthRate(validRecords, weekExtractor, valueExtractor);
            
            FetusRecord lastRecord = validRecords.get(validRecords.size() - 1);
            int lastWeek = weekExtractor.apply(lastRecord);
            BigDecimal lastValue = valueExtractor.apply(lastRecord);

            for (int i = 1; i <= 4; i++) {
                int predictedWeek = lastWeek + i;
                if (predictedWeek <= maxWeek) {
                    BigDecimal predictedValue = lastValue.multiply(
                        BigDecimal.valueOf(Math.pow(1 + growthRate, i))
                    );
                    prediction.add(new Object[]{predictedWeek, predictedValue});
                }
            }
            return prediction;
        }

        private double calculateAverageGrowthRate(List<FetusRecord> records,
                                                 Function<FetusRecord, Integer> weekExtractor,
                                                 Function<FetusRecord, BigDecimal> valueExtractor) {
            double totalGrowthRate = 0;
            int count = 0;

            for (int i = 1; i < records.size(); i++) {
                BigDecimal currentValue = valueExtractor.apply(records.get(i));
                BigDecimal previousValue = valueExtractor.apply(records.get(i-1));
                int weekDiff = weekExtractor.apply(records.get(i)) - weekExtractor.apply(records.get(i-1));

                if (currentValue != null && previousValue != null && previousValue.compareTo(BigDecimal.ZERO) > 0) {
                    double growthRate = (currentValue.doubleValue() / previousValue.doubleValue() - 1) / weekDiff;
                    totalGrowthRate += growthRate;
                    count++;
                }
            }

            return count > 0 ? totalGrowthRate / count : 0;
        }

    public Map<ChartType, Boolean> getAvailableChartTypes(Long fetusId, Long userId) {
        Map<ChartType, Boolean> availableTypes = new HashMap<>();
        
        availableTypes.put(ChartType.WEIGHT, true);
        availableTypes.put(ChartType.LENGTH, true);
        availableTypes.put(ChartType.HEAD_CIRCUMFERENCE, true);
        availableTypes.put(ChartType.PREDICTION_LINE, membershipService.canViewPredictionLine(userId));
        
        return availableTypes;
    }
    @Transactional
    public FetusRecord getFetusRecord(Long fetusId, Long userId) {
        if (!membershipService.canViewFetusRecord(userId)) {
            throw new MembershipFeatureException("This feature requires Basic or Premium membership");
        }

        FetusRecord record = fetusRecordRepository.findById(fetusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chỉ số thai nhi"));
                
        Long recordUserId = record.getFetus().getPregnancy().getUser().getId();
        if (!recordUserId.equals(userId)) {
            throw new RuntimeException("Không có quyền truy cập chỉ số thai nhi này");
        }
        
        return record;
    }
    @Transactional
    public List<FetusRecordDTO> getRecordsByFetusId(Long fetusId, Long userId) {
        if (!membershipService.canViewFetusRecord(userId)) {
            throw new MembershipFeatureException("Gói thành viên của bạn không cho phép xem chỉ số thai nhi");
        }

        Fetus fetus = fetusRepository.findById(fetusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai nhi"));
                
        if (!fetus.getPregnancy().getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền truy cập chỉ số thai nhi này");
        }

        List<FetusRecord> records = fetusRecordRepository.findByFetusFetusIdOrderByWeekAsc(fetusId);
        return records.stream()
                .map(FetusRecordDTO::new)
                .collect(Collectors.toList());
    }

    public Map<String, List<Object[]>> getAllGrowthData(Long fetusId, Long userId) {
        if (!membershipService.canViewFetusRecord(userId)) {
            throw new MembershipFeatureException("Gói thành viên của bạn không cho phép xem chỉ số thai nhi");
        }

        Fetus fetus = fetusRepository.findById(fetusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai nhi"));
                
        if (!fetus.getPregnancy().getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền truy cập chỉ số thai nhi này");
        }

        Map<String, List<Object[]>> growthData = getAllGrowthData(fetusId);
        
        if (membershipService.canViewPredictionLine(userId)) {
            Map<String, List<Object[]>> predictionData = calculatePredictionLine(fetusId);
            growthData.putAll(predictionData);
        }
        
        return growthData;
    }@Transactional
    public void checkFetusGrowth(FetusRecord record) {
        Long userId = record.getFetus().getPregnancy().getUser().getId();
        if (!membershipService.canAccessHealthAlerts(userId)) {
            return;
        }
    
        Integer fetusNumber = record.getFetus().getFetusIndex();
        PregnancyStandardId standardId = new PregnancyStandardId(record.getWeek(), fetusNumber);
        Optional<PregnancyStandard> standardOpt = pregnancyStandardRepository.findById(standardId);
    
        if (standardOpt.isEmpty()) return;
    
        PregnancyStandard standard = standardOpt.get();
        SeverityLevel severity = null;
    
        if (record.getFetalWeight() != null) {
            severity = checkThreshold(record.getFetalWeight(), standard.getMinWeight(), standard.getMaxWeight(), severity);
        }
    
        if (record.getHeadCircumference() != null) {
            severity = checkThreshold(record.getHeadCircumference(), standard.getMinHeadCircumference(), standard.getMaxHeadCircumference(), severity);
        }
    
        if (record.getFemurLength() != null) {
            severity = checkThreshold(record.getFemurLength(), standard.getMinLength(), standard.getMaxLength(), severity);
        }
    
        if (severity != null) {
            Reminder reminder = new Reminder();
            reminder.setUser(record.getFetus().getPregnancy().getUser());
            reminder.setPregnancy(record.getFetus().getPregnancy());
            reminder.setType(ReminderType.HEALTH_ALERT);
            reminder.setReminderDate(LocalDate.now());
            reminder.setStatus(ReminderStatus.NOT_YET);
            
            Reminder savedReminder = reminderRepository.save(reminder);
            
            ReminderHealthAlert alert = new ReminderHealthAlert();
            alert.setReminder(savedReminder);
            
            if (record.getFetalWeight() != null) {
                BigDecimal weight = record.getFetalWeight();
                if (weight.compareTo(standard.getMinWeight()) < 0) {
                    alert.setHealthType(HealthType.LOW_WEIGHT);
                } else if (weight.compareTo(standard.getMaxWeight()) > 0) {
                    alert.setHealthType(HealthType.HIGH_WEIGHT);
                }
            }
            
            if (record.getHeadCircumference() != null) {
                BigDecimal circumference = record.getHeadCircumference();
                if (circumference.compareTo(standard.getMinHeadCircumference()) < 0) {
                    alert.setHealthType(HealthType.LOW_CIRCUMFERENCE);
                } else if (circumference.compareTo(standard.getMaxHeadCircumference()) > 0) {
                    alert.setHealthType(HealthType.HIGH_CIRCUMFERENCE);
                }
            }
            
            if (record.getFemurLength() != null) {
                BigDecimal length = record.getFemurLength();
                if (length.compareTo(standard.getMinLength()) < 0) {
                    alert.setHealthType(HealthType.LOW_HEIGHT);
                } else if (length.compareTo(standard.getMaxLength()) > 0) {
                    alert.setHealthType(HealthType.HIGH_HEIGHT);
                }
            }
            
            alert.setSeverity(severity);
            alert.setSource(AlertSource.PREGNANCY_RECORDS);
            alert.setNotes("Chỉ số tăng trưởng thai nhi nằm ngoài phạm vi bình thường.");
            ReminderHealthAlert savedAlert = reminderHealthAlertRepository.save(alert);
        
            notificationService.sendHealthAlertNotification(
                userId,
                "Cảnh báo chỉ số thai nhi",
                "Phát hiện chỉ số bất thường: " + savedAlert.getHealthType()
            );
        
            Fetus fetus = record.getFetus();
            fetus.setStatus(FetusStatus.ISSUE);
            fetusRepository.save(fetus);
        
            throw new HealthAlertException(
                "Phát hiện chỉ số bất thường",
                savedAlert.getHealthType(),
                savedAlert.getSeverity()
            );
        }
        else {
            Fetus fetus = record.getFetus();
            if (fetus.getStatus() == FetusStatus.ISSUE) {
                fetus.setStatus(FetusStatus.ACTIVE);
                fetusRepository.save(fetus);
            }
        }
    }
}

