package lms.payment_service_lms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class PaymentServiceLmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceLmsApplication.class, args);
    }

}
