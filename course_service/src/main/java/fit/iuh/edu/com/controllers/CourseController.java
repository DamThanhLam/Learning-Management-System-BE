package fit.iuh.edu.com.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fit.iuh.edu.com.dtos.AttributeSearchCourse;
import fit.iuh.edu.com.dtos.CourseRequestAdd;
import fit.iuh.edu.com.dtos.CourseRequestUpdate;
import fit.iuh.edu.com.dtos.ResponseUser;
import fit.iuh.edu.com.enums.CourseStatus;
import fit.iuh.edu.com.models.Course;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.services.BL.UserServiceBL;
import fit.iuh.edu.com.services.Impl.BucketServiceImpl;
import fit.iuh.edu.com.services.Impl.CourseServiceImpl;
import fit.iuh.edu.com.services.Impl.UserServiceImpl;
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
import org.springframework.security.oauth2.core.user.OAuth2User;
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
    private UserServiceBL userServiceBL;
    @Autowired
    private CourseServiceImpl courseServiceImpl;

    private final WebClient webClient;


    public CourseController(WebClient.Builder webClientBuilder, @Value("${api.v1.baseUrl.userApi}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();

    }
    @PostMapping(path = "/demo")
    public ResponseEntity<?> getDemo() {
        return ResponseEntity.ok(userServiceBL.getUser());
    }


    //
//    @GetMapping(path = "get-course-detail-by-id")
//    public ResponseEntity<?> getCourseDetailById(@RequestParam("course-id") String courseId){
//        Course course = courseServiceImpl.getCourseDetailById(courseId);
//        return ResponseEntity.ok(course);
//    }
    @PreAuthorize("hasAnyRole('TEACHER')")
    @PostMapping(path = "/add-course")
    public ResponseEntity<?> addCourse(@Valid CourseRequestAdd courseRequestAdd, BindingResult bindingResult) throws IOException {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()) {
            response.put("errors", Arrays.asList(bindingResult.getAllErrors()));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }
        if(!ALLOWED_FILE_TYPES.contains(courseRequestAdd.getFileAvt().getContentType())) {
            response.put("errors", Arrays.asList("avt content type must be one of " + ALLOWED_FILE_TYPES));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }
        if(!ALLOWED_EXTENSIONS.contains(getFileExtension(courseRequestAdd.getFileAvt().getOriginalFilename()))) {
            response.put("errors", Arrays.asList("avt content type must be one of " + ALLOWED_EXTENSIONS));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }

        if(courseRequestAdd.getFileAvt().getSize() > MAX_SIZE) {
            response.put("errors", Arrays.asList("avt size must be less than " + MAX_SIZE));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }

        URL urlAvt = bucketServiceBL.putObjectToBucket(bucketName, courseRequestAdd.getFileAvt(),"images");
        User user = userServiceBL.getUser();
        Course course = courseRequestAdd.covertCourseRequestAddToCourse(urlAvt.toString(), user.getUserName(),user.getId());
        Course courseResult = courseServiceImpl.create(course);
        response.put("course", courseResult);
        return ResponseEntity.ok(response);
    }


    public String getFileExtension(String filename) {
            int dotIndex = filename.lastIndexOf(".");
            if (dotIndex > 0 && dotIndex < filename.length() - 1) {
                return filename.substring(dotIndex + 1).toLowerCase();
            }
            return ""; // Nếu không có đuôi mở rộng
        }

}
