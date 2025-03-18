package fit.iuh.edu.com.dtos;

import fit.iuh.edu.com.enums.CourseLevel;
import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.services.Impl.BucketServiceImpl;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseRequestAdd {
    @NotNull(message = "Course name must not be null")
    @Length(min = 5,message = "Course name must be longer than 5")
    private String courseName;
    @NotNull(message = "Course description must not be null")
    @Length(min = 5, message = "description must be longer than 5")
    private String description;
    @NotNull(message = "Price must not be null")
    @Min(value = 0, message = "price must be greater than 0")
    private float price;
    @NotNull(message = "File Avatar must not be null")
    private MultipartFile fileAvt;
    @NotNull(message = "Category must not be null")
    private String category;
    @NotNull(message = "Status must not be null")
    private CourseStatus status;
    @NotNull(message = "Level must not be null")
    private CourseLevel level;

    public Course covertCourseRequestAddToCourse(String urlAvt, String userName, String userId) {
        Course course = new Course();
        course.setCourseName(courseName);
        course.setDescription(description);
        course.setPrice(price);
        course.setCategory(category);
        course.setStatus(status);
        course.setLevel(level);
        course.setUrlAvt(urlAvt);
        course.setTeacherName(userName);
        course.setTeacherId(userId);
        return course;
    }
}
