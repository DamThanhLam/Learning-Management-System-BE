package fit.iuh.edu.com.services.BL;

import fit.iuh.edu.com.models.User;
import org.springframework.stereotype.Service;

@Service
public interface UserServiceBL {
    public User getUser(String userId);
    public boolean beforeAddTeacher(String email, String phoneNumber);
    public User create(User teacherTemp);
    public User getTeacherByCognitoId(String cognitoId);

    public void lockAndSendEmailTeacherAccount(User teacher);

    User findById(String id);

    void delete(User teacherTemp);

    void update(User teacher);
}
