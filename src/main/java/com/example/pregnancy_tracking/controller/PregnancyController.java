package com.example.pregnancy_tracking.controller;

import com.example.pregnancy_tracking.dto.PregnancyDTO;
import com.example.pregnancy_tracking.entity.FetusStatus;
import com.example.pregnancy_tracking.entity.PregnancyStatus;
import com.example.pregnancy_tracking.service.PregnancyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.pregnancy_tracking.dto.PregnancyListDTO;
import java.util.List;

@RestController
@RequestMapping("/api/pregnancies")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class PregnancyController {
    @Autowired
    private PregnancyService pregnancyService;

    @Operation(summary = "Tạo thai kỳ mới", description = "Tạo bản ghi thai kỳ mới và trả về đối tượng đã tạo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tạo thai kỳ thành công"),
            @ApiResponse(responseCode = "400", description = "Dữ liệu không hợp lệ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PostMapping("")
    public ResponseEntity<PregnancyListDTO> createPregnancy(@Valid @RequestBody PregnancyDTO pregnancyDTO) {
        PregnancyListDTO createdPregnancy = pregnancyService.createPregnancy(pregnancyDTO);
        return ResponseEntity.ok(createdPregnancy);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PregnancyListDTO> updatePregnancy(@PathVariable Long id,
                                                   @Valid @RequestBody PregnancyDTO pregnancyDTO) {
        PregnancyListDTO updatedPregnancy = pregnancyService.updatePregnancy(id, pregnancyDTO);
        return ResponseEntity.ok(updatedPregnancy);
    }

    @Operation(summary = "Lấy thai kỳ theo ID", description = "Lấy thông tin thai kỳ theo ID duy nhất.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thông tin thai kỳ thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thai kỳ"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PregnancyListDTO> getPregnancyById(@PathVariable Long id) {
        PregnancyListDTO pregnancy = pregnancyService.getPregnancyById(id);
        return ResponseEntity.ok(pregnancy);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updatePregnancyStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            PregnancyStatus pregnancyStatus = PregnancyStatus.valueOf(status.toUpperCase());
            pregnancyService.updatePregnancyStatus(id, pregnancyStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Lấy danh sách thai kỳ theo ID người dùng", 
              description = "Lấy tất cả thai kỳ của một người dùng cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thai kỳ thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PregnancyListDTO>> getPregnanciesByUserId(@PathVariable Long userId) {
        List<PregnancyListDTO> pregnancies = pregnancyService.getPregnanciesByUserId(userId);
        return ResponseEntity.ok(pregnancies);
    }

    @Operation(summary = "Lấy thai kỳ đang theo dõi theo ID người dùng",
            description = "Lấy thông tin chi tiết của thai kỳ đang theo dõi của người dùng, bao gồm dữ liệu thai nhi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy thai kỳ đang theo dõi thành công"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thai kỳ đang theo dõi"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @GetMapping("/ongoing/{userId}")
    public ResponseEntity<PregnancyListDTO> getOngoingPregnancyByUserId(@PathVariable Long userId) {
        PregnancyListDTO pregnancy = pregnancyService.getOngoingPregnancyByUserId(userId);
        return ResponseEntity.ok(pregnancy);
    }

    @Operation(summary = "Cập nhật trạng thái thai nhi", 
              description = "Cập nhật trạng thái của một thai nhi cụ thể.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cập nhật trạng thái thai nhi thành công"),
            @ApiResponse(responseCode = "400", description = "Giá trị trạng thái không hợp lệ"),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy thai nhi"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @PatchMapping("/fetus/{fetusId}/status")
    public ResponseEntity<Void> updateFetusStatus(
            @PathVariable Long fetusId,
            @RequestParam String status) {
        try {
            if (status == null || status.trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            FetusStatus fetusStatus = FetusStatus.valueOf(status.toUpperCase());
            pregnancyService.updateFetusStatus(fetusId, fetusStatus);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


}
