package fit.iuh.edu.com.dtos;

import fit.iuh.edu.com.enums.CourseStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseOfTeacherResponse {
    private String id;
    private String courseName;
    private double price;
    private CourseStatus status;
    private int countOrders;
    private int countLectures;
    private int countReviews;
}
