package fit.iuh.edu.com.lectureservice1.repository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import fit.iuh.edu.com.lectureservice1.dto.PaginatedLecturesDTO;
import fit.iuh.edu.com.lectureservice1.model.Lecture;
import org.springframework.stereotype.Repository;

import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
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
        this.lectureTable = enhancedClient.table("Lecture", TableSchema.fromBean(Lecture.class));
    }

    // Create or update lecture
    public Lecture save(Lecture lecture) {
        // Using PutItem to create or update the lecture in DynamoDB
        lectureTable.putItem(lecture);
        return lecture;
    }

    // Find a lecture by its ID (using courseId and id)
    public Lecture findById(String id) {
        // Create the primary key for the query (composite key)
        GetItemEnhancedRequest request = GetItemEnhancedRequest.builder()
                .key(k -> k.partitionValue(id)) // Using the composite key (courseId + id)
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

    public PaginatedLecturesDTO findByCourseId(
            String courseId,
            String  statusFilter
    ) {
        // 1) Build just your non‐key filters
        Map<String,String>         names  = new HashMap<>();
        Map<String,AttributeValue> values = new HashMap<>();
        List<String>               filters = new ArrayList<>();
        // 2) Build your key‐condition (partitionKey = :courseId)
        values.put( ":courseId", AttributeValue.builder().s(courseId).build());
        names.put("#courseId","courseId");
        filters.add("#courseId = :courseId");

        if (statusFilter != null && !statusFilter.isEmpty()) {
            filters.add("#status = :status");
            names.put("#status","status");
            values.put(":status", AttributeValue.builder().s(statusFilter).build());
        }

        Expression filterExpression = Expression.builder()
                .expression(String.join(" AND ", filters))
                .expressionNames(names)
                .expressionValues(values)
                .build();
        ScanEnhancedRequest scanEnhancedRequest = ScanEnhancedRequest.builder()
                .filterExpression(filterExpression)
                .build();

        return new PaginatedLecturesDTO(lectureTable.scan(scanEnhancedRequest).items().stream().toList(), null, null);
    }



    public Lecture findByCourseIdAndChapter(String courseId, Integer chapter) {
        // Lấy index secondary
        DynamoDbIndex<Lecture> index = lectureTable.index("courseId-chapter-index");

        // Xây điều kiện query: partition = courseId, sort = chapter
        QueryConditional condition = QueryConditional.keyEqualTo(
                Key.builder()
                        .partitionValue(courseId)
                        .sortValue(chapter)
                        .build()
        );
        Optional<Lecture> optionalLecture = index.query(r -> r.queryConditional(condition))
                .stream()
                .flatMap(page -> page.items().stream())
                .findFirst();
        // Thực hiện truy vấn, lấy item đầu tiên nếu có
        if (optionalLecture.isPresent()) {
            System.out.println(optionalLecture.get());
            return optionalLecture.get();
        } else {
            return null;
        }

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


