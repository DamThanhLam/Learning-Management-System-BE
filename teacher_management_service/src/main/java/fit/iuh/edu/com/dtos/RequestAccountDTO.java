package fit.iuh.edu.com.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class RequestAccountDTO {
    private String name;
    private String email;
    private String password;
    private LocalDateTime date;
    private int status; // 0 la accept, 1 la reject
    private String message;
}
