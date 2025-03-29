//package fit.iuh.edu.com.repositories;
//
//import fit.iuh.edu.com.models.Teacher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//import software.amazon.awssdk.enhanced.dynamodb.*;
//import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
//import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
//import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
//import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
//import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//@Repository
//public class TeacherRepository {
//    @Autowired
//    private DynamoDbClient dynamoDBClient;
//    private DynamoDbTable<Teacher> dynamoDbTable;
//    public TeacherRepository() {
//        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
//        dynamoDbTable = enhancedClient.table("User", TableSchema.fromBean(Teacher.class));
//    }
//    public Teacher create(Teacher teacherTemp) {
//        teacherTemp.setId(UUID.randomUUID().toString());
//        dynamoDbTable.putItem(teacherTemp);
//        return teacherTemp;
//    }
//    public Teacher findUserByEmailOrPhoneNumber(String email, String phoneNumber) {
//        Map<String, AttributeValue> expressionValue = new HashMap<>();
//        expressionValue.put(":phoneNumber", AttributeValue.builder().s(phoneNumber).build());
//        expressionValue.put(":email", AttributeValue.builder().s(email).build());
//        Expression expression = Expression.builder().expression(":email = email OR :phoneNumber = phoneNumber").expressionValues(expressionValue).build();
//        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest
//                .builder()
//                .filterExpression(expression)
//                .build();
//        return dynamoDbTable.scan(scanEnhancedRequest).items().stream().findFirst().orElse(null);
//    }
//
//    public Teacher findById(String id) {
//        QueryConditional queryConditional = QueryConditional.keyEqualTo(
//                Key.builder().partitionValue(id).build()
//        );
//
//        QueryEnhancedRequest enhancedRequest = QueryEnhancedRequest.builder()
//                .queryConditional(queryConditional)
//                .build();
//
//        return dynamoDbTable.query(enhancedRequest)
//                .items()
//                .stream()
//                .findFirst()
//                .orElse(null);
//    }
//
//    public void delete(Teacher teacherTemp) {
//        dynamoDbTable.deleteItem(teacherTemp);
//    }
//
//    public void update(Teacher teacher) {
//        dynamoDbTable.updateItem(teacher);
//    }
//}
