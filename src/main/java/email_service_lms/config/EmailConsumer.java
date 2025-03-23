package email_service_lms.config;

import email_service_lms.model.EmailMessage;
import email_service_lms.services.EmailService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
public class EmailConsumer {

    @Autowired
    private EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void consumeEmailMessage(EmailMessage emailMessage) {
        Context context = new Context();
        context.setVariables(emailMessage.getTemplateData());

        emailService.sendHtmlEmail(
                emailMessage.getTo(),
                emailMessage.getSubject(),
                emailMessage.getTemplateName(),
                context
        );
    }
}
