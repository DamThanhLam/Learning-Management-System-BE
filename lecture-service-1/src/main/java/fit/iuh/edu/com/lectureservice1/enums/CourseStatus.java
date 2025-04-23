package fit.iuh.edu.com.lectureservice1.enums;

public enum CourseStatus {
    DRAFT("draft"),
    OPEN("open");
    private final String status;
    CourseStatus(String status) {
        this.status = status;
    }

}
