package fit.iuh.edu.com.lectureservice1.service;

import fit.iuh.edu.com.lectureservice1.model.Course;
import fit.iuh.edu.com.lectureservice1.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;
    public Course getCourseByCourseIdAndTeacherId(String courseId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return courseRepository.getCourseCourseIdAndTeacherId(authentication.getName(), courseId);
    }

//    public boolean getCourseByCourseIdAndStudentId(String courseId) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    }
}
