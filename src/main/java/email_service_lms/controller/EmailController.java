package email_service_lms.controller;

import email_service_lms.config.RabbitMQConfig;
import email_service_lms.model.EmailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // them thong tin cua order vao day
    @PostMapping("/payment")
    public ResponseEntity<String> sendAccountRequest(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String orderId) {

        // Xu li thanh toan


        // gui email

        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("email.preheader", null, locale);

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", name);
        templateData.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        templateData.put("orderDetails", " ");

        // Tạo đối tượng EmailMessage
        EmailMessage emailMessage = new EmailMessage(
                email,
                subject,
                "payment-success",
                templateData
        );

        // Gửi thông điệp vào RabbitMQ Exchange với Routing Key 'payment_success_email'
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.PAYMENT_SUCCESS_ROUTING_KEY, emailMessage);
        return ResponseEntity.ok(String.format(
                "Email request has been successfully added to the queue for user '%s'. " +
                        "The email will be sent from the system to '%s' with subject '%s'. " +
                        "The request has been added to the '%s' queue at %s.",
                name, email, subject, RabbitMQConfig.PAYMENT_SUCCESS_QUEUE, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
    }


}
