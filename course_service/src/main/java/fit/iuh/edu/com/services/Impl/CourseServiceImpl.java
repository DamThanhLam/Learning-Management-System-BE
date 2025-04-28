package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.controllers.CourseController;
import fit.iuh.edu.com.enums.CourseLevel;
import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.models.Order;
import fit.iuh.edu.com.repositories.CourseRepository;
import fit.iuh.edu.com.repositories.OrderRepository;
import fit.iuh.edu.com.services.BL.CourseServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.internal.AttributeValues;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;


@Primary
@Service
public class CourseServiceImpl implements CourseServiceBL {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Override
    public Course create(Course course) {
        return courseRepository.create(course);
    }

    @Override
    public List<Course> getCoursesByStudentID(String studentID) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Course> courses = new ArrayList<>();
        List<Order> orders = orderRepository.getOrdersByUserId(authentication.getName());
        orders.forEach(order -> {
            if(order.getStatus() ==1){
                order.getOrderIds().forEach(orderDetailId->{
                    Course course = courseRepository.courseExist(orderDetailId);
                    courses.add(course);
                });
            }

        });

        return courses;
    }

    @Override
    public List<Course> getCoursesByTeacherID(String teacherId, int pageSize, Map<String, AttributeValue> lastEvaluatedKey) {
        ScanResponse scanResponse = courseRepository.getCoursesByTeacherID(teacherId,pageSize,lastEvaluatedKey);
        return mappingCoursesFromScanResponse(scanResponse);
    }

    @Override
    public Course getCourseDetailById(String courseId) {
        return courseRepository.getCourseDetailById(courseId);
    }

    @Override
    public boolean checkCourseBeforeUpdate(String courseId) {
        Course course = courseRepository.courseExist(courseId);
        return course != null;
    }

    @Override
    public Course updateCourse(Course course) {
        return courseRepository.updateCourse(course);
    }

    @Override
    public List<Course> getCoursesByCourseNameOrCategory(String courseName, String category, int pageSize, Map<String, AttributeValue> lastEvaluatedKey) {
        ScanResponse scanResponse = courseRepository.getCoursesByCourseNameOrCategory(courseName,category,pageSize,lastEvaluatedKey);
        return mappingCoursesFromScanResponse(scanResponse);
    }

    @Override
    public List<Course> getCoursesCommon() {
        List<Course> courses =  courseRepository.search(new String[]{"id", "totalReview", "countReviews"});
        courses = courses.stream()
                .sorted(Comparator.comparing(Course::getTotalReview).reversed())
                .sorted(Comparator.comparing(Course::getCountReviews).reversed()).limit(20).toList();
        return courses;
    }

    @Override
    public List<Course> searchCourses(String courseName, String category, Integer rating, String sort, int offset, int size) {
        // Viết truy vấn theo offset & limit
        // Có thể dùng JPA hoặc DynamoDB tùy bạn
        // sort = "asc" / "desc"
        // rating có thể là filter số sao
        return courseRepository.search(courseName, category, rating, sort, offset, size);
    }



    //
//    @Override
//    public List<Course> findByCourseName(String courseName, Map<String, AttributeValue> lastEvaluatedKey, int pageSize) {
//        return mappingCoursesFromScanResponse(courseRepository.findByCourseName(courseName, lastEvaluatedKey, pageSize));
//    }
//
//    @Override
//    public List<Course> findOwnOrStudentIdByCourseName(String username, String courseName, Map<String, AttributeValue> lastEvaluatedKey, int pageSize) {
//        ScanResponse scanResponse = courseRepository.findOwnOrStudentIdByCourseName(username,courseName,lastEvaluatedKey,pageSize);
//        return mappingCoursesFromScanResponse(scanResponse);
//    }
//
//    @PostAuthorize("returnObject.teacherId == authentication.name OR returnObject.studentsId.contains(authentication.name)")
//    @Override
//    public Course getCourseDetailById(String courseId) {
//        return courseRepository.getCourseDetailById(courseId);
//    }
//
    public List<Course> mappingCoursesFromScanResponse(ScanResponse response){
        List<Course> courses = new ArrayList<>();
        for (Map<String, AttributeValue> item: response.items()){
            Course course = new Course();

            // Ánh xạ các trường từ item vào đối tượng Course
            if (item.containsKey("id")) {
                course.setId(item.get("id").s());  // Giả sử "id" là chuỗi
            }
            if (item.containsKey("courseName")) {
                course.setCourseName(item.get("courseName").s());
            }
            if (item.containsKey("description")) {
                course.setDescription(item.get("description").s());
            }
            if (item.containsKey("price")) {
                course.setPrice(Double.parseDouble(item.get("price").n()));
            }

            if (item.containsKey("urlAvt")) {
                course.setUrlAvt(item.get("urlAvt").s());
            }
            if (item.containsKey("teacherName")) {
                course.setTeacherName(item.get("teacherName").s());
            }

            if(item.containsKey("category")){
                course.setCategory(item.get("category").s());
            }
            if(item.containsKey("status")){
                course.setStatus(CourseStatus.valueOf(item.get("status").s()));
            }
            if(item.containsKey("teacherId")){
                course.setTeacherId(item.get("teacherId").s());
            }
            if(item.containsKey("level")){
                System.out.println(item.get("level").s());
                course.setLevel(CourseLevel.valueOf(item.get("level").s()));
            }
            if(item.containsKey("totalReview")){
                try{
                    course.setTotalReview(Float.parseFloat(item.get("totalReview").n()));
                }catch (Exception e){
                    course.setTotalReview(0);
                }
            }
            if(item.containsKey("countOrders")){
                course.setCountOrders(Integer.parseInt(item.get("countOrders").n()));
            }
            if(item.containsKey("countLectures")){
                course.setCountLectures(Integer.parseInt(item.get("countLectures").n()));
            }
            if(item.containsKey("countReviews")){
                course.setCountReviews(Integer.parseInt(item.get("countReviews").n()));
            }

            courses.add(course);
        }
        return courses;
    }

}
