package fit.iuh.edu.com.dtos;

import fit.iuh.edu.com.enums.CourseStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseRequestUpdate {
    @NotNull(message = "course id must not be null")
    String id;
    @Null
    @Length(min = 2)
    String category;
    @Null
    @Future(message = "open time must be a future date")
    LocalDateTime openTime;
    @Null
    @Future(message = "close time mus be a future date")
    LocalDateTime closeTime;
    @Null
    @Future(message = "start time mus be a future date")
    LocalDateTime startTime;
    @Null
    @Future(message = "complete time mus be a future date")
    LocalDateTime completeTime;
    @Null
    @Min(value = 1, message = "number minimum must be greater than 0")
    int numberMinimum;
    @Null
    @Max(value = 100, message = "number maximum must be greater than 0")
    int numberMaximum;
    @Null
    CourseStatus courseStatus;
    @Null
    @Min(value = 0, message = "price must be greater than 0")
    @Max(value = 100000000, message = "price must be less than 100.000.000")
    double price;
    @Null
    String teacherId;
}
