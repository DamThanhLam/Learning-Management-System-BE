package lms.payment_service_lms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequestAccountDTO {
    private String name;
    private String email;
    private String password;
    private LocalDateTime date;
}
