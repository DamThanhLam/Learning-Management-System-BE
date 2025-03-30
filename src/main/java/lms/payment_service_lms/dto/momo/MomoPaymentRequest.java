package lms.payment_service_lms.dto.momo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class MomoPaymentRequest {
    private String apiKey;
    private double amount;
    private String orderId;
    private String phoneNumber;
    private String description;
}

