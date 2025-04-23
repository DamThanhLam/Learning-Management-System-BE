package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.Account;
import fit.iuh.edu.com.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepository {
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<User> dynamoDbTable;
    public UserRepository(){
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("User", TableSchema.fromBean(User.class));
    }

    public boolean userExists(String email) {
        return true;
    }
    public User getUser(String id){
        return null;
    }
    public User getUserDetail(String id){
        return null;
    }

    public User create(User user) {
        dynamoDbTable.putItem(user);
        return user;
    }

    public User find(String id) {
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

    @PreAuthorize("hasRole('ADMIN')")
    public List<User> findByRole(String role) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(":role", AttributeValue.builder().s(role).build());

        Expression expression = Expression.builder()
                .expressionValues(item)
                .expression("contains(groups, :role)").build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression).build();
        return dynamoDbTable.scan(request).items().stream().toList();
    }

    public User findByEmail(String email) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put(":email", AttributeValue.builder().s(email).build());

        Expression expression = Expression.builder()
                .expressionValues(item)
                .expression("email= :email").build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression).build();
        return dynamoDbTable.scan(request).items().stream().findFirst().orElse(null);
    }

    public void update(User user) {
        System.out.println(user.getUrlImage());
        dynamoDbTable.putItem(user);
    }
}
