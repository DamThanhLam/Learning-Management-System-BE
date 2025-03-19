package fit.iuh.edu.com.models;


import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.enums.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.util.List;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course {
    private String id;
    private String courseName;
    private String description;
    private double price;
    private String category;
    private String teacherId;
    private String teacherName;
    private List<String> studentsId;
    private CourseStatus status;
    private String urlAvt;
    private float totalReview;
    private CourseLevel level;
    private int countOrders;
    private int countReviews;
    private int countLectures;
    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }
}
