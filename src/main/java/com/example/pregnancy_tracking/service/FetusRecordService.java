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
import java.time.LocalDateTime;
import java.time.LocalDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.pregnancy_tracking.exception.HealthAlertException;
import lombok.extern.slf4j.Slf4j;
import java.math.RoundingMode;


@Slf4j
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

    @Autowired
    private PregnancyRepository pregnancyRepository;

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

    public Map<String, List<Object[]>> getAllGrowthData(Long fetusId, Long userId) {
        if (!membershipService.canViewFetusRecord(userId)) {
            throw new MembershipFeatureException("Gói thành viên của bạn không cho phép xem chỉ số thai nhi");
        }

        Fetus fetus = fetusRepository.findById(fetusId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai nhi"));

        if (!fetus.getPregnancy().getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền truy cập chỉ số thai nhi này");
        }

        List<FetusRecord> records = fetusRecordRepository.findByFetusFetusIdOrderByWeekAsc(fetusId);
        Map<String, List<Object[]>> growthData = new HashMap<>();

        growthData.put("fetalWeight", records.stream()
                .filter(r -> r.getFetalWeight() != null)
                .map(r -> new Object[]{r.getWeek(), r.getFetalWeight()})
                .collect(Collectors.toList()));
        growthData.put("femurLength", records.stream()
                .filter(r -> r.getFemurLength() != null)
                .map(r -> new Object[]{r.getWeek(), r.getFemurLength()})
                .collect(Collectors.toList()));
        growthData.put("headCircumference", records.stream()
                .filter(r -> r.getHeadCircumference() != null)
                .map(r -> new Object[]{r.getWeek(), r.getHeadCircumference()})
                .collect(Collectors.toList()));

        return growthData;
    }

    @Transactional
    public void updateRecordsForPregnancy(Long pregnancyId, LocalDate newExamDate,
                                          LocalDate lastExamDate, int newWeeks, int oldWeeks) {
        Pregnancy pregnancy = pregnancyRepository.findById(pregnancyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thai kỳ"));
    
        int weekDifference = newWeeks - oldWeeks;
        
        if (weekDifference < 0) {
            throw new IllegalStateException(
                "Không thể cập nhật: Tuần thai mới (" + newWeeks + 
                ") nhỏ hơn tuần thai hiện tại (" + oldWeeks + ")");
        }
    
        // Cập nhật ngày tháng của thai kỳ
        LocalDate newStartDate = newExamDate.minusWeeks(newWeeks);
        LocalDate newDueDate = newStartDate.plusWeeks(40);
        pregnancy.setStartDate(newStartDate);
        pregnancy.setDueDate(newDueDate);
        pregnancy.setExamDate(newExamDate);
        pregnancy.setLastExamDate(lastExamDate);
        pregnancy.setGestationalWeeks(newWeeks);
        pregnancy.setLastUpdatedAt(LocalDateTime.now());
        pregnancyRepository.save(pregnancy);
    
        // Lấy và cập nhật records
        List<FetusRecord> records = fetusRecordRepository
            .findByFetusPregnancyPregnancyId(pregnancyId);
    
        if (!records.isEmpty()) {
            // Sắp xếp records theo tuần giảm dần
            records.sort((r1, r2) -> r2.getWeek().compareTo(r1.getWeek()));
            
            // Tính toán khoảng cách an toàn để tránh trùng với tuần mới
            int safeDistance = 1; // Luôn giữ khoảng cách ít nhất 1 tuần
            int currentWeek = records.get(0).getWeek(); // Tuần cao nhất hiện tại
            
            // Chỉ cập nhật nếu có sự thay đổi và đảm bảo không trùng với tuần mới
            if (weekDifference > 0) {
                for (FetusRecord record : records) {
                    int proposedWeek = record.getWeek() + weekDifference;
                    // Đảm bảo tuần mới không vượt quá tuần hiện tại và cách tuần mới ít nhất 1 tuần
                    int adjustedWeek = Math.min(proposedWeek, newWeeks - safeDistance);
                    record.setWeek(adjustedWeek);
                    fetusRecordRepository.save(record);
                }
            }
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
            log.info("Health alert created for user {} with type: {}", userId, e.getHealthType());
            throw e;
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

        // Lấy record cuối cùng để dự đoán
        FetusRecord lastRecord = validRecords.get(validRecords.size() - 1);
        int lastWeek = weekExtractor.apply(lastRecord);
        Integer fetusNumber = records.get(0).getFetus().getFetusIndex();
        BigDecimal lastValue = valueExtractor.apply(lastRecord);

        // Dự đoán 4 tuần tiếp theo
        for (int i = 1; i <= 4; i++) {
            int predictedWeek = lastWeek + i;
            if (predictedWeek <= maxWeek) {
                // Lấy standard data cho tuần hiện tại và tuần dự đoán
                PregnancyStandardId currentStandardId = new PregnancyStandardId(lastWeek + i - 1, fetusNumber);
                PregnancyStandardId predictedStandardId = new PregnancyStandardId(predictedWeek, fetusNumber);
                
                Optional<PregnancyStandard> currentStandardOpt = pregnancyStandardRepository.findById(currentStandardId);
                Optional<PregnancyStandard> predictedStandardOpt = pregnancyStandardRepository.findById(predictedStandardId);
                
                if (currentStandardOpt.isPresent() && predictedStandardOpt.isPresent()) {
                    // Lấy giá trị trung bình của standard cho cả 2 tuần
                    BigDecimal currentAvg = getStandardValueForWeek(lastWeek + i - 1, fetusNumber, valueExtractor);
                    BigDecimal predictedAvg = getStandardValueForWeek(predictedWeek, fetusNumber, valueExtractor);
                    
                    // Tính tỷ lệ tăng trưởng theo standard
                    double standardGrowthRate = predictedAvg.doubleValue() / currentAvg.doubleValue();
                    
                    // Tính giá trị dự đoán dựa trên tỷ lệ tăng trưởng của standard
                    double predictedValue = lastValue.doubleValue() * standardGrowthRate;
                    
                    // Giới hạn giá trị dự đoán trong khoảng min-max của standard
                    BigDecimal minLimit = getMinStandardValue(predictedStandardOpt.get(), valueExtractor);
                    BigDecimal maxLimit = getMaxStandardValue(predictedStandardOpt.get(), valueExtractor);
                    predictedValue = Math.max(minLimit.doubleValue(), 
                        Math.min(maxLimit.doubleValue(), predictedValue));
                    
                    prediction.add(new Object[]{predictedWeek, BigDecimal.valueOf(predictedValue)
                        .setScale(2, RoundingMode.HALF_UP)});
                    
                    // Cập nhật lastValue cho lần dự đoán tiếp theo
                    lastValue = BigDecimal.valueOf(predictedValue);
                }
            }
        }
        return prediction;
    }

    private BigDecimal getStandardValueForWeek(int week, Integer fetusNumber,
                                     Function<FetusRecord, BigDecimal> valueExtractor) {
        PregnancyStandardId standardId = new PregnancyStandardId(week, fetusNumber);
        PregnancyStandard standard = pregnancyStandardRepository.findById(standardId)
                .orElseThrow(() -> new RuntimeException("Standard not found for week: " + week));
        return getStandardValue(standard, valueExtractor, StandardValueType.AVERAGE);
    }

    private BigDecimal getMinStandardValue(PregnancyStandard standard,
                                     Function<FetusRecord, BigDecimal> valueExtractor) {
        return getStandardValue(standard, valueExtractor, StandardValueType.MIN);
    }

    private BigDecimal getMaxStandardValue(PregnancyStandard standard,
                                     Function<FetusRecord, BigDecimal> valueExtractor) {
        return getStandardValue(standard, valueExtractor, StandardValueType.MAX);
    }

    private enum StandardValueType {
        MIN, MAX, AVERAGE
    }

    private enum MetricType {
        WEIGHT, LENGTH, HEAD_CIRCUMFERENCE
    }

    private interface MetricExtractor {
        BigDecimal getMin(PregnancyStandard standard);
        BigDecimal getMax(PregnancyStandard standard);
        BigDecimal getAvg(PregnancyStandard standard);
    }

    private final Map<MetricType, MetricExtractor> metricExtractors = Map.of(
        MetricType.WEIGHT, new MetricExtractor() {
            public BigDecimal getMin(PregnancyStandard s) { return s.getMinWeight(); }
            public BigDecimal getMax(PregnancyStandard s) { return s.getMaxWeight(); }
            public BigDecimal getAvg(PregnancyStandard s) { return s.getAvgWeight(); }
        },
        MetricType.LENGTH, new MetricExtractor() {
            public BigDecimal getMin(PregnancyStandard s) { return s.getMinLength(); }
            public BigDecimal getMax(PregnancyStandard s) { return s.getMaxLength(); }
            public BigDecimal getAvg(PregnancyStandard s) { return s.getAvgLength(); }
        },
        MetricType.HEAD_CIRCUMFERENCE, new MetricExtractor() {
            public BigDecimal getMin(PregnancyStandard s) { return s.getMinHeadCircumference(); }
            public BigDecimal getMax(PregnancyStandard s) { return s.getMaxHeadCircumference(); }
            public BigDecimal getAvg(PregnancyStandard s) { return s.getAvgHeadCircumference(); }
        }
    );

    private MetricType getMetricType(Function<FetusRecord, BigDecimal> valueExtractor) {
        // Tạo một record test để xác định loại metric
        FetusRecord testRecord = new FetusRecord();
        testRecord.setFetalWeight(BigDecimal.ONE);
        testRecord.setFemurLength(BigDecimal.TEN);
        testRecord.setHeadCircumference(BigDecimal.ZERO);
        
        BigDecimal extractedValue = valueExtractor.apply(testRecord);
        
        if (extractedValue.equals(BigDecimal.ONE)) {
            return MetricType.WEIGHT;
        } else if (extractedValue.equals(BigDecimal.TEN)) {
            return MetricType.LENGTH;
        } else if (extractedValue.equals(BigDecimal.ZERO)) {
            return MetricType.HEAD_CIRCUMFERENCE;
        }
        
        throw new IllegalArgumentException("Unknown metric type");
    }

    private BigDecimal getStandardValue(PregnancyStandard standard,
                                      Function<FetusRecord, BigDecimal> valueExtractor,
                                      StandardValueType type) {
        MetricType metricType = getMetricType(valueExtractor);
        MetricExtractor extractor = metricExtractors.get(metricType);
        
        return switch (type) {
            case MIN -> extractor.getMin(standard);
            case MAX -> extractor.getMax(standard);
            case AVERAGE -> extractor.getAvg(standard);
        };
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

    public Map<String, List<Object[]>> getPredictionData(Long fetusId, Long userId) {
        if (!membershipService.canViewFetusRecord(userId)) {
            throw new MembershipFeatureException("Gói thành viên của bạn không cho phép xem chỉ số thai nhi");
        }

        if (!membershipService.canViewPredictionLine(userId)) {
            log.info("User {} does not have permission to view prediction line", userId);
            return new HashMap<>();
        }

        Map<String, List<Object[]>> result = calculatePredictionLine(fetusId);
        log.info("Prediction data for fetus {}: {}", fetusId, result);
        
        // Check if data is empty
        if (result.isEmpty()) {
            log.warn("No prediction data generated for fetus {}", fetusId);
        } else {
            // Log each prediction type
            result.forEach((key, value) -> {
                log.info("{} prediction size: {}", key, value.size());
                if (!value.isEmpty()) {
                    log.info("First prediction point: {}", Arrays.toString(value.get(0)));
                }
            });
        }
        
        return result;
    }

    @Transactional
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
        List<String> healthAlerts = new ArrayList<>();

        if (record.getFetalWeight() != null) {
            BigDecimal weight = record.getFetalWeight();
            severity = checkThreshold(weight, standard.getMinWeight(), standard.getMaxWeight(), severity);
            if (weight.compareTo(standard.getMinWeight()) < 0) {
                healthAlerts.add("Cân nặng thấp hơn mức bình thường");
            } else if (weight.compareTo(standard.getMaxWeight()) > 0) {
                healthAlerts.add("Cân nặng cao hơn mức bình thường");
            }
        }

        if (record.getHeadCircumference() != null) {
            BigDecimal circumference = record.getHeadCircumference();
            severity = checkThreshold(circumference, standard.getMinHeadCircumference(), standard.getMaxHeadCircumference(), severity);
            if (circumference.compareTo(standard.getMinHeadCircumference()) < 0) {
                healthAlerts.add("Chu vi đầu thấp hơn mức bình thường");
            } else if (circumference.compareTo(standard.getMaxHeadCircumference()) > 0) {
                healthAlerts.add("Chu vi đầu cao hơn mức bình thường");
            }
        }

        if (record.getFemurLength() != null) {
            BigDecimal length = record.getFemurLength();
            severity = checkThreshold(length, standard.getMinLength(), standard.getMaxLength(), severity);
            if (length.compareTo(standard.getMinLength()) < 0) {
                healthAlerts.add("Chiều dài xương đùi thấp hơn mức bình thường");
            } else if (length.compareTo(standard.getMaxLength()) > 0) {
                healthAlerts.add("Chiều dài xương đùi cao hơn mức bình thường");
            }
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
            alert.setNotes(String.join(", ", healthAlerts));
            ReminderHealthAlert savedAlert = reminderHealthAlertRepository.save(alert);

            if (!healthAlerts.isEmpty()) {
                notificationService.sendBatchHealthAlertNotifications(userId, healthAlerts);
            }

            Fetus fetus = record.getFetus();
            fetus.setStatus(FetusStatus.ISSUE);
            fetusRepository.save(fetus);

        } else {
            Fetus fetus = record.getFetus();
            if (fetus.getStatus() == FetusStatus.ISSUE) {
                fetus.setStatus(FetusStatus.ACTIVE);
                fetusRepository.save(fetus);
            }
        }
    }
}

