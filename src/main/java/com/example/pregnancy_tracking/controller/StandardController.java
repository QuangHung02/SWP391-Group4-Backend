package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.StandardMedicalTask;
import com.example.pregnancy_tracking.service.StandardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.pregnancy_tracking.dto.PregnancyStandardDTO;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/standards")
@SecurityRequirement(name = "Bearer Authentication")
public class StandardController {
    private final StandardService standardService;

    public StandardController(StandardService standardService) {
        this.standardService = standardService;
    }

    @Operation(summary = "Lấy tất cả nhiệm vụ y tế tiêu chuẩn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách nhiệm vụ thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/medical-tasks")
    public ResponseEntity<List<StandardMedicalTask>> getAllStandardMedicalTasks() {
        return ResponseEntity.ok(standardService.getAllStandardMedicalTasks());
    }

    @Operation(summary = "Lấy nhiệm vụ y tế tiêu chuẩn theo tuần")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy nhiệm vụ theo tuần thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhiệm vụ cho tuần này"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/medical-tasks/week/{week}")
    public ResponseEntity<List<StandardMedicalTask>> getStandardMedicalTasksByWeek(@PathVariable Integer week) {
        List<StandardMedicalTask> tasks = standardService.getStandardMedicalTasksByWeek(week);
        if (tasks.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @Operation(summary = "Tạo nhiệm vụ y tế tiêu chuẩn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tạo nhiệm vụ thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu nhiệm vụ không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/medical-tasks")
    public ResponseEntity<StandardMedicalTask> createStandardMedicalTask(@RequestBody StandardMedicalTask task) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(standardService.createStandardMedicalTask(task));
    }

    @Operation(summary = "Xóa nhiệm vụ y tế tiêu chuẩn")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Xóa nhiệm vụ thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhiệm vụ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @DeleteMapping("/medical-tasks/{taskId}")
    public ResponseEntity<Void> deleteStandardMedicalTask(@PathVariable Long taskId) {
        try {
            standardService.deleteStandardMedicalTask(taskId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Tạo nhiệm vụ hàng tuần cho người dùng")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo nhiệm vụ thành công"),
            @ApiResponse(responseCode = "400", description = "Tham số yêu cầu không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng hoặc thai kỳ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/generate-tasks")
    public ResponseEntity<Void> generateWeeklyTasks(
            @RequestParam Long userId,
            @RequestParam Long pregnancyId,
            @RequestParam Integer currentWeek) {
        try {
            standardService.checkAndCreateWeeklyTasks(userId, pregnancyId, currentWeek);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Lấy tất cả tiêu chuẩn thai kỳ", 
              description = "Lấy giá trị tiêu chuẩn cho tất cả các tuần thai kỳ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy tiêu chuẩn thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tiêu chuẩn"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/pregnancy/all")
    public ResponseEntity<List<PregnancyStandardDTO>> getAllPregnancyStandards() {
        return ResponseEntity.ok(standardService.getAllPregnancyStandards());
    }

    @Operation(summary = "Lấy tiêu chuẩn thai kỳ theo số thai", 
              description = "Lấy tất cả giá trị tiêu chuẩn cho một số thai cụ thể")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy tiêu chuẩn thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tiêu chuẩn"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/pregnancy/fetus/{fetusNumber}")
    public ResponseEntity<List<PregnancyStandardDTO>> getPregnancyStandardsByFetusNumber(
            @PathVariable Integer fetusNumber) {
        List<PregnancyStandardDTO> standards = standardService.getPregnancyStandardsByFetusNumber(fetusNumber);
        if (standards.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(standards);
    }
    @Operation(summary = "Lấy tiêu chuẩn thai kỳ theo tuần và số thai", 
              description = "Lấy giá trị tiêu chuẩn cho một tuần và số thai cụ thể")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy tiêu chuẩn thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tiêu chuẩn cho tuần và số thai này"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/pregnancy")
    public ResponseEntity<PregnancyStandardDTO> getPregnancyStandard(
            @RequestParam Integer week,
            @RequestParam Integer fetusNumber) {
        return standardService.getPregnancyStandard(week, fetusNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Lấy tiêu chuẩn thai kỳ và đường dự đoán", 
              description = "Lấy giá trị tiêu chuẩn và đường dự đoán tăng trưởng cho biểu đồ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy dữ liệu thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy dữ liệu cho số thai này"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/pregnancy/fetus/{fetusNumber}/with-prediction")
    public ResponseEntity<Map<String, Object>> getPregnancyStandardsWithPrediction(
            @PathVariable Integer fetusNumber,
            @RequestParam Integer currentWeek) {
        Map<String, Object> result = standardService.getStandardsWithPrediction(fetusNumber, currentWeek);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}