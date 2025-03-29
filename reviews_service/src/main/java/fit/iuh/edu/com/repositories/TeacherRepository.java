package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class TeacherRepository {
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<Teacher> dynamoDbTable;
    public TeacherRepository() {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("Teacher", TableSchema.fromBean(Teacher.class));
    }

    public Teacher findByCognitoId(String id) {
       return null;
    }

    public void update(Teacher teacher) {
        dynamoDbTable.updateItem(teacher);
    }
}
