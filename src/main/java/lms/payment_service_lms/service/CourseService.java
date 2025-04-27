package lms.payment_service_lms.service;

import lms.payment_service_lms.entity.Course;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    Optional<Course> findById(String id);
    List<Course> findAll();
}
