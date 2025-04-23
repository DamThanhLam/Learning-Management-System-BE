package fit.iuh.edu.com.enums;

public enum Role {
    ADMIN(1),
    STUDENT(2),
    TEACHER(2);
    private int value;
    Role(int value) {
        this.value = value;
    }
}
