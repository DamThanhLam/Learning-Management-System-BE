package fit.iuh.edu.com.lectureservice1.model;


import fit.iuh.edu.com.lectureservice1.enums.CourseLevel;
import fit.iuh.edu.com.lectureservice1.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

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
    private List<String> studentsId;
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
