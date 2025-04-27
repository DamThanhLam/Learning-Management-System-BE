package lms.payment_service_lms.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    String userId;  // Momo, VNPay, Bank, etc.
    List<String> courseIds; // Số tiền thanh toán
    String orderInfo;  // Đơn vị tiền tệ
}