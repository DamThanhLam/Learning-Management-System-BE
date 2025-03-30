package email_service_lms.dto;

import lombok.Data;

@Data
public class AccountLockDTO {
    private String accountName;
    private String accountDate;
    private String accountEmail;
    private String accountReason;
}
