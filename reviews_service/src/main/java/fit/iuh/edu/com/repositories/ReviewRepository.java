package fit.iuh.edu.com.repositories;

import fit.iuh.edu.com.models.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.*;

@Repository
public class ReviewRepository{
    @Autowired
    private DynamoDbClient dynamoDBClient;
    private final DynamoDbTable<Review> dynamoDbTable;
    public ReviewRepository(){
        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDBClient).build();
        dynamoDbTable = enhancedClient.table("Review", TableSchema.fromBean(Review.class));
    }
    public Review addReview(Review review){
        review.setId(java.util.UUID.randomUUID().toString());
        review.setCreatedAt(Instant.now());
        dynamoDbTable.putItem(review);
        return review;
    }

    public List<Review> getReviewsByCourseId(String courseId) {
        Map<String, AttributeValue> expressValue = new HashMap<String, AttributeValue>();
        expressValue.put(":courseId", AttributeValue.builder().s(courseId).build());
        Expression expression = Expression
                .builder()
                .expression("courseId = :courseId")
                .expressionValues(expressValue)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest
                .builder()
                .filterExpression(expression)
                .build();
        return dynamoDbTable.scan(request).items().stream().toList();
    }
    public List<Review> getReviewsBeforeNow(String courseId) {
        DynamoDbIndex<Review> gsi = dynamoDbTable.index("courseId-createdAt-index");
        Instant now = Instant.now();
        QueryConditional cond = QueryConditional.sortLessThan(
                Key.builder()
                        .partitionValue(courseId)
                        .sortValue(now.toString())
                        .build()
        );
        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(cond)
                .scanIndexForward(false)
                .build();

        SdkIterable<Page<Review>> pages = gsi.query(request);
        List<Review> result = new ArrayList<>();
        for (Page<Review> page : pages) {
            result.addAll(page.items());
        }
        return result;
    }


    public List<Review> getReviewsByCourseId(String courseId, int review) {
        Map<String, AttributeValue> expressValue = new HashMap<String, AttributeValue>();
        expressValue.put(":courseId", AttributeValue.builder().s(courseId).build());
        expressValue.put(":review", AttributeValue.builder().n(String.valueOf(review)).build());

        Expression expression = Expression
                .builder()
                .expression("courseId = :courseId AND review = :review")
                .expressionValues(expressValue)
                .build();
        ScanEnhancedRequest request = ScanEnhancedRequest
                .builder()
                .filterExpression(expression)
                .build();
        return dynamoDbTable.scan(request).items().stream().toList();
    }

    public Review find(String reviewId, String courseId) {
        return dynamoDbTable.getItem(Key.builder().partitionValue(reviewId).sortValue(courseId).build());
    }
    public Review find(String reviewId) {
        QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder().partitionValue(reviewId).build()
        );

        QueryEnhancedRequest enhancedRequest = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .build();

        return dynamoDbTable.query(enhancedRequest)
                .items()
                .stream()
                .findFirst()
                .orElse(null);
    }

    public Review getReviewedByCourseIdAndUserId(String courseId, String userId) {
        DynamoDbIndex<Review> index = dynamoDbTable.index("courseId-createdAt-index");
        QueryConditional cond = QueryConditional.keyEqualTo(
                Key.builder()
                        .partitionValue(userId)
                        .sortValue(courseId)
                        .build()
        );
        return Objects.requireNonNull(index.query(cond).stream().findFirst().orElse(null)).items().stream().findFirst().orElse(null);
    }
}
