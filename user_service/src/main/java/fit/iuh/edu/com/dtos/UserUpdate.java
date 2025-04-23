package fit.iuh.edu.com.dtos;

import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdate {
    @Length(min = 4, max = 50, message = "Username have length from 4 to 50")
    private String userName;
    @Email(message = "Email is not in correct format")
    private String email;
    @Length(min = 10, max = 11, message = "phoneNumber have length from 10 to 11")
    private String phoneNumber;
    private LocalDate birthday;
    private Gender gender;
    private String description;
    private MultipartFile imageAvt;
    private MultipartFile cvFile;
    private Map<String, String> contacts;
}
