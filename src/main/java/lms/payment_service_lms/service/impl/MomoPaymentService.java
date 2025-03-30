package lms.payment_service_lms.service.impl;

import lms.payment_service_lms.dto.PaymentRequest;
import lms.payment_service_lms.dto.PaymentResponse;
import lms.payment_service_lms.exception.PaymentException;
import lms.payment_service_lms.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MomoPaymentService implements PaymentService {

    @Value("${momo.api.key}")
    private String momoApiKey;  // API key của Momo

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) throws PaymentException {
        try {
            // Giả lập gọi API của Momo (cần dùng thư viện thực tế của Momo hoặc API endpoint của họ)
            // Chẳng hạn, bạn sẽ gửi một request thanh toán tới Momo API và nhận phản hồi

            // Đây là ví dụ giả lập trả về thành công
            return new PaymentResponse("momo-transaction-id", "Success", "Payment processed via Momo");
        } catch (Exception e) {
            throw new PaymentException("Error processing Momo payment", e);
        }
    }
}
