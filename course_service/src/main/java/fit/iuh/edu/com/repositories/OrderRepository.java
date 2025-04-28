package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.models.Order;
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
public class OrderRepository{
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<Order> dynamoDbTable;
    public OrderRepository(){
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("Order", TableSchema.fromBean(Order.class));
    }
    public List<Order> getOrdersByUserId(String userId){
        Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
        attributeValueMap.put(":userId", AttributeValue.builder().s(userId).build());

        Expression expression =
                Expression.builder()
                        .expressionValues(attributeValueMap)
                        .expression("userId = :userId").build();

        ScanEnhancedRequest scanEnhancedRequest =
                ScanEnhancedRequest.builder()
                        .filterExpression(expression).build();
        return dynamoDbTable.scan(scanEnhancedRequest).items().stream().toList();
    }
}
