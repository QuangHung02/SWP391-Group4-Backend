package com.example.pregnancy_tracking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class CustomErrorController implements ErrorController {
    @Operation(summary = "Xử lý lỗi API", description = "Trả về thông báo lỗi chi tiết cho các mã trạng thái HTTP khác nhau")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "Không tìm thấy tài nguyên"),
            @ApiResponse(responseCode = "403", description = "Không có quyền truy cập"),
            @ApiResponse(responseCode = "401", description = "Chưa xác thực"),
            @ApiResponse(responseCode = "500", description = "Lỗi máy chủ")
    })
    @RequestMapping
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Map<String, Object> errorDetails = new HashMap<>();

        HttpStatus status = getStatus(request);
        String message = getErrorMessage(status);
        errorDetails.put("timestamp", System.currentTimeMillis());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", message);
        errorDetails.put("path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
        return ResponseEntity.status(status).body(errorDetails);
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(statusCode);
        } catch (Exception ex) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private String getErrorMessage(HttpStatus status) {
        switch (status) {
            case NOT_FOUND:
                return "Không tìm thấy tài nguyên yêu cầu";
            case FORBIDDEN:
                return "Bạn không có quyền truy cập tài nguyên này";
            case UNAUTHORIZED:
                return "Vui lòng đăng nhập để truy cập tài nguyên này";
            default:
                return "Đã xảy ra lỗi không mong muốn";
        }
    }
}
