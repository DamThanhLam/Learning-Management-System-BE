package fit.iuh.edu.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Account {
    private String id;
    private String email;
    private String password;
    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
