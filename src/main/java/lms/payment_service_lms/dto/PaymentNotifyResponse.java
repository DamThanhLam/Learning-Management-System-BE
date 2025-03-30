package lms.payment_service_lms.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentNotifyResponse {
    String resultCode; // Kết quả thanh toán (0 - thành công, khác 0 - thất bại)
    String transactionId; // Mã giao dịch
    String message; // Thông báo lỗi (nếu có)
}
