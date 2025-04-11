package fit.iuh.edu.com.services.BL;

import fit.iuh.edu.com.models.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryServiceBL {
    List<Category> getAllCategories();
    void addCategory(String categoryName);
}
