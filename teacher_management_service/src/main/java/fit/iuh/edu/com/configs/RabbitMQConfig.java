package fit.iuh.edu.com.configs;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {


    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }

    // Tên Exchange
    public static final String EXCHANGE = "email_exchange";

    // Queue cho email thanh toán thành công
    public static final String PAYMENT_SUCCESS_QUEUE = "payment_success_queue";
    public static final String ACCOUNT_REQUEST_QUEUE = "account_request_queue";
    public static final String LOCK_ACCOUNT_QUEUE = "lock_account_queue";

    // Routing Key cho email thanh toán thành côngnt_success_email";
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment_request_email";
    public static final String ACCOUNT_REQUEST_ROUTING_KEY = "account_request_email";
    public static final String LOCK_ACCOUNT_ROUTING_KEY = "lock_account_email";
}

