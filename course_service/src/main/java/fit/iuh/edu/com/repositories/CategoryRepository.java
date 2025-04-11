package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.Category;
import fit.iuh.edu.com.models.Course;
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
import java.util.UUID;

@Repository
public class CategoryRepository {
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<Category> dynamoDbTable;
    public CategoryRepository(){
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("Category", TableSchema.fromBean(Category.class));

    }
    public List<Category> getAll(){
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .build();
        return dynamoDbTable.scan().items().stream().toList();
    }
    public void add(Category category){
        category.setId(UUID.randomUUID().toString());
        category.setCategoryName(category.getCategoryName().trim().replace("\\s+",""));
        dynamoDbTable.putItem(category);
    }
    public Category get(String categoryName){
        Map<String, AttributeValue> expressionValues = new HashMap<>();
        expressionValues.put(":categoryName", AttributeValue.builder().s(categoryName.trim().replace("\\s+","")).build());
        Expression expression = Expression.builder()
                .expressionValues(expressionValues)
                .expression("categoryName = :categoryName").build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();
        return dynamoDbTable.scan().items().stream().toList().get(0);
    }
}
