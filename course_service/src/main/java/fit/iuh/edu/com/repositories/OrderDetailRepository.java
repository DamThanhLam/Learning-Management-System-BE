package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.OrderDetail;
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
public class OrderDetailRepository {
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<OrderDetail> dynamoDbTable;
    public OrderDetailRepository(){
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("OrderDetail", TableSchema.fromBean(OrderDetail.class));

    }
    public OrderDetail getOrderDetailByCourseIdAndUserId(String courseId, String userId){
        Map<String, AttributeValue> attributeValueMap = new HashMap<String, AttributeValue>();
        attributeValueMap.put(":courseId",AttributeValue.builder().s(courseId).build());
        attributeValueMap.put(":userId",AttributeValue.builder().s(userId).build());

        Expression expression =
                Expression.builder()
                        .expressionValues(attributeValueMap)
                        .expression("courseId = :courseId AND userId = :userId").build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder().filterExpression(expression).build();
        return dynamoDbTable.scan(request).items().stream().findFirst().orElse(null);
    }
}
