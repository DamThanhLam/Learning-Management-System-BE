package fit.iuh.edu.com.controllers;

import fit.iuh.edu.com.dtos.*;
import fit.iuh.edu.com.models.Category;
import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.services.BL.CategoryServiceBL;
import fit.iuh.edu.com.services.BL.UserServiceBL;
import fit.iuh.edu.com.services.Impl.BucketServiceImpl;
import fit.iuh.edu.com.services.Impl.CourseServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/course")
public class CourseController {
    private static final List<String> ALLOWED_FILE_TYPES_IMAGE = Arrays.asList("image/jpeg", "image/png");
    private static final List<String> ALLOWED_EXTENSIONS_IMAGE = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_SIZE_IMAGE = 5 * 1024 * 1024; // 5MB

    private static final List<String> ALLOWED_FILE_TYPES_VIDEO = Arrays.asList("video/mp4", "video/avi", "video/mpeg");
    private static final List<String> ALLOWED_EXTENSIONS_VIDEO = Arrays.asList("mp4", "avi", "mpeg");
    private static final long MAX_SIZE_VIDEO_INTRO = 50 * 1024 * 1024; // 10MB


    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Autowired
    private BucketServiceImpl bucketServiceBL;
    @Autowired
    private UserServiceBL userServiceBL;
    @Autowired
    private CourseServiceImpl courseServiceImpl;
    @Autowired
    private CategoryServiceBL categoryServiceBL;

