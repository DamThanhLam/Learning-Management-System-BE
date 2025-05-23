package fit.iuh.edu.com.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import fit.iuh.edu.com.converter.LocalDateConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;
    private String userName;
    private String email;
    private LocalDate birthday;
    private String gender;
    private List<String> groups;
    private Set<String> shoppingList;
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
