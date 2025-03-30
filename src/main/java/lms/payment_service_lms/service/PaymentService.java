package lms.payment_service_lms.service;

import lms.payment_service_lms.dto.PaymentRequest;
import lms.payment_service_lms.dto.PaymentResponse;
import lms.payment_service_lms.exception.PaymentException;

public interface PaymentService {
    PaymentResponse processPayment(PaymentRequest paymentRequest) throws PaymentException;
}
