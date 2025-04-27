package lms.payment_service_lms.service;

import lms.payment_service_lms.entity.Order;

public interface EmailSenderService {
    public void sendEmail(Order order, String subject);
}
