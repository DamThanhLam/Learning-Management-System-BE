package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<User> dynamoDbTable;
    public UserRepository() {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("User", TableSchema.fromBean(User.class));
    }
    public User getUserById(String id) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(key -> key.partitionValue(id));
        QueryEnhancedRequest queryEnhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();
        PageIterable<User> pageIterable = dynamoDbTable.query(queryEnhancedRequest);
        AtomicReference<User> response = new AtomicReference<>();
        pageIterable.items().forEach(response::set);
        return response.get();
    }
    public void updateUser(User user) {
        dynamoDbTable.updateItem(user);
    }
    public User create(User teacherTemp) {
        teacherTemp.setId(UUID.randomUUID().toString());
        dynamoDbTable.putItem(teacherTemp);
        return teacherTemp;
    }
    public User findById(String id) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(id).build()
        );

        QueryEnhancedRequest enhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return dynamoDbTable.query(enhancedRequest)
                .items()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public void delete(User user) {
        dynamoDbTable.deleteItem(user);
    }

    public void update(User user) {
        dynamoDbTable.updateItem(user);
    }

    public User findUserByEmailOrPhoneNumber(String email, String phoneNumber) {
        Map<String, AttributeValue> expressionValue = new HashMap<>();
        expressionValue.put(":phoneNumber", AttributeValue.builder().s(phoneNumber).build());
        expressionValue.put(":email", AttributeValue.builder().s(email).build());
        Expression expression = Expression.builder().expression(":email = email OR :phoneNumber = phoneNumber").expressionValues(expressionValue).build();
        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest
                .builder()
                .filterExpression(expression)
                .build();
        return dynamoDbTable.scan(scanEnhancedRequest).items().stream().findFirst().orElse(null);
    }
}
