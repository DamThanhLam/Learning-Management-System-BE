package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.models.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FeedbackRepository {
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<Feedback> dynamoDbTable;
    public FeedbackRepository(){
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("Feedback", TableSchema.fromBean(Feedback.class));
    }

    public List<Feedback> findByTeacherId(String courseId){
        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put(":courseId", AttributeValue.builder().s(courseId).build());
        Expression expression = Expression.builder()
                .expression("courseId = :courseId")
                .expressionValues(key)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression).build();
        return dynamoDbTable.scan(request).items().stream().toList();
    }
}
