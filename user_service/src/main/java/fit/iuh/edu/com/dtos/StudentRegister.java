package fit.iuh.edu.com.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentRegister {
    @NotNull(message = "Username must not be null")
    @Length(min = 4, max = 50, message = "Username have length from 4 to 50")
    private String username;
    @NotNull(message = "Password must not be null.")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,}$", message = "Password must be at least 8 characters, including at least one uppercase letter, one lowercase letter, one number, and one special character.")
    private String password;
    @Email(message = "Email is not in correct format")
    private String email;

}
