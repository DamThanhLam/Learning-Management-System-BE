package email_service_lms.controller;

import email_service_lms.config.RabbitMQConfig;
import email_service_lms.dto.AccountLockDTO;
import email_service_lms.dto.OrderDetailDTO;
import email_service_lms.model.EmailMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/v1/email")
public class EmailController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // them thong tin cua order vao day
    @PostMapping("/payment")
    public ResponseEntity<String> sendAccountRequest(@RequestBody OrderDetailDTO orderDetail) {

        // Xu li thanh toan


        // gui email
        Locale locale = LocaleContextHolder.getLocale();
        String subject = String.format("[%s] [%s] %s [%s]", messageSource.getMessage("company.name", null, locale), new SimpleDateFormat("dd/MM/yyyy").format(new Date()), messageSource.getMessage("email.preheader", null, locale), orderDetail.getOrderId());
        EmailMessage emailMessage = getEmailMessageForPayment(orderDetail, subject);

        // Gửi thông điệp vào RabbitMQ Exchange với Routing Key 'payment_success_email'
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.PAYMENT_SUCCESS_ROUTING_KEY, emailMessage, message -> {
            message.getMessageProperties().setContentType("application/x-java-serialized-object");
            return message;
        });

        return ResponseEntity.ok(String.format(
                "Email request has been successfully added to the queue for user '%s'. " +
                        "The email will be sent from the system to '%s' with subject '%s'. " +
                        "The request has been added to the '%s' queue at %s.",
                orderDetail.getCustomerName(), orderDetail.getCustomerEmail(), subject, RabbitMQConfig.PAYMENT_SUCCESS_QUEUE, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
    }

    private static EmailMessage getEmailMessageForPayment(OrderDetailDTO orderDetail, String subject) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", orderDetail.getCustomerName());
        templateData.put("date", orderDetail.getOrderDate());
        templateData.put("id", orderDetail.getOrderId());
        templateData.put("amount", orderDetail.getOrderAmount());

        // Tạo đối tượng EmailMessage
        EmailMessage emailMessage = new EmailMessage(
                orderDetail.getCustomerEmail(),
                subject,
                "payment-success",
                templateData
        );
        return emailMessage;
    }

    @PostMapping("/lock-account")
    public ResponseEntity<String> lockAccountRequest(@RequestParam("file") MultipartFile file, @RequestBody AccountLockDTO accountLockDTO) {
        // Kiểm tra file không null và là file Excel
        if (file.isEmpty() || !Objects.requireNonNull(file.getOriginalFilename()).endsWith(".xlsx")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File không hợp lệ. Vui lòng tải lên file Excel.");
        }

        // Gửi email
        Locale locale = LocaleContextHolder.getLocale();
        String subject = String.format("[%s] [%s] %s [%s]",
                messageSource.getMessage("company.name", null, locale),
                new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                messageSource.getMessage("account.preheader", null, locale),
                accountLockDTO.getAccountEmail());

        EmailMessage emailMessage = getEmailMessageForLockAccount(accountLockDTO, subject);

        // Chuyển file Excel thành byte array
        byte[] excelFileData = null;
        try {
            excelFileData = file.getBytes();  // Dữ liệu byte từ file tải lên
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Có lỗi khi xử lý file Excel.");
        }

        // Gửi thông điệp vào RabbitMQ Exchange với Routing Key 'lock_account_email'
        byte[] finalExcelFileData = excelFileData;
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, RabbitMQConfig.LOCK_ACCOUNT_ROUTING_KEY, emailMessage, message -> {
            message.getMessageProperties().setContentType("application/x-java-serialized-object");
            message.getMessageProperties().setHeader("excelFileData", finalExcelFileData);  // Gửi file Excel kèm theo
            return message;
        });

        return ResponseEntity.ok(String.format(
                "Email request has been successfully added to the queue for user '%s'. " +
                        "The email will be sent from the system to '%s' with subject '%s'. " +
                        "The request has been added to the '%s' queue at %s.",
                accountLockDTO.getAccountName(), accountLockDTO.getAccountEmail(), subject, RabbitMQConfig.LOCK_ACCOUNT_QUEUE,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
    }
    private static EmailMessage getEmailMessageForLockAccount(AccountLockDTO accountLockDTO, String subject) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", accountLockDTO.getAccountName());
        templateData.put("email", accountLockDTO.getAccountEmail());
        templateData.put("lockReason", accountLockDTO.getAccountReason());

        // Tạo đối tượng EmailMessage
        EmailMessage emailMessage = new EmailMessage(
                accountLockDTO.getAccountEmail(),
                subject,
                "payment-success",
                templateData
        );
        return emailMessage;
    }
}
