package lms.payment_service_lms.service;

import lms.payment_service_lms.dto.PaymentRequest;
import lms.payment_service_lms.dto.PaymentResponse;
import lms.payment_service_lms.exception.PaymentException;
import lms.payment_service_lms.service.impl.BankPaymentService;
import lms.payment_service_lms.service.impl.MomoPaymentService;
import lms.payment_service_lms.service.impl.VNPayPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentFacade {

    private final Map<String, PaymentService> paymentServices = new HashMap<>();

    @Autowired
    public PaymentFacade(MomoPaymentService momoPaymentService,
                         VNPayPaymentService vnPayPaymentService,
                         BankPaymentService bankPaymentService) {
        paymentServices.put("momo", momoPaymentService);
        paymentServices.put("vnpay", vnPayPaymentService);
        paymentServices.put("bank", bankPaymentService);
    }

    public PaymentResponse processPayment(PaymentRequest paymentRequest) throws  PaymentException {
        PaymentService paymentService = paymentServices.get(paymentRequest.getPaymentMethod().toLowerCase());
        if (paymentService != null) {
            return paymentService.processPayment(paymentRequest);
        }
        throw new PaymentException("Unsupported payment method: " + paymentRequest.getPaymentMethod());
    }
}
