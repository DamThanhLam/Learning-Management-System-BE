package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.models.Review;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.repositories.CourseRepository;
import fit.iuh.edu.com.repositories.ReviewRepository;
import fit.iuh.edu.com.repositories.UserRepository;
import fit.iuh.edu.com.services.BL.ReviewServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ReviewServiceImpl implements ReviewServiceBL {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean checkDependency(String courseId) {
        System.out.println(courseId);
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());

        return courseRepository.getCourseByStudentIDAndCourseID(SecurityContextHolder.getContext().getAuthentication().getName(), courseId) != null;
    }

    @Override
    public Review add(Review review) {
        return reviewRepository.addReview(review);
    }

    @Override
    public List<Review> getReviewsByCourseId(String courseId, int review) {
        return reviewRepository.getReviewsByCourseId(courseId, review);
    }

    @Override
    public List<Review> getReviewsByCourseId(String courseId) {
        return reviewRepository.getReviewsByCourseId(courseId);
    }
    @Override
    public List<Review> getReviewsByTeacherId(User teacher) {
        List<Review> reviews = new ArrayList<>();
        teacher.getReviewsId().forEach(item ->{
            reviews.add(reviewRepository.find(item));
        });
        return reviews;
    }

    @Override
    public boolean checkBeforeMapReview(String courseId, String reviewId) {
        Course course = courseRepository.courseExist(courseId);
        Review review = reviewRepository.find(reviewId,courseId);
        if (course == null || review == null) {
            return false;
        }
        return Objects.equals(course.getId(), review.getCourseId()) && Objects.equals(course.getTeacherId(), SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Override
    public void mapReviewToTeacher(String reviewId,String courseId) {
        Review review = reviewRepository.find(reviewId, courseId);
        User user = userRepository.getUserById(SecurityContextHolder.getContext().getAuthentication().getName());
        List<String> reviewsId = user.getReviewsId();
        if(reviewsId == null){
            reviewsId = new ArrayList<>();
        }
        reviewsId.add(review.getId());
        user.setReviewsId(reviewsId);
        userRepository.updateUser(user);
    }
}
