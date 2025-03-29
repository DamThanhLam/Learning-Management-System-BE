package fit.iuh.edu.com.services.bl;

import fit.iuh.edu.com.dtos.UserOwnResponse;
import fit.iuh.edu.com.dtos.UserResponseNoAuth;
import fit.iuh.edu.com.models.User;
import org.springframework.stereotype.Service;

@Service
public interface UserServiceBL {

    public boolean studentRegister(User user);
    public User getUserDetail();
    public UserOwnResponse getUser();
    public User addUser(User user);

    UserResponseNoAuth getUserById(String id);
}
