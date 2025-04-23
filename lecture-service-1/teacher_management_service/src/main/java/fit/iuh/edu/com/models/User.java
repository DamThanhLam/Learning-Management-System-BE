package fit.iuh.edu.com.models;

import fit.iuh.edu.com.converter.GenderConverter;
import fit.iuh.edu.com.converter.LocalDateConverter;
import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;
import java.util.List;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
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
    private String cognitoId;
    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }
    @DynamoDbConvertedBy(LocalDateConverter.class)
    public LocalDate getBirthday() {
        return birthday;
    }

    @DynamoDbConvertedBy(GenderConverter.class)
    public Gender getGender() {
        return gender;
    }
}
