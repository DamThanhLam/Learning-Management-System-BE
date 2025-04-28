package fit.iuh.edu.com.models;

import fit.iuh.edu.com.converter.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDbBean
@Builder
public class Review {
    private String id;
    private String userId;
    private String userName;
    private String urlAvt;
    private String courseId;
    private String urlImage;
    private String content;
    private int review;
    private Instant createdAt;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    // GSI 1: courseId-createdAt-index
    @DynamoDbSecondaryPartitionKey(indexNames = "courseId-createdAt-index")
    public String getCourseIdForCourseIdCreatedAtIndex() {
        return courseId;
    }

    @DynamoDbSecondarySortKey(indexNames = "courseId-createdAt-index")
    public Instant getCreatedAt() {
        return createdAt;
    }

    // GSI 2: userId-courseId-index
    @DynamoDbSecondaryPartitionKey(indexNames = "userId-courseId-index")
    public String getUserIdForUserIdCourseIdIndex() {
        return userId;
    }

    @DynamoDbSecondarySortKey(indexNames = "userId-courseId-index")
    public String getCourseIdForUserIdCourseIdIndex() {
        return courseId;
    }
}