package fit.iuh.edu.com.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestAdd {
    @NotNull(message = "Course id must be not null")
    private String courseId;
    @NotNull(message = "File image must be not null")
    private MultipartFile fileImage;
    @NotNull(message = "Content must be no null")
    @Length(min = 10, max = 500, message = "Content must be length from 10 to 500")
    private String content;
    @NotNull(message = "Review must not be null")
    @Min(value = 1,message = "The review must be greater than 0")
    @Max(value = 5,message = "The review must be less than 6")
    private int review;
}
