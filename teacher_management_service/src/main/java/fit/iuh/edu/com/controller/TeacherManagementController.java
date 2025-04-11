package fit.iuh.edu.com.controller;

import fit.iuh.edu.com.dtos.DecisionMakingTeacherAdd;
import fit.iuh.edu.com.dtos.TeacherAddRequest;
import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.enums.DecisionMaking;
import fit.iuh.edu.com.export.ExcelExporter;
import fit.iuh.edu.com.models.EmailMessage;
import fit.iuh.edu.com.models.Feedback;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.services.BL.BucketServiceBL;
import fit.iuh.edu.com.services.BL.CognitoServiceBL;
import fit.iuh.edu.com.services.BL.FeedbackServiceBL;
import fit.iuh.edu.com.services.BL.UserServiceBL;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Controller
@RequestMapping("api/v1/teacher")
public class TeacherManagementController {
    private static final List<String> ALLOWED_FILE_TYPES_IMAGE = Arrays.asList("image/jpeg", "image/png");
    private static final List<String> ALLOWED_FILE_TYPES_PDF = Arrays.asList("application/pdf");

    private static final List<String> ALLOWED_EXTENSIONS_IMAGE = Arrays.asList("jpg", "jpeg", "png");
    private static final List<String> ALLOWED_EXTENSIONS_PDF = Arrays.asList("pdf");

    private static final long MAX_SIZE_IMAGE = 5 * 1024 * 1024; // 5MB

    @Autowired
    private BucketServiceBL bucketServiceBL;
    @Autowired
    private UserServiceBL userServiceBL;
    @Autowired
    private CognitoServiceBL cognitoServiceBL;
    @Autowired
    private FeedbackServiceBL feedbackServiceBL;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @PostMapping(path = "/require-create-teacher-account")
    public ResponseEntity<?> createTeacherTemp(@Valid TeacherAddRequest teacherAddRequest, BindingResult bindingResult) throws IOException, ExecutionException, InterruptedException {
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()) {
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        ResponseEntity<?> checkFileImage = checkFile(teacherAddRequest.getFaceImage(), ALLOWED_FILE_TYPES_IMAGE, ALLOWED_EXTENSIONS_IMAGE, MAX_SIZE_IMAGE);
        if(checkFileImage != null) {
            return checkFileImage;
        }
        checkFileImage = checkFile(teacherAddRequest.getCvFile(), ALLOWED_FILE_TYPES_PDF, ALLOWED_EXTENSIONS_PDF, MAX_SIZE_IMAGE);
        if(checkFileImage != null) {
            return checkFileImage;
        }
        if(teacherAddRequest.getBirthday().isAfter(teacherAddRequest.getBirthday().plusYears(18))) {
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", "The birthday must be greater than 18 years");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(!userServiceBL.beforeAddTeacher(teacherAddRequest.getEmail(),teacherAddRequest.getPhoneNumber())){
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", "Email or phone number already exists");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        Map<String, String> responsePutFaceImage = bucketServiceBL.putObjectToBucket(bucketName, teacherAddRequest.getFaceImage(),"images");
        Map<String, String> responsePutCVFile = bucketServiceBL.putObjectToBucket(bucketName, teacherAddRequest.getCvFile(),"files");
        User teacher = User
                .builder()
                .userName(teacherAddRequest.getTeacherName())
                .email(teacherAddRequest.getEmail())
                .birthday(teacherAddRequest.getBirthday())
                .gender(teacherAddRequest.getGender())
                .description(teacherAddRequest.getDescription())
                .urlImage(responsePutFaceImage.get("url"))
                .cvFile(responsePutCVFile.get("key"))
                .accountStatus(AccountStatus.REQUIRE)
                .build();
        userServiceBL.create(teacher);
        response.put("code", HttpStatus.OK);
        response.put("status", "ok");
        response.put("data", "Teacher has been created successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/lock-teacher-account")
    public ResponseEntity<?> lockTeacherAccount(@RequestParam("teacherId") String teacherId){
        Map<String, Object> response = new HashMap<>();
        User teacher = userServiceBL.findById(teacherId);
        if(teacher== null){
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status","error");
            response.put("message","Teacher not found");
            return ResponseEntity.badRequest().body(response);
        }


//        List<Feedback> feedbacks = feedbackServiceBL.getFeedbacksByTeacherId(teacherId);
//        String path = UUID.randomUUID()+".xlsx";
//        byte[] bytes = ExcelExporter.exportAndReadExcel(feedbacks, Path.of(path));
//        EmailMessage emailMessage = EmailMessage.builder()
//                .to(teacher.getEmail())
//                .subject("Lock your account")
//                .templateData()
//                .build();
        return null;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/decision-making-create-teacher-account")
    public ResponseEntity<?> decisionMaking(@Valid DecisionMakingTeacherAdd decisionMakingTeacherAdd, BindingResult bindingResult) throws IOException, ExecutionException, InterruptedException {
        System.out.println(decisionMakingTeacherAdd.getAction());
        Map<String, Object> response = new HashMap<>();
        if(bindingResult.hasErrors()) {
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        User teacher = userServiceBL.findById(decisionMakingTeacherAdd.getId());
        if(teacher == null) {
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", "Teacher not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(!teacher.getAccountStatus().equals(AccountStatus.REQUIRE)){
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", "");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if(decisionMakingTeacherAdd.getAction().equals(AccountStatus.ACCEPT)){
            String id = cognitoServiceBL.createTeacher(teacher);
            teacher.setCognitoId(id);
            teacher.setAccountStatus(decisionMakingTeacherAdd.getAction());
            userServiceBL.update(teacher);

            //send email

            response.put("code", HttpStatus.OK);
            response.put("status", "ok");
            response.put("data", "Teacher has been accepted");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        }else if(decisionMakingTeacherAdd.getAction().equals(AccountStatus.REJECT)){
            userServiceBL.delete(teacher);
            response.put("code", HttpStatus.OK);
            response.put("status", "ok");
            response.put("data", "Teacher has been deleted successfully");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        response.put("code", HttpStatus.BAD_REQUEST);
        response.put("status", "error");
        response.put("message", "");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    public ResponseEntity<?> checkFile(MultipartFile file, List<String> ALLOWED_FILE_TYPES_IMAGE,List<String> ALLOWED_EXTENSIONS_IMAGE, long MAX_SIZE_IMAGE) {
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
