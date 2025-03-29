package fit.iuh.edu.com.dtos;

import fit.iuh.edu.com.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class TeacherAddRequest {
    @NotNull(message = "The teacher's name must not be null")
    @Length(min = 4, message = "The teacher's name must be length greater than 4")
    private String teacherName;
    @NotNull(message = "The gender must not be null")
    private Gender gender;
    @NotNull(message = "The birth day must not be null")
    private LocalDate birthday;
    @Email(message = "The email must be in correct format")
    private String email;
    @NotNull(message = "The phone number must not be null")
    @Length(min = 10, max = 11, message = "Phone number must be 10-11 digits long")
    private String phoneNumber;
    @NotNull(message = "The description must not be null")
    @Length(min = 100, max = 1000, message = "The description must be length from 100 to 1000")
    private String description;
    @NotNull(message = "The Face image must not be null")
    private MultipartFile faceImage;
    @NotNull(message = "The CV must not be null")
    private MultipartFile cvFile;
}
