package lms.payment_service_lms.service.impl;

import lms.payment_service_lms.dto.PaymentRequest;
import lms.payment_service_lms.dto.PaymentResponse;
import lms.payment_service_lms.exception.PaymentException;
import lms.payment_service_lms.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BankPaymentService implements PaymentService {

    @Value("${bank.api.key}")
    private String bankApiKey;  // API key của ngân hàng

    @Override
    public PaymentResponse processPayment(PaymentRequest paymentRequest) throws PaymentException {
        try {
            // Gửi request đến API ngân hàng để thực hiện giao dịch thanh toán
            // Ví dụ giả lập trả về thành công
            return new PaymentResponse("bank-transaction-id", "Success", "Payment processed via Bank");
        } catch (Exception e) {
            throw new PaymentException("Error processing Bank payment", e);
        }
    }
}
