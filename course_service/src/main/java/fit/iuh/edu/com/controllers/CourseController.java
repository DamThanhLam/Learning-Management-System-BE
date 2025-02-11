package fit.iuh.edu.com.controllers;

import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.services.BL.BucketServiceBL;
import fit.iuh.edu.com.services.Impl.BucketServiceImpl;
import fit.iuh.edu.com.services.Impl.CourseServiceImpl;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("api/v1/course")
public class CourseController {
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList("image/jpeg", "image/png");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_SIZE = 5 * 1024 * 1024; // 5MB
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Autowired
    private BucketServiceImpl bucketServiceBL;
    @Autowired
    private CourseServiceImpl courseServiceImpl;
    /*
    add:
    - check id teacher and get teacher name(wait finish service teacher)
    - check all param with type date
    -
     */
    @PostMapping(path = "/add-course")
    public ResponseEntity<?> addCourse(@Valid CourseRequestAdd courseRequestAdd, BindingResult bindingResult) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()) {
            response.put("errors", Arrays.asList(bindingResult.getAllErrors()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(!ALLOWED_FILE_TYPES.contains(courseRequestAdd.avt.getContentType())) {
            response.put("errors", Arrays.asList("avt content type must be one of " + ALLOWED_FILE_TYPES));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(!ALLOWED_EXTENSIONS.contains(getFileExtension(courseRequestAdd.avt.getOriginalFilename()))) {
            response.put("errors", Arrays.asList("avt content type must be one of " + ALLOWED_EXTENSIONS));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if(courseRequestAdd.avt.getSize() > MAX_SIZE) {
            response.put("errors", Arrays.asList("avt size must be less than " + MAX_SIZE));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        URL urlAvt = bucketServiceBL.putObjectToBucket(bucketName, courseRequestAdd.avt,"images");
        Course course = covertCourseRequestAddToCourse(courseRequestAdd, urlAvt);

        Course courseResult = courseServiceImpl.create(course);

        response.put("course", courseResult);
        return ResponseEntity.ok(response);
    }
    @GetMapping(path = "search-text-by-course-name")
    public ResponseEntity<?> searchTextByCourseName(@RequestParam("courseName") String courseName, @RequestParam(value = "lastEvaluatedKey", defaultValue = "null")Map<String, AttributeValue> lastEvaluatedKey, @RequestParam(value = "page-size", defaultValue = "10") int pageSize) {
        Map<String, Object> response = new HashMap<>();

        ScanResponse scanResponse = courseServiceImpl.findByCourseName(courseName, lastEvaluatedKey, pageSize);
        response.put("courses", mappingCoursesFromScanResponse(scanResponse));
        response.put("lastEvaluateKey", scanResponse.lastEvaluatedKey());
        return ResponseEntity.ok(response);
    }
    @GetMapping(path = "search-text-own-or-studentId-by-course-name")
    public ResponseEntity<?> searchTextOwnByCourseName(@RequestParam("courseName") String courseName, @RequestParam(value = "lastEvaluatedKey", defaultValue = "null")Map<String, AttributeValue> lastEvaluatedKey, @RequestParam(value = "page-size", defaultValue = "10") int pageSize) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ScanResponse scanResponse = courseServiceImpl.findOwnOrStudentIdByCourseName(user.getUsername(),courseName, lastEvaluatedKey, pageSize);
        response.put("courses", mappingCoursesFromScanResponse(scanResponse));
        response.put("lastEvaluateKey", scanResponse.lastEvaluatedKey());
        return ResponseEntity.ok(response);
    }
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
            if (item.containsKey("createTime")) {
                course.setCreateTime(LocalDateTime.parse(item.get("createTime").s()));
            }
            if (item.containsKey("updateTime")) {
                course.setUpdateTime(LocalDateTime.parse(item.get("updateTime").s()));
            }
            if (item.containsKey("openTime")) {
                course.setOpenTime(LocalDateTime.parse(item.get("openTime").s()));
            }
            if (item.containsKey("closeTime")) {
                course.setCloseTime(LocalDateTime.parse(item.get("closeTime").s()));
            }
            if (item.containsKey("startTime")) {
                course.setStartTime(LocalDateTime.parse(item.get("startTime").s()));
            }

            if (item.containsKey("completeTime")) {
                course.setCompleteTime(LocalDateTime.parse(item.get("completeTime").s()));
            }
            if (item.containsKey("urlAvt")) {
                course.setUrlAvt(item.get("urlAvt").s());
            }
            if (item.containsKey("teacherName")) {
                course.setTeacherName(item.get("teacherName").s());
            }
            if (item.containsKey("numberMinimum")) {
                course.setNumberMinimum(Integer.parseInt(item.get("numberMinimum").n()));
            }
            if (item.containsKey("numberMaximum")) {
                course.setNumberMaximum(Integer.parseInt(item.get("numberMaximum").n()));
            }
            if (item.containsKey("numberCurrent")) {
                course.setNumberCurrent(Integer.parseInt(item.get("numberCurrent").n()));
            }
            courses.add(course);
        }
        return courses;
    }
    private Course covertCourseRequestAddToCourse(CourseRequestAdd courseRequestAdd, URL urlAvt) throws IOException {
        Course course = new Course(
                courseRequestAdd.courseName,
                courseRequestAdd.description,
                courseRequestAdd.price,
                courseRequestAdd.openTime,
                courseRequestAdd.closeTime,
                courseRequestAdd.startTime,
                courseRequestAdd.completeTime,
                CourseStatus.SEND,
                urlAvt.toString(),
                "",
                "",
                courseRequestAdd.numberMinimum,
                courseRequestAdd.numberMaximum,
                0);
        return course;
    }
private record CourseRequestAdd(
        @NotNull(message = "course name must not be null")
        @Length(min = 3, max = 50,message = "course name has length minimum 3 and maximum 50")
        String courseName,
        @NotNull(message = "description must not be null")
        @Length(min = 10, max = 150, message = "description has length minimum 10 and maximum 150")
        String description,
        @NotNull(message = "category must not be null")
        String category,
        @NotNull(message = "open time must not be null")
        @Future(message = "open time must be a future date")
        LocalDateTime openTime,
        @NotNull(message = "close time must not be null")
        @Future(message = "close time mus be a future date")
        LocalDateTime closeTime,
        @NotNull(message = "start time must not be null")
        @Future(message = "start time mus be a future date")
        LocalDateTime startTime,
        @NotNull(message = "complete time must not be null")
        @Future(message = "complete time mus be a future date")
        LocalDateTime completeTime,
        @Min(value = 1, message = "number minimum must be greater than 0")
        int numberMinimum,
        @Max(value = 100, message = "number maximum must be greater than 0")
        int numberMaximum,
        @NotNull(message = "file avt must not be null")
        MultipartFile avt,
        @NotNull(message = "price must not be null")
        @Min(value = 0, message = "price must be greater than 0")
        @Max(value = 100000000, message = "price must be less than 100.000.000")
        double price
        ){};
    public String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return ""; // Nếu không có đuôi mở rộng
    }

}
