package email_service_lms.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailDTO {

    private String customerName;
    private String customerEmail;
    private String orderAmount;
    private String orderId;
    private String orderDate;
}
