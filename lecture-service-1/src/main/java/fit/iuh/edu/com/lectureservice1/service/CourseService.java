package fit.iuh.edu.com.lectureservice1.service;

import fit.iuh.edu.com.lectureservice1.model.Course;
import fit.iuh.edu.com.lectureservice1.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    public Course getCourseCourseIdAndUserId(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication instanceof JwtAuthenticationToken jwtAuth){
            String userId = jwtAuth.getToken().getSubject();
            System.out.println("userId:"+userId);
            System.out.println("courseId:"+courseId);
            return courseRepository.getCourseCourseIdAndUserId(userId, courseId);

        }
        return null;
    }
}
