package lms.payment_service_lms.repositories;
import lms.payment_service_lms.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.time.LocalDateTime;
import java.util.*;

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

    public List<Course> findAll() {
        List<Course> result = new ArrayList<>();

        try {
            // Scan toàn bộ bảng Course
            ScanEnhancedRequest request = ScanEnhancedRequest.builder().build();
            PageIterable<Course> pages = dynamoDbTable.scan(request);

            pages.stream()
                    .flatMap(page -> page.items().stream())
                    .forEach(result::add);
        } catch (Exception e) {
            e.printStackTrace(); // Hoặc log lỗi bằng logger
            throw new RuntimeException("Failed to scan Course table", e);
        }

        return result;
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

//    @PostAuthorize("returnObject.teacherId == authentication.name OR returnObject.studentsId.contains(authentication.name)")
    public Course getCourseDetailById(String courseId) {
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
                .projectionExpression("id, courseName, price, urlAvt, teacherName, category, #s, teacherId, totalReview, countReviews")
                .limit(pageSize != 0 ?pageSize:10);
        if(lastEvaluatedKey!= null && !lastEvaluatedKey.isEmpty()){
            System.out.println("lastEvaluatedKey:"+lastEvaluatedKey);
            requestBuilder .exclusiveStartKey(lastEvaluatedKey);
        }
        ScanRequest request = requestBuilder.build();
        return dynamoDBClient.scan(request);
    }

    public List<Course> search(Map<String, AttributeValue> expressionAttributeValues, String expressionContent, Map<String, String> expressionAttributeNames,int limit, Map<String, AttributeValue> lastEvaluatedKey ) {
        Expression expression = Expression.builder()
                .expression(expressionContent)
                .expressionValues(expressionAttributeValues)
                .expressionNames(expressionAttributeNames)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .limit(limit)
                .exclusiveStartKey(lastEvaluatedKey)
                .build();
        return dynamoDbTable.scan(request).items().stream().toList();
    }
    public List<Course> search(Map<String, AttributeValue> expressionAttributeValues, String expressionContent, Map<String, String> expressionAttributeNames,int limit) {
        Expression expression = Expression.builder()
                .expression(expressionContent)
                .expressionValues(expressionAttributeValues)
                .expressionNames(expressionAttributeNames)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .limit(limit)
                .build();
        return dynamoDbTable.scan(request).items().stream().toList();
    }
    public List<Course> search(Map<String, AttributeValue> expressionAttributeValues, String expressionContent, Map<String, String> expressionAttributeNames ) {
        Expression expression = Expression.builder()
                .expression(expressionContent)
                .expressionValues(expressionAttributeValues)
                .expressionNames(expressionAttributeNames)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();
        return dynamoDbTable.scan(request).items().stream().toList();
    }
    public List<Course> search(Map<String, AttributeValue> expressionAttributeValues, String expressionContent) {
        Expression expression = Expression.builder()
                .expression(expressionContent)
                .expressionValues(expressionAttributeValues)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .build();
        return dynamoDbTable.scan(request).items().stream().toList();
    }
    public List<Course> search( String[] attributesToProject) {
        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .attributesToProject(attributesToProject)
                .build();
        return dynamoDbTable.scan(request).items().stream().toList();
    }
    public List<Course> search(String courseName, String category, Integer rating, String sort, int offset, int size) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        Map<String, String> expressionAttributeNames = new HashMap<>();
        StringBuilder filterExpression = new StringBuilder();

        if (courseName != null && !courseName.isEmpty()) {
            expressionAttributeValues.put(":courseName", AttributeValues.stringValue(courseName));
            filterExpression.append("contains(courseName, :courseName)");
        }

        if (category != null && !category.isEmpty()) {
            expressionAttributeValues.put(":category", AttributeValues.stringValue(category));
            if (filterExpression.length() > 0) filterExpression.append(" AND ");
            filterExpression.append("contains(category, :category)");
        }

        if (rating != null) {
            expressionAttributeValues.put(":rating", AttributeValues.numberValue(rating));
            if (filterExpression.length() > 0) filterExpression.append(" AND ");
            filterExpression.append("totalReview >= :rating");
        }

        expressionAttributeValues.put(":status", AttributeValues.stringValue("OPEN"));
        expressionAttributeNames.put("#s", "status");
        if (filterExpression.length() > 0) filterExpression.append(" AND ");
        filterExpression.append("#s = :status");

        Expression expression = Expression.builder()
                .expression(filterExpression.toString())
                .expressionValues(expressionAttributeValues)
                .expressionNames(expressionAttributeNames)
                .build();

        ScanEnhancedRequest request = ScanEnhancedRequest.builder()
                .filterExpression(expression)
                .limit(offset + size) // Load thêm dữ liệu để có thể bỏ offset
                .build();

        List<Course> results = dynamoDbTable.scan(request)
                .items()
                .stream()
                .skip(offset) // Bỏ qua các phần tử theo offset
                .limit(size) // Giới hạn số lượng
                .collect(java.util.stream.Collectors.toList());

        // TODO: DynamoDB không hỗ trợ sort trong scan. Nếu cần sort chính xác thì phải sort sau khi lấy dữ liệu:
        if (sort != null && !sort.isEmpty()) {
            switch (sort.toLowerCase()) {
                case "price" -> results.sort(Comparator.comparing(Course::getPrice));
                case "rating" -> results.sort(Comparator.comparing(Course::getTotalReview).reversed());
                case "name", "coursename" -> results.sort(Comparator.comparing(Course::getCourseName));
            }
        }

        return results;
    }

}
