package fit.iuh.edu.com.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;
import java.util.List;

@DynamoDbBean
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private String id;
    private List<String> orderIds;
    private double total;
    private String userId;
    private String userName;
    private String userEmail;
    private String orderInfor;
    private String transactionId;
    private LocalDateTime date;
    private int status; // 0 la thanh toan thanh cong, 2 la thanh toan that bai, 3 la gap loi

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

}
