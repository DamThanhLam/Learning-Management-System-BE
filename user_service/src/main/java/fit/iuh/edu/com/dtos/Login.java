package fit.iuh.edu.com.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Login {
    @NotNull(message = "email must not be null")
    private String email;
    @NotNull(message = "password must not be null")
    private String password;
}
