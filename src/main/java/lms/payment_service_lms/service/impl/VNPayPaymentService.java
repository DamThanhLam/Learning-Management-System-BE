package lms.payment_service_lms.service.impl;

import lms.payment_service_lms.dto.PaymentRequest;
import lms.payment_service_lms.dto.PaymentResponse;
import lms.payment_service_lms.exception.PaymentException;
import lms.payment_service_lms.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VNPayPaymentService implements PaymentService {

    @Value("${vnpay.api.key}")
    private String vnpayApiKey;  // API key của VNPay

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) throws PaymentException {
        try {
            // Gửi request đến VNPay API để tạo giao dịch thanh toán
            // Ví dụ giả lập trả về thành công
            return new PaymentResponse("vnpay-transaction-id", "Success", "Payment processed via VNPay");
        } catch (Exception e) {
            throw new PaymentException("Error processing VNPay payment", e);
        }
    }
}

