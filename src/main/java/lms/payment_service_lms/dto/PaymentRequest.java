package lms.payment_service_lms.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentRequest {
    String paymentMethod;  // Momo, VNPay, Bank, etc.
    Long amount;  // Số tiền thanh toán
    String currency;  // Đơn vị tiền tệ
    String customerId;  // Mã khách hàng
    String paymentDetails;  // Chi tiết thanh toán (mã thẻ Visa, email Momo, mã giao dịch VNPay, v.v.)
}