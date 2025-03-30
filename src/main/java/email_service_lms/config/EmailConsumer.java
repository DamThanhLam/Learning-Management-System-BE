package email_service_lms.config;

import email_service_lms.model.EmailMessage;
import email_service_lms.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Component
public class EmailConsumer {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_SUCCESS_QUEUE)
    public void consumePaymentSuccessEmail(EmailMessage emailMessage) {
        emailMessage.setTemplateName("payment-success");
        Context context = new Context();
        context.setVariables(emailMessage.getTemplateData());

        // Gửi email thông báo thanh toán thành công
        emailService.sendHtmlEmail(
                emailMessage.getTo(),
                emailMessage.getSubject(),
                emailMessage.getTemplateName(),
                context
        );
    }

    @RabbitListener(queues = RabbitMQConfig.ACCOUNT_REQUEST_QUEUE)
    public void consumeAccountRequestEmail(EmailMessage emailMessage) {
        emailMessage.setTemplateName("account-request");
        Context context = new Context();
        context.setVariables(emailMessage.getTemplateData());

        // Gửi email thông báo thanh toán thành công
        emailService.sendHtmlEmail(
                emailMessage.getTo(),
                emailMessage.getSubject(),
                emailMessage.getTemplateName(),
                context
        );
    }

    @RabbitListener(queues = RabbitMQConfig.LOCK_ACCOUNT_QUEUE)
    public void accountLockRequestEmail(EmailMessage emailMessage, @Header("excelFileData") byte[] excelFileData) {
        // Thiết lập template và dữ liệu
        emailMessage.setTemplateName("account-lock");
        Context context = new Context();
        context.setVariables(emailMessage.getTemplateData());

        // Gửi email kèm file Excel
        try {
            emailService.sendEmailWithAttachment(
                    emailMessage.getTo(),
                    emailMessage.getSubject(),
                    emailMessage.getTemplateName(),
                    context,
                    excelFileData  // Đính kèm file Excel vào email
            );
        } catch (MessagingException e) {
            e.printStackTrace();
            // Xử lý lỗi nếu có
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
