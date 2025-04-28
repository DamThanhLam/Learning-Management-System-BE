package fit.iuh.edu.com.services.BL;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderDetailBL {
    public boolean checkCoursePurchased(String courseId);
}
