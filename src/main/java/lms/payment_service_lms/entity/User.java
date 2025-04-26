package fit.iuh.edu.com.models;

import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.enums.Gender;
import fit.iuh.edu.com.converter.LocalDateConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private String id;
    private String userName;
    private String email;
    private String phoneNumber;
    private LocalDate birthday;
    private Gender gender;
    private List<String> groups;
    private List<String> reviewsId;
    private String description;
    private String urlImage;
    private String cvFile;
    private AccountStatus accountStatus;
    private Map<String, String> contacts;
    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }
    @DynamoDbConvertedBy(LocalDateConverter.class)
    public LocalDate getBirthday() {
        return birthday;
    }

}
