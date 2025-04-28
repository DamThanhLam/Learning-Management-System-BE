package fit.iuh.edu.com.lectureservice1.repository;

import fit.iuh.edu.com.lectureservice1.model.Course;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class OrderRepository {
    private final DynamoDbTable<Course> courseTable;

    public OrderRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        // get the DynamoDB table and map it to the Lecture model.
        this.courseTable = enhancedClient.table("Course", TableSchema.fromBean(Course.class));
    }
}
