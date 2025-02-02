package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.services.BL.CourseServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;


@Primary
@Service
public class CourseServiceImpl implements CourseServiceBL {
    @Value("${aws.region}")
    private String region;
    DynamoDbClient dynamoDBClient(){
        return DynamoDbClient.builder()
                .region(Region.of(region))
                .credentialsProvider(ProfileCredentialsProvider.create("LamDEV-Profile"))
                .build();
    }

    @Override
    public Course create(Course course) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient()).build();
        DynamoDbTable<Course> courseTable = enhancedClient.table("Course", TableSchema.fromBean(Course.class));
        course.setId(java.util.UUID.randomUUID().toString());
        courseTable.putItem(course);
        System.out.println("Course added successfully: " + course.getCourseName());
        return course;
    }

    @Override
    public Course update(Course course) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient()).build();
        DynamoDbTable<Course> courseTable = enhancedClient.table("Course", TableSchema.fromBean(Course.class));
        courseTable.updateItem(course);
        return course;
    }

    @Override
    public void delete(Course course) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient()).build();
        DynamoDbTable<Course> courseTable = enhancedClient.table("Course", TableSchema.fromBean(Course.class));
        course.setStatus(CourseStatus.DELETED);
        courseTable.updateItem(course);
    }
    @Override
    public ScanResponse findByCourseName(String courseName, Map<String, AttributeValue> lastEvaluatedKey, int pageSize) {
        DynamoDbClient dynamoDbClient = dynamoDBClient();

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":courseName", AttributeValues.stringValue(courseName));

        ScanRequest query = ScanRequest
                .builder()
                .tableName("Course")
                .filterExpression("contains(courseName, :courseName)")
                .expressionAttributeValues(expressionAttributeValues)
                .exclusiveStartKey(lastEvaluatedKey)
                .projectionExpression("id, courseName, description, price, createTime, updateTime, openTime, closeTime, startTime, completeTime, urlAvt, teacherName, numberMinimum, numberMaximum, numberCurrent")
                .limit(pageSize)
                .build();

        ScanResponse response = dynamoDbClient.scan(query);

        return response;
    }


}
