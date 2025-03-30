package lms.payment_service_lms.controller;

import lms.payment_service_lms.dto.PaymentRequest;
import lms.payment_service_lms.dto.PaymentResponse;
import lms.payment_service_lms.exception.PaymentException;
import lms.payment_service_lms.service.PaymentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentFacade paymentFacade;

    @PostMapping("/process-payment")
    public PaymentResponse processPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            return paymentFacade.processPayment(paymentRequest);
        } catch (PaymentException e) {
            return new PaymentResponse(null, "Failure", e.getMessage());
        }
    }
}
