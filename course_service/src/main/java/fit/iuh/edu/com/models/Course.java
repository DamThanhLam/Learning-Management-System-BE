package fit.iuh.edu.com.models;


import fit.iuh.edu.com.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.LocalDateTime;
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

    private LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime updateTime = LocalDateTime.now();

    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private LocalDateTime startTime;
    private LocalDateTime completeTime;
    private CourseStatus status;
    private String urlAvt;
    private String createdBy;
    private String updatedBy;
    private String teacherId;
    private String teacherName;
    private int numberMinimum;
    private int numberMaximum;
    private int numberCurrent;
    private List<String> studentIds;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = "courseName-closeTime-index")
    public String getCourseName() {
        return courseName;
    }

    public Course(String courseName, String description, double price, LocalDateTime openTime, LocalDateTime closeTime, LocalDateTime startTime, LocalDateTime completeTime, CourseStatus status, String urlAvt, String teacherId, String teacherName, int numberMinimum, int numberMaximum, int numberCurrent) {
        this.courseName = courseName;
        this.description = description;
        this.price = price;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.startTime = startTime;
        this.completeTime = completeTime;
        this.status = status;
        this.urlAvt = urlAvt;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.numberMinimum = numberMinimum;
        this.numberMaximum = numberMaximum;
        this.numberCurrent = numberCurrent;
    }
}
