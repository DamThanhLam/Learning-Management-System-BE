package fit.iuh.edu.com.services.BL;

import fit.iuh.edu.com.models.Review;
import fit.iuh.edu.com.models.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReviewServiceBL {
    public boolean checkDependency(String courseId);
    public Review add(Review obj);

    List<Review> getReviewsByCourseId(String courseId, int review);
    List<Review> getReviewsByCourseId(String courseId);
    List<Review> getReviewsByTeacherId(User teacher);
    public boolean checkBeforeMapReview(String courseId,String reviewId);
    public void mapReviewToTeacher(String reviewId,String courseId);

    List<Review> getReviewsBeforeNow(String courseId);

    Review getReviewedByCourseId(String courseId);
}
