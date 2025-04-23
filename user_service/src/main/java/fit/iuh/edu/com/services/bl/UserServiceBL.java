package fit.iuh.edu.com.services.bl;

import fit.iuh.edu.com.dtos.UserOwnResponse;
import fit.iuh.edu.com.dtos.UserResponseNoAuth;
import fit.iuh.edu.com.dtos.UserUpdate;
import fit.iuh.edu.com.models.User;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface UserServiceBL {

    public boolean studentRegister(User user);
    public User getUserDetail();
    public UserOwnResponse getUser();
    public User addUser(User user);

    UserResponseNoAuth getUserById(String id);
    User getById(String id);
    User getUserByEmail(String email);

    @PreAuthorize("hasRole('ADMIN')")
    List<User> getUserByRole(String role);

    void update(UserUpdate userUpdate) throws IOException;
}
