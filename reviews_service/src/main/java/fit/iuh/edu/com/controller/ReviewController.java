package fit.iuh.edu.com.controller;

import com.google.common.io.Files;
import fit.iuh.edu.com.dtos.ReviewRequestAdd;
import fit.iuh.edu.com.models.Review;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.services.BL.BucketServiceBL;
import fit.iuh.edu.com.services.BL.ReviewServiceBL;
import fit.iuh.edu.com.services.BL.UserServiceBL;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
@Controller
@RequestMapping("/api/v1/reviews")
public class ReviewController {
    private static final List<String> ALLOWED_FILE_TYPES_IMAGE = Arrays.asList("image/jpeg", "image/png");
    private static final List<String> ALLOWED_EXTENSIONS_IMAGE = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_SIZE_IMAGE = 5 * 1024 * 1024; // 5MB
    private final BucketServiceBL bucketServiceBL;
    private final ReviewServiceBL reviewServiceBL;
    private final UserServiceBL userServiceBL;
    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    public ReviewController(BucketServiceBL bucketServiceBL, ReviewServiceBL reviewServiceBL, UserServiceBL userServiceBL) {
        this.bucketServiceBL = bucketServiceBL;
        this.reviewServiceBL = reviewServiceBL;
        this.userServiceBL = userServiceBL;
    }
    @GetMapping("/reviewed")
    public ResponseEntity<?> reviewed(@RequestParam("courseId") String courseId) {
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("code",200);
        response.put("message","success");
        response.put("data",reviewServiceBL.getReviewedByCourseId(courseId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    public ResponseEntity<?> addReview(@Valid ReviewRequestAdd reviewRequestAdd, BindingResult bindingResult) throws IOException, ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<String, Object>();
        if (bindingResult.hasErrors()) {
            response.put("errors", bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(response);
        }
//        ResponseEntity<?> resultCheckFileImage = checkFileImage(reviewRequestAdd.getFileImage());
//        if(resultCheckFileImage != null){
//            return resultCheckFileImage;
//        }
        if(!reviewServiceBL.checkDependency(reviewRequestAdd.getCourseId())){
            response.put("errors", "Review dependency class errors");
            return ResponseEntity.badRequest().body(response);
        }
        if(reviewServiceBL.getReviewedByCourseId(reviewRequestAdd.getCourseId())!= null){
            response.put("errors", "The course has been evaluated");
            return ResponseEntity.badRequest().body(response);
        }
        User user = userServiceBL.getUser(SecurityContextHolder.getContext().getAuthentication().getName());
//        String urlAvt = bucketServiceBL.putObjectToBucket(bucketName, reviewRequestAdd.getFileImage(),"images");
        Review review = Review
                .builder()
                .content(reviewRequestAdd.getContent())
                .review(reviewRequestAdd.getReview())
                .courseId(reviewRequestAdd.getCourseId())
//                .urlImage(urlAvt)
                .userId(user.getId())
                .urlAvt(user.getUrlImage())
                .userName(user.getUserName())
                .build();
        response.put("data", reviewServiceBL.add(review));
        response.put("code",200);
        response.put("message","success");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping(path = "/get-reviews-by-courseId")
    public ResponseEntity<?> getReviewsByCourseId(@RequestParam("courseId") String courseId, @RequestParam(value = "review", required = false) Integer review ) {
        List<Review> reviews = new ArrayList<>();
        if(review != null && review > 0 && review <= 5) {
            reviews = reviewServiceBL.getReviewsByCourseId(courseId, review);
        }else{
            reviews = reviewServiceBL.getReviewsBeforeNow(courseId);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("code",200);
        response.put("data", reviews);
        response.put("message","success");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @GetMapping(path = "/get-reviews-by-teacherId")
    public ResponseEntity<?> getReviewsByTeacher(@RequestParam("teacherId") String teacherId) {
        User teacher = userServiceBL.getUser(teacherId);
        Map<String, Object> response = new HashMap<>();
        response.put("code",200);
        response.put("data", reviewServiceBL.getReviewsByTeacherId(teacher));
        response.put("message","success");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping(path = "map-review-from-course-to-teacher")
    public ResponseEntity<?> mapReviewFromCourseToTeacher(@RequestParam("courseId") String courseId,@RequestParam("reviewId") String reviewId) {
        Map<String, Object> response = new HashMap<>();
        if(reviewServiceBL.checkBeforeMapReview(courseId, reviewId)){
            reviewServiceBL.mapReviewToTeacher(reviewId,courseId);
            response.put("code",200);
            response.put("message","success");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("code",400);
        response.put("message","error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    public ResponseEntity<?> checkFileImage(MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        if(!ALLOWED_FILE_TYPES_IMAGE.contains(file.getContentType())) {
            response.put("errors", "avt content type must be one of " + ALLOWED_FILE_TYPES_IMAGE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }
        if(!ALLOWED_EXTENSIONS_IMAGE.contains(Files.getFileExtension(Objects.requireNonNull(file.getOriginalFilename())))) {
            response.put("errors", "avt content type must be one of " + ALLOWED_EXTENSIONS_IMAGE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        }

        if(file.getSize() > MAX_SIZE_IMAGE) {
            response.put("errors", "avt size must be less than " + MAX_SIZE_IMAGE);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return null;
    }


}
