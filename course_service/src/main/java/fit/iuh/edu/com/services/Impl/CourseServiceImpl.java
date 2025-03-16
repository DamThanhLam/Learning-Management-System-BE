package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.controllers.CourseController;
import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.repositories.CourseRepository;
import fit.iuh.edu.com.services.BL.CourseServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.prepost.PostAuthorize;
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

    @Override
    public Course create(Course course) {
        return courseRepository.create(course);
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
//    public List<Course> mappingCoursesFromScanResponse(ScanResponse response){
//        List<Course> courses = new ArrayList<>();
//        for (Map<String, AttributeValue> item: response.items()){
//            Course course = new Course();
//
//            // Ánh xạ các trường từ item vào đối tượng Course
//            if (item.containsKey("id")) {
//                course.setId(item.get("id").s());  // Giả sử "id" là chuỗi
//            }
//            if (item.containsKey("courseName")) {
//                course.setCourseName(item.get("courseName").s());
//            }
//            if (item.containsKey("description")) {
//                course.setDescription(item.get("description").s());
//            }
//            if (item.containsKey("price")) {
//                course.setPrice(Double.parseDouble(item.get("price").n()));
//            }
//            if (item.containsKey("createTime")) {
//                course.setCreateTime(LocalDateTime.parse(item.get("createTime").s()));
//            }
//            if (item.containsKey("updateTime")) {
//                course.setUpdateTime(LocalDateTime.parse(item.get("updateTime").s()));
//            }
//            if (item.containsKey("openTime")) {
//                course.setOpenTime(LocalDateTime.parse(item.get("openTime").s()));
//            }
//            if (item.containsKey("closeTime")) {
//                course.setCloseTime(LocalDateTime.parse(item.get("closeTime").s()));
//            }
//            if (item.containsKey("startTime")) {
//                course.setStartTime(LocalDateTime.parse(item.get("startTime").s()));
//            }
//
//            if (item.containsKey("completeTime")) {
//                course.setCompleteTime(LocalDateTime.parse(item.get("completeTime").s()));
//            }
//            if (item.containsKey("urlAvt")) {
//                course.setUrlAvt(item.get("urlAvt").s());
//            }
//            if (item.containsKey("teacherName")) {
//                course.setTeacherName(item.get("teacherName").s());
//            }
//            if (item.containsKey("numberMinimum")) {
//                course.setNumberMinimum(Integer.parseInt(item.get("numberMinimum").n()));
//            }
//            if (item.containsKey("numberMaximum")) {
//                course.setNumberMaximum(Integer.parseInt(item.get("numberMaximum").n()));
//            }
//            if (item.containsKey("numberCurrent")) {
//                course.setNumberCurrent(Integer.parseInt(item.get("numberCurrent").n()));
//            }
//            if(item.containsKey("category")){
//                course.setCategory(item.get("category").s());
//            }
//            if(item.containsKey("status")){
//                course.setStatus(CourseStatus.valueOf(item.get("status").s()));
//            }
//            if(item.containsKey("teacherId")){
//                course.setTeacherId(item.get("teacherId").s());
//            }
//            if(item.containsKey("studentsId")){
//                course.setStudentsId(Arrays.asList(item.get("studentsId").s()));
//            }
//            courses.add(course);
//        }
//        return courses;
//    }

}
