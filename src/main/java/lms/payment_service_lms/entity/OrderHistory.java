package lms.payment_service_lms.entity;

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
public class OrderHistory {
    private String id;
    private List<String> listOrders;
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

    public OrderHistory(String id, List<String> listOrders, double total, String userId, String userName, String userEmail, String orderInfor, String transactionId, LocalDateTime date, int status) {
        this.id = id;
        this.listOrders = listOrders;
        this.total = total;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.orderInfor = orderInfor;
        this.transactionId = transactionId;
        this.date = date;
        this.status = status;
    }
}
