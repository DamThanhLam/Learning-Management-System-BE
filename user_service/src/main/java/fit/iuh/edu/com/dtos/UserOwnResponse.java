package fit.iuh.edu.com.dtos;

import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Data
@Builder
public class UserOwnResponse {
    private String id;
    private String userName;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;
    private Gender gender;
    private String description;
    private String urlImage;
    private String cvFile;
    private Map<String, String> contacts;
}
