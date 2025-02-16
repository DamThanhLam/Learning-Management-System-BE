package fit.iuh.edu.com.enums;

public enum CourseStatus {
    OPEN_REGISTER("OPEN_REGISTER"),
    CLOSED_REGISTER("CLOSED_REGISTER"),
    CANCELED("CANCELED"),
    ONGOING("ONGOING"),
    NOT_STARTED("NOT_STARTED"),
    COMPLETED("COMPLETED"),
    DELETED("DELETED"),
    OPEN("OPEN");
    private final String status;
    CourseStatus(String status) {
        this.status = status;
    }

}
