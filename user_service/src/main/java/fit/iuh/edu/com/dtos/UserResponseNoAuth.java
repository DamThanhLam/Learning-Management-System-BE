package fit.iuh.edu.com.dtos;

import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class UserResponseNoAuth {
    private String id;
    private String userName;
    private String description;
    private String urlImage;
    private Map<String, String> contacts;
}
