package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.entity.MotherRecord;
import com.example.pregnancy_tracking.service.MotherRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mother-records")
@SecurityRequirement(name = "Bearer Authentication")
public class MotherRecordController {

    @Autowired
    private MotherRecordService motherRecordService;

    @Operation(summary = "Tạo bản ghi theo dõi mẹ", description = "Tạo bản ghi theo dõi mới cho mẹ liên kết với một thai kỳ.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo bản ghi thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("/{pregnancyId}")
    public ResponseEntity<MotherRecord> createRecord(@PathVariable Long pregnancyId,
                                                     @RequestBody MotherRecord motherRecord) {
        MotherRecord createdRecord = motherRecordService.createRecord(pregnancyId, motherRecord);
        return ResponseEntity.ok(createdRecord);
    }

    @Operation(summary = "Lấy bản ghi theo dõi mẹ theo ID thai kỳ", 
              description = "Lấy tất cả bản ghi theo dõi mẹ liên quan đến một thai kỳ cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách bản ghi thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy bản ghi"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/{pregnancyId}")
    public ResponseEntity<List<MotherRecord>> getRecordsByPregnancyId(@PathVariable Long pregnancyId) {
        List<MotherRecord> records = motherRecordService.getRecordsByPregnancyId(pregnancyId);
        return ResponseEntity.ok(records);
    }
}
