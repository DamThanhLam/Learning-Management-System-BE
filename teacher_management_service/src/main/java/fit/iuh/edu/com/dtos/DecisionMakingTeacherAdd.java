package fit.iuh.edu.com.dtos;

import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.enums.DecisionMaking;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class DecisionMakingTeacherAdd {
    @NotNull(message = "Id must not be null")
    private String id;
    @NotNull(message = "action must not be null")
    private String action;
    @Length(min = 50, max = 500, message = "Description must be length from 50 to 500")
    private String description;
}
