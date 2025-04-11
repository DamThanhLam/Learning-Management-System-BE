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
    List<Course> getCoursesByStudentID(String studentID, int limit, Map<String, AttributeValue> lastEvaluatedKey);

    List<Course> getCoursesByTeacherID(String name, int pageSize, Map<String, AttributeValue> lastEvaluatedKey);

    Course getCourseDetailById(String courseId);
    boolean checkCourseBeforeUpdate(String courseId);
    Course updateCourse(Course course);

    List<Course> getCoursesByCourseNameOrCategory(String courseName, String category, int pageSize, Map<String, AttributeValue> lastEvaluatedKey);


}
