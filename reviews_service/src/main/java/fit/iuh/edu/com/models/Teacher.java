package fit.iuh.edu.com.models;


import fit.iuh.edu.com.converter.LocalDateConverter;
import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.enums.Gender;
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

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Teacher {
    private String id;
    private String teacherName;
    private Gender gender;
    private LocalDate birthday;
    private String email;
    private String description;
    private String urlImage;
    private String cvFile;
    private AccountStatus accountStatus;
    private String cognitoId;
    private List<String> groups;
    private List<String> reviewsId;
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
