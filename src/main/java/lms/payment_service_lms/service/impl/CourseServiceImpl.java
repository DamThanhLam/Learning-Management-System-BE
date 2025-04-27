package lms.payment_service_lms.service.impl;

import lms.payment_service_lms.entity.Course;
import lms.payment_service_lms.repositories.CourseRepository;
import lms.payment_service_lms.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    @Override
    public Optional<Course> findById(String courseId) {
        return Optional.ofNullable(courseRepository.courseExist(courseId));
    }


    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }
}
