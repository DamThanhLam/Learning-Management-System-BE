package fit.iuh.edu.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Feedback {
    private String id;
    private String userId;
    private String teacherId;
    private String content;
    private String title;
    private List<String> urlImages;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
