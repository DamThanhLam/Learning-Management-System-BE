package fit.iuh.edu.com.services.BL;

import fit.iuh.edu.com.models.Feedback;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FeedbackServiceBL {
    public List<Feedback> getFeedbacksByTeacherId(String teacherId);
}
