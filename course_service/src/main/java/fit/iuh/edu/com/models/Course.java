package fit.iuh.edu.com.models;


import fit.iuh.edu.com.enums.CourseLevel;
import fit.iuh.edu.com.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.LastModifiedDate;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private String id;
    private String courseName;
    private String description;
    private double price;
    private String category;
    private String teacherId;
    private String teacherName;
    private CourseStatus status;
    private String urlAvt;
    private float totalReview;
    private CourseLevel level;
    private int countOrders;
    private int countReviews;
    private int countLectures;
    private String urlIntro;
    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }
}
