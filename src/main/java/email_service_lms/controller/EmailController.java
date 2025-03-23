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
    @PostMapping("/payment-success")
    public ResponseEntity<String> sendAccountRequest(@RequestParam String email, @RequestParam String name) {
        Locale locale = LocaleContextHolder.getLocale(); // Lấy ngôn ngữ hiện tại

        // Lấy tiêu đề email từ messages.properties
        String subject = messageSource.getMessage("email.preheader", null, locale);

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", name);
        templateData.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))); // Format theo chuẩn tiếng Anh
        EmailMessage emailMessage = new EmailMessage(
                email,
                subject, // Dùng tiêu đề lấy từ messages.properties
                "payment-success",
                templateData
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, emailMessage);
        return ResponseEntity.ok("The email request for account creation has been added to the queue.");
    }

    // 2. Endpoint gửi email chấp nhận/từ chối tạo tài khoản giáo viên
    @PostMapping("/account-request")
    public ResponseEntity<String> sendAccountRequest(@RequestParam String email, @RequestParam String name, @RequestParam boolean approved) {
        Locale locale = LocaleContextHolder.getLocale(); // Lấy ngôn ngữ hiện tại

        // Lấy tiêu đề email từ messages.properties
        String subject = messageSource.getMessage("email.preheader", null, locale);

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", name);
        templateData.put("approved", approved);
        templateData.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))); // Format theo chuẩn tiếng Anh
        EmailMessage emailMessage = new EmailMessage(
                email,
                subject, // Dùng tiêu đề lấy từ messages.properties
                "account-request",
                templateData
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, emailMessage);
        return ResponseEntity.ok("The email request for account creation has been added to the queue.");
    }

    // 3. Endpoint gửi email thông báo lý do khóa tài khoản
    @PostMapping("/lock-account")
    public ResponseEntity<String> sendLockAccountRequest(@RequestParam String email, @RequestParam String name) {
        Locale locale = LocaleContextHolder.getLocale(); // Lấy ngôn ngữ hiện tại

        // Lấy tiêu đề email từ messages.properties
        String subject = messageSource.getMessage("email.preheader", null, locale);

        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", name);
        templateData.put("date", LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))); // Format theo chuẩn tiếng Anh
        EmailMessage emailMessage = new EmailMessage(
                email,
                subject, // Dùng tiêu đề lấy từ messages.properties
                "account-lock",
                templateData
        );

        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_QUEUE, emailMessage);
        return ResponseEntity.ok("The email request for account creation has been added to the queue.");
    }
}
