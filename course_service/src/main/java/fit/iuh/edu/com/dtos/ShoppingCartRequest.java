package fit.iuh.edu.com.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShoppingCartRequest {
    @NotNull(message = "course Id must not be null")
    private String courseId;

}

