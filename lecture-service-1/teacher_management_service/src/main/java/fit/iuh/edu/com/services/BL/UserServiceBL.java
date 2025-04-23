package fit.iuh.edu.com.services.BL;

import fit.iuh.edu.com.models.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

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

    Page<User> getAllAccountByStatus(String status, int page, int pageSize);
}
