package fit.iuh.edu.com.services.Impl;


import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.repositories.CourseRepository;
import fit.iuh.edu.com.repositories.UserRepository;
import fit.iuh.edu.com.services.BL.ShoppingCartBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ShoppingCartImpl implements ShoppingCartBL {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void addCourse(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.getUserById(authentication.getName());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (courseRepository.courseExist(courseId)!= null) {
            if (user.getShoppingList() == null) {
                user.setShoppingList(new HashSet<>());
            }
            user.getShoppingList().add(courseId);
            userRepository.update(user);
        }
    }

    @Override
    public void removeCourse(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.getUserById(authentication.getName());

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        if (courseRepository.courseExist(courseId)!= null) {
            if (user.getShoppingList() == null) {
                user.setShoppingList(new HashSet<>());
            }
            user.getShoppingList().remove(courseId);
            userRepository.update(user);
        }
    }

    @Override
    public List<Course> getShoppingCart() {
        List<Course> result = new ArrayList<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.getUserById(authentication.getName());

        if (user == null) {
            throw new RuntimeException("User not found");
        }
        Set<String> shoppingList = user.getShoppingList();
        if (shoppingList != null) {
            for (String courseId : shoppingList) {
                Course course = courseRepository.getCourseDetailById(courseId);
                if(course != null){
                    result.add(course);
                }
            }
        }
        return result;
    }
}
