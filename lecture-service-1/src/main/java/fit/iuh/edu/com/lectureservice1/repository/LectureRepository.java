package fit.iuh.edu.com.lectureservice1.repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import fit.iuh.edu.com.lectureservice1.dto.PaginatedLecturesDTO;
import fit.iuh.edu.com.lectureservice1.model.Lecture;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Repository
public class LectureRepository {
    private final DynamoDbTable<Lecture> lectureTable;

    /*
     * Spring injects the dynamoDbClient bean, on which we rely to make the enhanced
     * client
     */
    public LectureRepository(DynamoDbClient dynamoDbClient) {
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        // get the DynamoDB table and map it to the Lecture model.
        this.lectureTable = enhancedClient.table("Lectures", TableSchema.fromBean(Lecture.class));
    }

    // Create or update lecture
    public Lecture save(Lecture lecture) {
        // Using PutItem to create or update the lecture in DynamoDB
        lectureTable.putItem(lecture);
        return lecture;
    }

    // Find a lecture by its ID (using courseId and id)
    public Lecture findById(String courseId, int chapter) {
        // Create the primary key for the query (composite key)
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                .key(k -> k.partitionValue(courseId).sortValue(chapter)) // Using the composite key (courseId + id)
                .build();
        return lectureTable.getItem(request); // Get the lecture by its composite key
    }

    // Find lectures by courseId
    public List<Lecture> findByCourseId(String courseId) {
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId))) // Query by courseId
                .build();

        // Get results from DynamoDB
        SdkIterable<Page<Lecture>> results = lectureTable.query(request);

        // Stream results and collect them into a list
        return StreamSupport.stream(results.spliterator(), false).flatMap(page -> page.items().stream())
                .collect(Collectors.toList());
    }
    
    // Find lectures by courseId (paginated, sorted by chapterOrderIndex)
    // Find lectures by courseId (paginated, sorted by chapterOrderIndex)
    public PaginatedLecturesDTO findByCourseId(String courseId, int limit, String lastEvaluatedId, String lastEvaluatedChapterOrderIndex) {
        // Get the current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities().toString();

        // Check if the user is a teacher or student
        boolean isTeacher = role.contains("ROLE_TEACHER");
        boolean isStudent = role.contains("ROLE_STUDENT");

        // Reference the LSI
        DynamoDbIndex<Lecture> chapterOrderIndexLSI = lectureTable.index("chapterOrderIndex-index");

        // Build the query request
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId))) // Query by courseId
                .limit(limit)
                .scanIndexForward(true); // Ascending order based on chapterOrderIndex

        // If the user is a student, only return published lectures
        if (true) {
            // Use the Expression class to filter the status
            Expression filterExpression = Expression.builder()
                .expression("status = :published")
                .expressionValues(Map.of(":published", AttributeValue.builder().s("PUBLISHED").build()))
                .build();

            requestBuilder.filterExpression(filterExpression);
        }

        // Set exclusiveStartKey for pagination, if provided
        if (lastEvaluatedId != null && lastEvaluatedChapterOrderIndex != null) {
            Map<String, AttributeValue> exclusiveStartKey = new HashMap<>();
            exclusiveStartKey.put("courseId", AttributeValue.builder().s(courseId).build());
            exclusiveStartKey.put("id", AttributeValue.builder().s(lastEvaluatedId).build());
            exclusiveStartKey.put("chapterOrderIndex", AttributeValue.builder().s(lastEvaluatedChapterOrderIndex).build());
            requestBuilder.exclusiveStartKey(exclusiveStartKey);
        }

        // Execute the query
        Iterator<Page<Lecture>> results = chapterOrderIndexLSI.query(requestBuilder.build()).iterator();

        // If no results
        if (!results.hasNext()) {
            return new PaginatedLecturesDTO(Collections.emptyList(), null, null);
        }

        // Get the first page (only one page since we use limit)
        Page<Lecture> page = results.next();
        List<Lecture> lectures = page.items();
        Map<String, AttributeValue> lastKey = page.lastEvaluatedKey();

        String nextId = null;
        String nextChapterOrderIndex = null;

        // Prepare pagination token for next call
        if (lastKey != null && lastKey.containsKey("id") && lastKey.containsKey("chapterOrderIndex")) {
            nextId = lastKey.get("id").s();
            nextChapterOrderIndex = lastKey.get("chapterOrderIndex").s();
        }

        return new PaginatedLecturesDTO(lectures, nextId, nextChapterOrderIndex);
    }
}

//    // Find lectures by courseId (sorted by chapterOrderIndex using LSI)
//    public PaginatedLecturesDTO findByCourseId(String courseId, int limit, String lastEvaluatedId, String lastEvaluatedChapterOrderIndex) {
//        // Reference the LSI
//        DynamoDbIndex<Lecture> chapterOrderIndexLSI = lectureTable.index("chapterOrderIndex-index");
//
//        // Build the query request
//        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
//            .queryConditional(QueryConditional.keyEqualTo(k -> k.partitionValue(courseId))) // Query by courseId
//            .limit(limit)
//            .scanIndexForward(true); // Ascending order based on chapterOrderIndex
//
//        // Set exclusiveStartKey for pagination, if provided
//        if (lastEvaluatedId != null && lastEvaluatedChapterOrderIndex != null) {
//            Map<String, AttributeValue> exclusiveStartKey = new HashMap<>();
//            exclusiveStartKey.put("courseId", AttributeValue.builder().s(courseId).build());
//            exclusiveStartKey.put("id", AttributeValue.builder().s(lastEvaluatedId).build());
//            exclusiveStartKey.put("chapterOrderIndex", AttributeValue.builder().s(lastEvaluatedChapterOrderIndex).build());
//            requestBuilder.exclusiveStartKey(exclusiveStartKey);
//        }
//
//        // Execute the query
//        Iterator<Page<Lecture>> results = chapterOrderIndexLSI.query(requestBuilder.build()).iterator();
//
//        // If no results
//        if (!results.hasNext()) {
//            return new PaginatedLecturesDTO(Collections.emptyList(), null, null);
//        }
//
//        // Get the first page (only one page since we use limit)
//        Page<Lecture> page = results.next();
//        List<Lecture> lectures = page.items();
//        Map<String, AttributeValue> lastKey = page.lastEvaluatedKey();
//
//        String nextId = null;
//        String nextChapterOrderIndex = null;
//
//        // Prepare pagination token for next call
//        if (lastKey != null && lastKey.containsKey("id") && lastKey.containsKey("chapterOrderIndex")) {
//            nextId = lastKey.get("id").s();
//            nextChapterOrderIndex = lastKey.get("chapterOrderIndex").s();
//        }
//
//        return new PaginatedLecturesDTO(lectures, nextId, nextChapterOrderIndex);
//    }


