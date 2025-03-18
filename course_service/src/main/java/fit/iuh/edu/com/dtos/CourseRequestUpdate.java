package fit.iuh.edu.com.dtos;

import fit.iuh.edu.com.enums.CourseLevel;
import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.services.Impl.BucketServiceImpl;
import jakarta.validation.constraints.*;
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
public class CourseRequestUpdate{
    @NotNull(message = "Id name must not be null")
    private String id;
    @Length(min = 5,message = "Course name must be longer than 5")
    private String courseName;
    @Length(min = 5, message = "description must be longer than 5")
    private String description;
    @Min(value = 0, message = "price must be greater than 0")
    private float price;
    private MultipartFile fileAvt;
    private String category;
    private CourseStatus status;
    private CourseLevel level;

    public Course toCourse(String urlAvt,Course course) {
        if(courseName != null){
            course.setCourseName(courseName);
        }
        if(description != null){
            course.setDescription(description);
        }
        if(price != 0){
            course.setPrice(price);
        }
        if(category != null){
            course.setCategory(category);
        }
        if(status != null) {
            course.setStatus(status);
        }
        if(level != null){
            course.setLevel(level);
        }
        if(urlAvt != null){
            course.setUrlAvt(urlAvt);
        }
        return course;
    }
    public Course toCourse(Course course) {
        if(courseName != null){
            course.setCourseName(courseName);
        }
        if(description != null){
            course.setDescription(description);
        }
        if(price != 0){
            course.setPrice(price);
        }
        if(category != null){
            course.setCategory(category);
        }
        if(status != null) {
            course.setStatus(status);
        }
        if(level != null){
            course.setLevel(level);
        }

        return course;
    }
}
