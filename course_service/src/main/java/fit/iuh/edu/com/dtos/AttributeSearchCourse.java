package fit.iuh.edu.com.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AttributeSearchCourse {
    String courseName;
    int pageSize;
    String lastEvaluateKey;
    public AttributeSearchCourse(String courseName, int pageSize, String lastEvaluateKey) {
        this.courseName = courseName;
        this.pageSize = pageSize;
        this.lastEvaluateKey = lastEvaluateKey;
        if (this.pageSize <= 0) {
            this.pageSize = 10; // Giá trị mặc định
        }
    }
}
