package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.models.Feedback;
import fit.iuh.edu.com.repositories.FeedbackRepository;
import fit.iuh.edu.com.services.BL.FeedbackServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackServiceBL {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public List<Feedback> getFeedbacksByTeacherId(String teacherId) {
        return feedbackRepository.findByTeacherId(teacherId);
    }
}
