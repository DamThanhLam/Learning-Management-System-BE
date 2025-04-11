package fit.iuh.edu.com.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private String id;
    private String categoryName;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
