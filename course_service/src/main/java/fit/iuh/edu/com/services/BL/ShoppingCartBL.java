package fit.iuh.edu.com.services.BL;

import fit.iuh.edu.com.models.Course;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ShoppingCartBL {
    public void addCourse(String courseId);
    public void removeCourse(String courseId);
    public List<Course> getShoppingCart();
}
