package fit.iuh.edu.com.controller;

import fit.iuh.edu.com.configs.PasswordGenerator;
import fit.iuh.edu.com.configs.RabbitMQConfig;
import fit.iuh.edu.com.dtos.DecisionMakingTeacherAdd;
import fit.iuh.edu.com.dtos.RequestAccountDTO;
import fit.iuh.edu.com.dtos.TeacherAddRequest;
import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.enums.DecisionMaking;
import fit.iuh.edu.com.export.ExcelExporter;
import fit.iuh.edu.com.models.Account;
import fit.iuh.edu.com.models.EmailMessage;
import fit.iuh.edu.com.models.Feedback;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.services.BL.*;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.time.LocalDateTime;
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
    @Autowired
    private Cipher cipherDecrypt;
    @Autowired
    private AccountServiceBL accountServiceBL;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private PublicKey publicKey;
    @Autowired
    private PasswordEncoder passwordEncoder;

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
                .groups(List.of("STUDENT", "TEACHER"))
                .accountStatus(AccountStatus.REQUIRE)
                .build();
        userServiceBL.create(teacher);
        response.put("code", HttpStatus.OK);
        response.put("status", "ok");
        response.put("data", "Teacher has been created successfully: ID=" + teacher.getId() );
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
    public ResponseEntity<?> decisionMaking(@RequestBody @Valid DecisionMakingTeacherAdd decisionMakingTeacherAdd, BindingResult bindingResult)
            throws IOException, ExecutionException, InterruptedException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {

        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        User teacher = userServiceBL.findById(decisionMakingTeacherAdd.getId());
        if (teacher == null) {
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", "Teacher not found with id: " + decisionMakingTeacherAdd.getId());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!AccountStatus.REQUIRE.equals(teacher.getAccountStatus())) {
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", "Teacher account status must be REQUIRE to proceed. Current status: " + teacher.getAccountStatus().name());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String action = decisionMakingTeacherAdd.getAction();
        if (action != null && action.equalsIgnoreCase(AccountStatus.ACCEPT.name())) {
//            String id = cognitoServiceBL.createTeacher(teacher);
//            teacher.setCognitoId(id);
            teacher.setAccountStatus(AccountStatus.ACCEPT);
            userServiceBL.update(teacher);

            String key = UUID.randomUUID().toString();
            String generatedPassword = PasswordGenerator.generateSecurePassword();

            // Encrypt password
//            Cipher cipherEncrypt = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//            cipherEncrypt.init(Cipher.ENCRYPT_MODE, publicKey);
//            byte[] encryptedBytes = cipherEncrypt.doFinal(generatedPassword.getBytes(StandardCharsets.UTF_8));
//
//            // Encode Base64 để giả lập truyền qua mạng
//            String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
//
//            // Ở server hoặc ngay sau nhận base64:
//            // Decode
//            byte[] decodedEncryptedBytes = Base64.getDecoder().decode(encryptedBase64);
//
//            // Decrypt
//            byte[] decryptedBytes = cipherDecrypt.doFinal(decodedEncryptedBytes);
//            String decryptedPassword = new String(decryptedBytes, StandardCharsets.UTF_8);
//
//            // Save decrypted password
            Account account = Account.builder()
                    .id(UUID.randomUUID().toString())
                    .email(teacher.getEmail())
                    .password(passwordEncoder.encode(generatedPassword))
                    .build();

            accountServiceBL.addAccount(account);


            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ACCOUNT_REQUEST_ROUTING_KEY,
                    new RequestAccountDTO(teacher.getUserName(), teacher.getEmail(), generatedPassword, LocalDateTime.now(), 0, decisionMakingTeacherAdd.getDescription())
            );

            response.put("code", HttpStatus.OK);
            response.put("status", "ok");
            response.put("data", "Teacher has been accepted and account created successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } else if (action != null && action.equalsIgnoreCase(AccountStatus.REJECT.name())) {
            userServiceBL.delete(teacher);

            response.put("code", HttpStatus.OK);
            response.put("status", "ok");
            response.put("data", "Teacher has been rejected and deleted successfully.");

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE,
                    RabbitMQConfig.ACCOUNT_REQUEST_ROUTING_KEY,
                    new RequestAccountDTO(teacher.getUserName(), teacher.getEmail(), "", LocalDateTime.now(), 1, decisionMakingTeacherAdd.getDescription())
            );

            return ResponseEntity.status(HttpStatus.OK).body(response);



        } else {
            response.put("code", HttpStatus.BAD_REQUEST);
            response.put("status", "error");
            response.put("message", "Invalid action. Allowed values: ACCEPT, REJECT.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get-all-account")
    public ResponseEntity<?> getAllAccountByStatus(@RequestParam(defaultValue = "all") String accountStatus,
    @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "8") int pageSize, @RequestParam(defaultValue = "TEACHER") String role) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.OK);
        response.put("status", "ok");
        response.put("require-account-list", userServiceBL.getAllAccountByStatusAndRole(accountStatus, role, page, pageSize));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
