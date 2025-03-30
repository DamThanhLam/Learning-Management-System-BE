package email_service_lms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

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

    @Bean
    public Queue paymentSuccessQueue() {
        return new Queue(PAYMENT_SUCCESS_QUEUE);
    }

    @Bean
    public Queue accountRequestQueue() {
        return new Queue(ACCOUNT_REQUEST_QUEUE);
    }

    @Bean
    public Queue lockAccountQueue() {
        return new Queue(LOCK_ACCOUNT_QUEUE);
    }

    @Bean
    public DirectExchange emailExchange() {
        return new DirectExchange(EXCHANGE);  // Tạo Exchange
    }

    // Bind Queue với Routing Key vào Exchange
    @Bean
    public Binding paymentSuccessBinding(Queue paymentSuccessQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(paymentSuccessQueue).to(emailExchange).with(PAYMENT_SUCCESS_ROUTING_KEY);
    }

    @Bean
    public Binding accountRequestQueueBinding(Queue accountRequestQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(accountRequestQueue).to(emailExchange).with(ACCOUNT_REQUEST_ROUTING_KEY);
    }

    @Bean
    public Binding lockAccountQueueBinding(Queue paymentSuccessQueue, DirectExchange emailExchange) {
        return BindingBuilder.bind(paymentSuccessQueue).to(emailExchange).with(PAYMENT_SUCCESS_ROUTING_KEY);
    }
}

