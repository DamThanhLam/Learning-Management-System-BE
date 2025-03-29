package fit.iuh.edu.com.controller;

import fit.iuh.edu.com.dtos.Login;
import fit.iuh.edu.com.dtos.UserOwnResponse;
import fit.iuh.edu.com.dtos.UserResponseNoAuth;
import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.dtos.StudentRegister;
import fit.iuh.edu.com.models.Account;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.services.CognitoService;
import fit.iuh.edu.com.services.bl.AccountServiceBL;
import fit.iuh.edu.com.services.bl.UserServiceBL;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.util.*;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @Autowired
    private CognitoService cognitoService;
    @Autowired
    private UserServiceBL userServiceBL;

    @Autowired
    private AccountServiceBL accountServiceBL;

    @Autowired
    private Cipher cipherDecrypt;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam String id) {
        UserResponseNoAuth user = userServiceBL.getUserById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("status","success");
        response.put("code", 200);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<?> getUserDetails() {
        Map<String, Object> response = new HashMap<>();
        UserOwnResponse user = userServiceBL.getUser();
        response.put("user", user);
        response.put("status","success");
        response.put("code", 200);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/student/register")
    public ResponseEntity<?> studentRegister(@Valid @RequestBody StudentRegister studentRegister, BindingResult bindingResult) throws IllegalBlockSizeException, BadPaddingException {


        Map<String, Object> response = new HashMap<>();
        if (bindingResult.hasErrors()) {
            response.put("errors", bindingResult.getAllErrors());
            response.put("status","error");
            response.put("code",400);
            return ResponseEntity.ok(response);
        }
        if(!accountServiceBL.beforeRegister(studentRegister.getEmail())){
            response.put("errors", "Email already exist");
            response.put("status","error");
            response.put("code",400);
            return ResponseEntity.ok(response);
        }


        String key = UUID.randomUUID().toString();
        List<String> groups = new ArrayList<>();
        groups.add("STUDENT");
        User user = User.builder()
                .id(key)
                .userName(studentRegister.getUsername())
                .accountStatus(AccountStatus.ACCEPT)
                .groups(groups)
                .build();
        byte[] encryptedBytes = Base64.getDecoder().decode(studentRegister.getPassword());
        byte[] decryptedBytes = cipherDecrypt.doFinal(encryptedBytes);

        Account account = Account.builder()
                .id(key)
                .email(studentRegister.getEmail())
                .password(Arrays.toString(decryptedBytes))
                .build();

        accountServiceBL.addAccount(account);
        userServiceBL.addUser(user);
        response.put("status","success");
        response.put("code",200);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody Login login, BindingResult bindingResult, HttpServletResponse response) throws IllegalBlockSizeException, BadPaddingException {
        Map<String, Object> result = new HashMap<>();
        if (bindingResult.hasErrors()) {
            result.put("errors", bindingResult.getAllErrors());
            result.put("status","error");
            result.put("code",400);
            return ResponseEntity.ok(result);
        }
        byte[] encryptedBytes = Base64.getDecoder().decode(login.getPassword());
        byte[] decryptedBytes = cipherDecrypt.doFinal(encryptedBytes);
        String jwt = accountServiceBL.login(login.getEmail(), Arrays.toString(decryptedBytes));
        Cookie jwtCookie = new Cookie("JWT", jwt);
        jwtCookie.setHttpOnly(true);   // Bảo mật chống XSS
        jwtCookie.setSecure(true);     // Chỉ gửi qua HTTPS (bỏ nếu dev trên localhost)
        jwtCookie.setPath("/");        // Cookie áp dụng cho toàn bộ domain
        jwtCookie.setMaxAge(7 * 24 * 60 * 60); // Hết hạn sau 7 ngày
        System.out.println(jwt);
        response.addCookie(jwtCookie);
        // Trả về phản hồi JSON
        result.put("message", "Login successful!");
        result.put("status", "success");
        result.put("data",userServiceBL.getUser());
        result.put("code", 200);
        return ResponseEntity.ok(result);

    }
}
