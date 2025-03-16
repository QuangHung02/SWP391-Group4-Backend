package com.example.pregnancy_tracking.service;

import com.example.pregnancy_tracking.config.VNPayConfig;
import com.example.pregnancy_tracking.entity.MembershipPackage;
import com.example.pregnancy_tracking.entity.User;
import com.example.pregnancy_tracking.repository.MembershipPackageRepository;
import com.example.pregnancy_tracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final UserRepository userRepository;
    private final MembershipPackageRepository packageRepository;
    private final MembershipService membershipService;

    public String createPaymentUrl(Long userId, Long packageId, String returnUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        MembershipPackage membershipPackage = packageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Package not found"));

        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";
        
        // Tạo thời gian giao dịch
        // Sửa lại format thời gian cho đúng với yêu cầu của VNPay
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        
        // Tính toán số tiền
        long amount = membershipPackage.getPrice().multiply(new BigDecimal("100")).longValue();
        
        Map<String, String> vnp_Params = new LinkedHashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderInfo", "Thanh toan goi " + membershipPackage.getName() + "_" + userId);
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);

        // Tạo URL thanh toán không có hash
        StringBuilder queryUrl = new StringBuilder();
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                if (queryUrl.length() > 0) {
                    queryUrl.append('&');
                }
                queryUrl.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
            }
        }
        
        // Tạo chuỗi hash và chữ ký
        String hashData = queryUrl.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.vnp_HashSecret, hashData);
        
        // Thêm chữ ký vào URL
        queryUrl.append("&vnp_SecureHash=").append(vnp_SecureHash);
        
        return VNPayConfig.vnp_PayUrl + "?" + queryUrl.toString();
    }

    public void processPaymentReturn(Map<String, String> vnpParams) {
        String vnp_ResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnp_TxnRef = vnpParams.get("vnp_TxnRef");
        
        if ("00".equals(vnp_ResponseCode)) {
            Long userId = Long.parseLong(vnpParams.get("vnp_OrderInfo").split("_")[1]);
            membershipService.upgradeToPremium(userId);
        } else {
            throw new RuntimeException("Payment failed with code: " + vnp_ResponseCode);
        }
    }
}