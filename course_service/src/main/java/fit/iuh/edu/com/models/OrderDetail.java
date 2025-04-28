package fit.iuh.edu.com.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@DynamoDbBean
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
    private String id;
    private String courseId;
    private String userId;
    private LocalDateTime date;
    private double price;


    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }


    public void setId(String id) {
        if (id == null || id.isEmpty()) {
            this.id = "ORD-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)
                    + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        this.id = id;
    }
}

