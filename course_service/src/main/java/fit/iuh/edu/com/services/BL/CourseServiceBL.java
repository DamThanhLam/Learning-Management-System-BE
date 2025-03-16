package fit.iuh.edu.com.services.BL;


import fit.iuh.edu.com.models.Course;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.List;
import java.util.Map;

@Service
public interface CourseServiceBL {
    Course create(Course course);

//    PaginatedScanList<Course> getAllCoursesByStudentID(int studentID, int limit, Map<String, AttributeValue> lastEvaluatedKey);
//    PaginatedScanList<Course> getAllCoursesByTeacherID(int teacherID, int limit, Map<String, AttributeValue> lastEvaluatedKey);
}
