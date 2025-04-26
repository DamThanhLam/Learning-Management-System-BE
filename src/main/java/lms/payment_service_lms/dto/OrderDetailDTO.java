package email_service_lms.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String customerName;
    private String customerEmail;
    private String orderAmount;
    private String orderId;
    private String orderDate;
}
