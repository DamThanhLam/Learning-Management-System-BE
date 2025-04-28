package fit.iuh.edu.com.lectureservice1.repository;

import fit.iuh.edu.com.lectureservice1.model.Course;
import fit.iuh.edu.com.lectureservice1.model.Lecture;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CourseRepository {
    private final DynamoDbTable<Course> courseTable;

    public CourseRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        // get the DynamoDB table and map it to the Lecture model.
        this.courseTable = enhancedClient.table("Course", TableSchema.fromBean(Course.class));
    }
    public Course getCourseCourseIdAndTeacherId(String userId, String courseId) {
        System.out.println(userId);
        System.out.println(courseId);
        Map<String, AttributeValue> expressionValue = new HashMap<String, AttributeValue>();
        expressionValue.put(":userId",AttributeValue.builder().s(userId).build());
        expressionValue.put(":courseId",AttributeValue.builder().s(courseId).build());

        Expression expression = Expression.builder().expressionValues(expressionValue)
                .expression("id = :courseId  AND teacherId = :userId").build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression).build();
        System.out.println(this.courseTable.scan(request).items().stream().count());
        return this.courseTable.scan(request).items().stream().findFirst().orElse(null);
    }

    public Course getId(String courseId) {
       return courseTable.getItem(Key.builder().partitionValue(courseId).build());
    }

    public void update(Course course) {
        courseTable.putItem(course);
    }
}
