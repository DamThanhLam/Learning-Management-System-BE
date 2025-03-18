package fit.iuh.edu.com.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lecture {
    private String id;
    private String courseId;
    private String title;
    private String urlFile;
    private String urlVideo;
    private String urlThumbnail;
    private String description;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
