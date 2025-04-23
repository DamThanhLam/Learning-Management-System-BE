package fit.iuh.edu.com.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailMessage implements Serializable {
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> templateData;
}
