package lms.payment_service_lms.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentResponse {
    String paymentId;  // ID thanh toán trả về từ dịch vụ
    String status;  // Trạng thái thanh toán (Success, Failure)
    String message;  // Thông báo chi tiết


}