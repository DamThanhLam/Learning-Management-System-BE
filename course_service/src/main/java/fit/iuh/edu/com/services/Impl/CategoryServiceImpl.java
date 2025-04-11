package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.models.Category;
import fit.iuh.edu.com.repositories.CategoryRepository;
import fit.iuh.edu.com.services.BL.CategoryServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryServiceBL {
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.getAll();
    }

    @Override
    public void addCategory(String categoryName) {
        Category category = new Category();
        category.setCategoryName(categoryName);
        if(categoryRepository.get(categoryName) != null){
            return;
        }
        categoryRepository.add(category);
    }
}