    @GetMapping
    public ResponseEntity<?> getCourseDetailById(@RequestParam("id") String courseId){
        Course course = courseServiceImpl.getCourseDetailById(courseId);
        Map<String, Object> response = new HashMap<>();
        response.put("code",200);
        response.put("data",course);
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/add-image")
    public ResponseEntity<?> addImage(@RequestParam("image") MultipartFile image) throws IOException {
        Map<String, Object> response = new HashMap<>();

        ResponseEntity<?> resultCheckFileImage = checkFileImage(image);
        if(resultCheckFileImage != null){
            return resultCheckFileImage;
        }
        String urlAvt = bucketServiceBL.putObjectToBucket(bucketName, image,"images");
        response.put("code",200);
        response.put("url",urlAvt);
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }
    @PutMapping
    public ResponseEntity<?> updateCourse(@Valid CourseRequestUpdate courseRequestUpdate, BindingResult bindingResult) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()) {
            response.put("status","error");
            response.put("message",bindingResult.getAllErrors());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(courseServiceImpl.checkCourseBeforeUpdate(courseRequestUpdate.getId())){
            Course updatedCourseResponse = null;
            Course course = courseServiceImpl.getCourseDetailById(courseRequestUpdate.getId());
            String urlAvt= "";
            String urlIntro="";
            if(courseRequestUpdate.getFileAvt() != null){
                ResponseEntity<?> resultCheckFileImage = checkFileImage(courseRequestUpdate.getFileAvt());
                if(resultCheckFileImage != null){
                    return resultCheckFileImage;
                }
                //delete object old
//                String urlAvtOld = courseServiceImpl.getCourseDetailById(courseRequestUpdate.getId()).getUrlAvt();
//                int indexSlashEnd = urlAvtOld.lastIndexOf(".amazonaws.com");
//                String key = urlAvtOld.substring(indexSlashEnd + 15);
//                System.out.println("key old: "+key);
//                bucketServiceBL.removeObjectFromBucket(bucketName,key);
                urlAvt = bucketServiceBL.putObjectToBucket(bucketName, courseRequestUpdate.getFileAvt(),"images");
            }
            if(courseRequestUpdate.getVideoIntro() != null){
                ResponseEntity<?> resultCheckFileImage = checkVideoIntro(courseRequestUpdate.getVideoIntro());
                if(resultCheckFileImage != null){
                    return resultCheckFileImage;
                }
                //delete object old
//                String urlAvtOld = courseServiceImpl.getCourseDetailById(courseRequestUpdate.getId()).getUrlAvt();
//                int indexSlashEnd = urlAvtOld.lastIndexOf(".amazonaws.com");
//                String key = urlAvtOld.substring(indexSlashEnd + 15);
//                System.out.println("key old: "+key);
//                bucketServiceBL.removeObjectFromBucket(bucketName,key);
                urlIntro = bucketServiceBL.putObjectToBucket(bucketName, courseRequestUpdate.getVideoIntro(),"videos");
            }

            response.put("status","success");
            response.put("code",200);
            response.put("data", courseServiceImpl.updateCourse(courseRequestUpdate.toCourse(urlAvt,urlIntro,course)));
            return ResponseEntity.ok(response);
        }
        response.put("status","error");
        response.put("message","Course can not update");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/search-courses-common")
    public ResponseEntity<?> searchCoursesCommon(){
        Map<String, Object> response = new HashMap<>();
        List<Course> courses = courseServiceImpl.getCoursesCommon();
        if(courses.isEmpty()){
            return  ResponseEntity.badRequest().build();
        }
        response.put("code",200);
        response.put("message","success");
        response.put("data",courses);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/search")
    public ResponseEntity<?> listCourses(
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) Integer rating
    ) {
        int currentPage = (page != null && page >= 0) ? page : 0;

        // Nếu bạn có phân trang theo offset
        int offset = currentPage * size;

        // Gọi service với các thông số mới
        List<Course> courses = courseServiceImpl.searchCourses(
                courseName,
                category,
                rating,
                sort,
                offset,
                size
        );

        List<CourseOfStudentResponse> coursesResponse = courses.stream()
                .map(course -> CourseOfStudentResponse.builder()
                        .id(course.getId())
                        .price(course.getPrice())
                        .courseName(course.getCourseName())
                        .countReviews(course.getCountReviews())
                        .teacherName(course.getTeacherName())
                        .teacherId(course.getTeacherId())
                        .totalReview(course.getTotalReview())
                        .urlAvt(course.getUrlAvt())
                        .category(course.getCategory())
                        .build())
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", coursesResponse);
        response.put("message", "success");

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student")
    public ResponseEntity<?> listCoursesByStudentId(@RequestParam(required = false) String lastEvaluatedId, @RequestParam(required = false, defaultValue = "10") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, AttributeValue> lastEvaluatedKey = new HashMap<>();
        if (lastEvaluatedId != null && !lastEvaluatedId.isEmpty()) {
            lastEvaluatedKey.put("id", AttributeValue.builder().s(lastEvaluatedId).build());
        }
        List<Course> courses = courseServiceImpl.getCoursesByStudentID(authentication.getName(),pageSize,lastEvaluatedKey);

        List<CourseOfStudentResponse> coursesResponse = courses.stream()
                .map(course -> CourseOfStudentResponse.builder()
                        .id(course.getId())
                        .price(course.getPrice())
                        .courseName(course.getCourseName())
                        .countReviews(course.getCountReviews())
                        .teacherName(course.getTeacherName())
                        .teacherId(course.getTeacherId())
                        .totalReview(course.getTotalReview())
                        .build())
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("code",200);
        response.put("data",coursesResponse);
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/teacher")
    public ResponseEntity<?> listCoursesByTeacherId(@RequestParam(required = false) String lastEvaluatedId, @RequestParam(required = false, defaultValue = "10") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, AttributeValue> lastEvaluatedKey = new HashMap<>();
        if (lastEvaluatedId != null && !lastEvaluatedId.isEmpty()) {
            lastEvaluatedKey.put("id", AttributeValue.builder().s(lastEvaluatedId).build());
        }
        List<Course> courses = courseServiceImpl.getCoursesByTeacherID(authentication.getName(),pageSize,lastEvaluatedKey);
        List<CourseOfTeacherResponse> coursesResponse = new ArrayList<>();

        courses.forEach(course -> {

            CourseOfTeacherResponse courseOfStudentResponse = CourseOfTeacherResponse
                    .builder()
                    .id(course.getId())
                    .price(course.getPrice())
                    .courseName(course.getCourseName())
                    .countReviews(course.getCountReviews())
                    .countLectures(course.getCountLectures())
                    .countOrders(course.getCountOrders())
                    .status(course.getStatus())
                    .build();
            coursesResponse.add(courseOfStudentResponse);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("code",200);
        response.put("data",coursesResponse);
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all-categories")
    public ResponseEntity<?> getAllCategories(){
        Map<String, Object> response = new HashMap<>();
        response.put("code",200);
        response.put("data", categoryServiceBL.getAllCategories().stream().map(Category::getCategoryName));
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('TEACHER')")
    @PostMapping(path = "/add-course")
    public ResponseEntity<?> addCourse(@Valid CourseRequestAdd courseRequestAdd, BindingResult bindingResult) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()) {
            response.put("errors", Arrays.asList(bindingResult.getAllErrors()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }
        ResponseEntity<?> resultCheckFileImage = checkFileImage(courseRequestAdd.getFileAvt());
        if(resultCheckFileImage != null){
            return resultCheckFileImage;
        }
        ResponseEntity<?> resultCheckVideoIntro = checkVideoIntro(courseRequestAdd.getVideoIntro());
        if(resultCheckVideoIntro != null){
            return resultCheckVideoIntro;
        }


        String urlAvt = bucketServiceBL.putObjectToBucket(bucketName, courseRequestAdd.getFileAvt(),"images");
        String urlIntro = bucketServiceBL.putObjectToBucket(bucketName, courseRequestAdd.getVideoIntro(),"videos");
        User user = userServiceBL.getUser();
        Course course = courseRequestAdd.covertCourseRequestAddToCourse(urlAvt, user.getUserName(),user.getId(),urlIntro);
        Course courseResult = courseServiceImpl.create(course);
        categoryServiceBL.addCategory(course.getCategory());

        response.put("code",200);
        response.put("data",courseResult);
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<?> checkVideoIntro(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        if(!ALLOWED_FILE_TYPES_VIDEO.contains(file.getContentType())) {
            response.put("errors", "avt content type must be one of " + ALLOWED_FILE_TYPES_VIDEO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }
        if(!ALLOWED_EXTENSIONS_VIDEO.contains(getFileExtension(Objects.requireNonNull(file.getOriginalFilename())))) {
            response.put("errors", "avt content type must be one of " + ALLOWED_EXTENSIONS_VIDEO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }

        if(file.getSize() > MAX_SIZE_VIDEO_INTRO) {
            response.put("errors", "avt size must be less than " + MAX_SIZE_VIDEO_INTRO);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return null;
    }

    public ResponseEntity<?> checkFileImage(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        if(!ALLOWED_FILE_TYPES_IMAGE.contains(file.getContentType())) {
            response.put("errors", "avt content type must be one of " + ALLOWED_FILE_TYPES_IMAGE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }
        if(!ALLOWED_EXTENSIONS_IMAGE.contains(getFileExtension(Objects.requireNonNull(file.getOriginalFilename())))) {
            response.put("errors", "avt content type must be one of " + ALLOWED_EXTENSIONS_IMAGE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }

        if(file.getSize() > MAX_SIZE_IMAGE) {
            response.put("errors", "avt size must be less than " + MAX_SIZE_IMAGE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return null;
    }
    public String getFileExtension(String filename) {
            int dotIndex = filename.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < filename.length() - 1) {
                return filename.substring(dotIndex + 1).toLowerCase();
            }
            return ""; // Nếu không có đuôi mở rộng
        }

}
