package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.models.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Repository
public class CourseRepository {
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private DynamoDbTable<Course> dynamoDbTable;
    public CourseRepository(){
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("Course", TableSchema.fromBean(Course.class));

    }
    public Course create(Course course) {

        course.setId(java.util.UUID.randomUUID().toString());
        dynamoDbTable.putItem(course);
        return course;
    }
    public Course courseExist(String courseId) {
        Key key = Key.builder().partitionValue(courseId).build();
        return dynamoDbTable.getItem(key);
    }
//
//    public Course update(Course course) {
//        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
//        DynamoDbTable<Course> courseTable = enhancedClient.table("Course", TableSchema.fromBean(Course.class));
//        courseTable.updateItem(course);
//        return course;
//    }
//

    public ScanResponse findByCourseName(String courseName, Map<String, AttributeValue> lastEvaluatedKey, int pageSize) {

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":courseName", AttributeValues.stringValue(courseName));
        expressionAttributeValues.put(":openTime", AttributeValues.stringValue(String.valueOf(LocalDateTime.now())));
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#s", "status"); // Định nghĩa alias cho "status"

        ScanRequest query = ScanRequest
                .builder()
                .tableName("Course")
                .filterExpression("contains(courseName, :courseName) and openTime >= :openTime")
                .exclusiveStartKey(lastEvaluatedKey)
                .expressionAttributeValues(expressionAttributeValues)
                .expressionAttributeNames(expressionAttributeNames)
                .projectionExpression("id, courseName, description, price, createTime, updateTime, openTime, closeTime, startTime, completeTime, urlAvt, teacherName, numberMinimum, numberMaximum, numberCurrent, category, studentIds, #s, teacherId")
                .limit(pageSize)
                .build();


        ScanResponse response = dynamoDBClient.scan(query);

        return response;
    }

    public ScanResponse findOwnOrStudentIdByCourseName(String username, String courseName, Map<String, AttributeValue> lastEvaluatedKey, int pageSize) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":courseName", AttributeValues.stringValue(courseName));
        expressionAttributeValues.put(":userId", AttributeValues.stringValue(username));
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#s", "status");
        ScanRequest request = ScanRequest
                .builder()
                .tableName("Course")
                .filterExpression("contains(courseName, :courseName) AND teacherId = :userId OR contains(studentIds,:userId)")
                .expressionAttributeValues(expressionAttributeValues)
                .expressionAttributeNames(expressionAttributeNames)
                .projectionExpression("id, courseName, description, price, createTime, updateTime, openTime, closeTime, startTime, completeTime, urlAvt, teacherName, numberMinimum, numberMaximum, numberCurrent, category, studentIds, #s, teacherId")
                .limit(pageSize)
                .exclusiveStartKey(lastEvaluatedKey)
                .build();
        ScanResponse response = dynamoDBClient.scan(request);
        return response;
    }

    @PostAuthorize("returnObject.teacherId == authentication.name OR returnObject.studentsId.contains(authentication.name)")
    public Course getCourseDetailById(String courseId) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        DynamoDbTable<Course> dynamoDbTable = enhancedClient.table("Course", TableSchema.fromBean(Course.class));
        return dynamoDbTable.getItem(Key.builder().partitionValue(courseId).build());
    }

    public ScanResponse getCoursesByStudentID(String studentID, int pageSize, Map<String, AttributeValue> lastEvaluatedKey) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":studentID", AttributeValues.stringValue(studentID));
        expressionAttributeValues.put(":s", AttributeValues.stringValue("OPEN"));
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#s", "status");
        ScanRequest.Builder requestBuilder  = ScanRequest
                .builder()
                .tableName("Course")
                .filterExpression("contains(studentsId,:studentID) AND #s = :s")
                .expressionAttributeValues(expressionAttributeValues)
                .expressionAttributeNames(expressionAttributeNames)
                .projectionExpression("id, courseName, price, urlAvt, teacherName, category, #s, teacherId")
                .limit(pageSize != 0 ?pageSize:10);
        if(lastEvaluatedKey!= null && !lastEvaluatedKey.isEmpty()){
            System.out.println("lastEvaluatedKey:"+lastEvaluatedKey);
            requestBuilder .exclusiveStartKey(lastEvaluatedKey);
        }
        ScanRequest request = requestBuilder.build();
        return dynamoDBClient.scan(request);
    }

    public ScanResponse getCoursesByTeacherID(String teacherId, int pageSize, Map<String, AttributeValue> lastEvaluatedKey) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":teacherId", AttributeValues.stringValue(teacherId));
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#s", "status");
        ScanRequest.Builder requestBuilder  = ScanRequest
                .builder()
                .tableName("Course")
                .filterExpression("teacherId = :teacherId")
                .expressionAttributeValues(expressionAttributeValues)
                .expressionAttributeNames(expressionAttributeNames)
                .projectionExpression("id, courseName, price, studentIds, #s, countOrders, countLectures, countReviews")
                .limit(pageSize != 0 ?pageSize:10);
        if(lastEvaluatedKey!= null && !lastEvaluatedKey.isEmpty()){
            System.out.println("lastEvaluatedKey:"+lastEvaluatedKey);
            requestBuilder .exclusiveStartKey(lastEvaluatedKey);
        }
        ScanRequest request = requestBuilder.build();
        return dynamoDBClient.scan(request);
    }


    public Course updateCourse(Course course) {
        return dynamoDbTable.updateItem(course);
    }

    public ScanResponse getCoursesByCourseNameOrCategory(String courseName, String category, int pageSize, Map<String, AttributeValue> lastEvaluatedKey) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        String filterExpression = "";
        if(courseName != null && !courseName.isEmpty()){
            expressionAttributeValues.put(":courseName", AttributeValues.stringValue(courseName));
            filterExpression+="contains(courseName,:courseName)";
        }
        if(category != null && !category.isEmpty()){
            expressionAttributeValues.put(":category", AttributeValues.stringValue(category));
            if(!filterExpression.isEmpty()){
                filterExpression+=" OR ";
            }
            filterExpression+="contains(category,:category)";
        }
        if(!filterExpression.isEmpty()){
            filterExpression+=" AND ";
        }
        expressionAttributeValues.put(":s", AttributeValues.stringValue("OPEN"));
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#s", "status");
        ScanRequest.Builder requestBuilder  = ScanRequest
                .builder()
                .tableName("Course")
                .filterExpression(filterExpression+"#s = :s")
                .expressionAttributeValues(expressionAttributeValues)
                .expressionAttributeNames(expressionAttributeNames)
                .projectionExpression("id, courseName, price, urlAvt, teacherName, category, #s, teacherId")
                .limit(pageSize != 0 ?pageSize:10);
        if(lastEvaluatedKey!= null && !lastEvaluatedKey.isEmpty()){
            System.out.println("lastEvaluatedKey:"+lastEvaluatedKey);
            requestBuilder .exclusiveStartKey(lastEvaluatedKey);
        }
        ScanRequest request = requestBuilder.build();
        return dynamoDBClient.scan(request);
    }
}
