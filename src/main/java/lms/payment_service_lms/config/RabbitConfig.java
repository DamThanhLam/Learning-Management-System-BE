package lms.payment_service_lms.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue paymentQueue() {
        return new Queue("payment_queue", false); // Đảm bảo rằng queue không bị xóa sau khi tắt RabbitMQ
    }
}

