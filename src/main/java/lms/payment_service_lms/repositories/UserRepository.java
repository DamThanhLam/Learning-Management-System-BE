package lms.payment_service_lms.repositories;
import lms.payment_service_lms.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

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
}
