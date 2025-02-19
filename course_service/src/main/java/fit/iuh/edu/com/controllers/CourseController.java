package fit.iuh.edu.com.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.services.Impl.BucketServiceImpl;
import fit.iuh.edu.com.services.Impl.CourseServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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

    private final WebClient webClient;


    public CourseController(WebClient.Builder webClientBuilder, @Value("${api.v1.baseUrl.userApi}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();


    }
    @PostMapping(path = "search-text-own-or-studentId-by-course-name")
    public ResponseEntity<?> searchTextOwnByCourseName(@Valid AttributeSearchCourse attributeSearchCourse, BindingResult bindingResult) throws JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> rawMap  = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, AttributeValue> lastEvaluateKeyMap = new HashMap<>();

        if (attributeSearchCourse.lastEvaluateKey != null) {

            rawMap  = objectMapper.readValue(attributeSearchCourse.lastEvaluateKey, new TypeReference<Map<String, String>>() {});
            lastEvaluateKeyMap = rawMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> AttributeValue.builder().s(e.getValue()).build() // Convert String to AttributeValue
                    ));
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Course> courses = courseServiceImpl.findOwnOrStudentIdByCourseName(((Jwt)authentication.getPrincipal()).getClaims().get("username").toString(),attributeSearchCourse.courseName != null ? attributeSearchCourse.courseName :"", !lastEvaluateKeyMap.isEmpty() ? lastEvaluateKeyMap:null, attributeSearchCourse.pageSize);
        response.put("courses", courses);
        return ResponseEntity.ok(response);
    }
    @GetMapping(path = "get-course-detail-by-id")
    public ResponseEntity<?> getCourseDetailById(@RequestParam("course-id") String courseId){
        Course course = courseServiceImpl.getCourseDetailById(courseId);
        return ResponseEntity.ok(course);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
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

        if(courseRequestAdd.openTime.isAfter(courseRequestAdd.closeTime)){
            response.put("errors", Arrays.asList("open time must be less than end time"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }
        if(courseRequestAdd.startTime.isAfter(courseRequestAdd.completeTime)){
            response.put("errors", Arrays.asList("start time must be less than end time"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }
        if(courseRequestAdd.closeTime.isAfter(courseRequestAdd.startTime)){
            response.put("errors", Arrays.asList("close time must be less than start time"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }

        fit.iuh.edu.com.models.User teacher = getUserById(courseRequestAdd.teacherId);
        if (teacher == null){
            response.put("errors", Arrays.asList("teacher not found. Our fault not yours, please contract us to check."));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }
        System.out.println(teacher.getUserName());
        if(teacher.getGroups() == null || !teacher.getGroups().contains("TEACHER")){
            response.put("errors", Arrays.asList("user found not in the teacher group"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }


        URL urlAvt = bucketServiceBL.putObjectToBucket(bucketName, courseRequestAdd.avt,"images");


        Course course = covertCourseRequestAddToCourse(courseRequestAdd, urlAvt,teacher.getId(), teacher.getUserName());

        Course courseResult = courseServiceImpl.create(course);

        response.put("course", courseResult);
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping(path = "/update-course")
    public ResponseEntity<?> updateCourse(@Valid CourseRequestUpdate courseRequestUpdate, BindingResult bindingResult) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()) {
            response.put("errors", Arrays.asList(bindingResult.getAllErrors()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }


        return ResponseEntity.ok(response);
    }

    private fit.iuh.edu.com.models.User getUserById(String teacherId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwt = ((Jwt) authentication.getPrincipal()).getTokenValue();
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("").queryParam("id", teacherId).build())
                .headers(httpHeaders -> httpHeaders.setBearerAuth(jwt))
                .retrieve()
                .bodyToMono(ResponseUser.class)
                .map(ResponseUser::user)
                .block();

    }


    @PostMapping(path = "search-text-by-course-name")
    public ResponseEntity<?> searchTextByCourseName(@Valid AttributeSearchCourse attributeSearchCourse, BindingResult bindingResult ) throws JsonProcessingException {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> rawMap  = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, AttributeValue> lastEvaluateKeyMap = new HashMap<>();

        if (attributeSearchCourse.lastEvaluateKey != null) {

            rawMap  = objectMapper.readValue(attributeSearchCourse.lastEvaluateKey, new TypeReference<Map<String, String>>() {});
            lastEvaluateKeyMap = rawMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> AttributeValue.builder().s(e.getValue()).build() // Convert String to AttributeValue
                    ));
        }
        List<Course> courses = courseServiceImpl.findByCourseName(attributeSearchCourse.courseName != null ?attributeSearchCourse.courseName :"" , !lastEvaluateKeyMap.isEmpty() ? lastEvaluateKeyMap:null, attributeSearchCourse.pageSize);


        response.put("courses", courses);

        return ResponseEntity.ok(response);
    }
    private Course covertCourseRequestAddToCourse(CourseController.CourseRequestAdd courseRequestAdd, URL urlAvt, String teacherId, String teacherName) throws IOException {
        Course course = new Course(
                courseRequestAdd.courseName,
                courseRequestAdd.description,
                courseRequestAdd.price,
                courseRequestAdd.openTime,
                courseRequestAdd.closeTime,
                courseRequestAdd.startTime,
                courseRequestAdd.completeTime,
                CourseStatus.OPEN,
                urlAvt.toString(),
                teacherId,
                teacherName,
                courseRequestAdd.numberMinimum,
                courseRequestAdd.numberMaximum,
                0,
                courseRequestAdd.category);
        return course;
    }

    private record ResponseUser(fit.iuh.edu.com.models.User user) {};
    private record AttributeSearchCourse(
            String courseName,
            int pageSize,
            String lastEvaluateKey
    ){
        public AttributeSearchCourse {
            if (pageSize <= 0) {
                pageSize = 10; // Giá trị mặc định
            }
        }
    }
    private record CourseRequestAdd(
            @NotNull(message = "course name must not be null")
            @Length(min = 3, max = 50,message = "course name has length minimum 3 and maximum 50")
            String courseName,
            @NotNull(message = "description must not be null")
            @Length(min = 10, max = 150, message = "description has length minimum 10 and maximum 150")
            String description,
            @NotNull(message = "category must not be null")
            @Length(min = 2)
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
            double price,
            @NotNull(message = "teacher id must not be null")
            String teacherId
    ){};
    private record CourseRequestUpdate(
            @NotNull(message = "course id must not be null")
            String id,
            @Null
            @Length(min = 2)
            String category,
            @Null
            @Future(message = "open time must be a future date")
            LocalDateTime openTime,
            @Null
            @Future(message = "close time mus be a future date")
            LocalDateTime closeTime,
            @Null
            @Future(message = "start time mus be a future date")
            LocalDateTime startTime,
            @Null
            @Future(message = "complete time mus be a future date")
            LocalDateTime completeTime,
            @Null
            @Min(value = 1, message = "number minimum must be greater than 0")
            int numberMinimum,
            @Null
            @Max(value = 100, message = "number maximum must be greater than 0")
            int numberMaximum,
            @Null
            CourseStatus courseStatus,
            @Null
            @Min(value = 0, message = "price must be greater than 0")
            @Max(value = 100000000, message = "price must be less than 100.000.000")
            double price,
            @Null
            String teacherId
    ){};
        public String getFileExtension(String filename) {
            int dotIndex = filename.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < filename.length() - 1) {
                return filename.substring(dotIndex + 1).toLowerCase();
            }
            return ""; // Nếu không có đuôi mở rộng
        }

}
