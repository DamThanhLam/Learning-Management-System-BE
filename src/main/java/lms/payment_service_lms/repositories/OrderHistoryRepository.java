package lms.payment_service_lms.repositories;

import lms.payment_service_lms.entity.Course;
import lms.payment_service_lms.entity.OrderHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Repository
public class OrderHistoryRepository {
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<OrderHistory> dynamoDbTable;

    public OrderHistoryRepository() {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("Course", TableSchema.fromBean(OrderHistory.class));
    }

    public void create(OrderHistory orderHistory) {
        dynamoDbTable.putItem(orderHistory);
    }

    public OrderHistory findById(String courseId) {
        Key key = Key.builder().partitionValue(courseId).build();
        return dynamoDbTable.getItem(key);
    }
}
