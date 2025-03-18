package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

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
}
