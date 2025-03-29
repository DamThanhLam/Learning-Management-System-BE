package fit.iuh.edu.com.enums;

public enum AccountStatus {
    REQUIRE("require"),
    REJECT("reject"),
    ACCEPT("accept"),
    LOCKED("locked");
    private String value;
    private AccountStatus(String value) {
        this.value = value;
    }
}
