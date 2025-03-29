package fit.iuh.edu.com.services.BL;

import fit.iuh.edu.com.models.User;
import org.springframework.stereotype.Service;

@Service
public interface CognitoServiceBL {
    String createTeacher(User teacher);
}
