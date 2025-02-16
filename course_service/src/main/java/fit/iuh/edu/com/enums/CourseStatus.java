package fit.iuh.edu.com.enums;

public enum CourseStatus {
    OPEN_REGISTER("open_register"),
    CLOSED_REGISTER("closed_register"),
    CANCELED("canceled"),
    ONGOING("ongoing"),
    NOT_STARTED("not started"),
    COMPLETED("completed"),
    DELETED("deleted"),
    OPEN("open");
    final String status;
    CourseStatus(String status) {
        this.status = status;
    }
}
