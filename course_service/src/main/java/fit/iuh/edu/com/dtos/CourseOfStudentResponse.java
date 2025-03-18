package fit.iuh.edu.com.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseOfStudentResponse {
    private String id;
    private String courseName;
    private String teacherName;
    private String teacherId;
    private float totalReview;
    private int countReviews;
    private double price;
}
