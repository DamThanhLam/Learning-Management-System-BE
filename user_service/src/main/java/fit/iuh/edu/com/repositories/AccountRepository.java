package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.Account;
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
import java.util.Map;

@Repository
public class AccountRepository {
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<Account> dynamoDbTable;
    public AccountRepository(){
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("Account", TableSchema.fromBean(Account.class));
    }
    public Account create(Account course) {
        dynamoDbTable.putItem(course);
        return course;
    }
    public Account accountExists(String email) {
        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put(":email", AttributeValue.builder().s(email).build());

        Expression expression = Expression.builder()
                .expression("email = :email")
                .expressionValues(key)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();
        return dynamoDbTable.scan(request).items().stream().findFirst().orElse(null);
    }

    public Account getByEmail(String email) {
        Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        key.put(":email", AttributeValue.builder().s(email).build());

        Expression expression = Expression.builder()
                .expression("email = :email")
                .expressionValues(key)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();
        return dynamoDbTable.scan(request).items().stream().findFirst().orElse(null);
    }
}
