package fit.iuh.edu.com.controller;

import fit.iuh.edu.com.dtos.*;
import fit.iuh.edu.com.enums.AccountStatus;
import fit.iuh.edu.com.models.Account;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.services.CognitoService;
import fit.iuh.edu.com.services.bl.AccountServiceBL;
import fit.iuh.edu.com.services.bl.UserServiceBL;
import fit.iuh.edu.com.utils.JwtTokenUtil;
import fit.iuh.edu.com.utils.MultipartFileValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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
    @Autowired
    private JwtEncoder jwtEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtDecoder jwtDecoder;

    @GetMapping
    public ResponseEntity<?> findById(@RequestParam String id) {
        UserResponseNoAuth user = userServiceBL.getUserById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("status","success");
        response.put("code", 200);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/own")
    public ResponseEntity<?> getUserDetails() {
        Map<String, Object> response = new HashMap<>();
        UserOwnResponse user = userServiceBL.getUser();
        response.put("user", user);
        response.put("status","success");
        response.put("code", 200);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role")
    public ResponseEntity<?> getUserRole(@RequestParam String role) {
        Map<String, Object> response = new HashMap<>();
        List<User> users = userServiceBL.getUserByRole(role);
        response.put("user", users);
        response.put("status","success");
        response.put("code", 200);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken() {
        Map<String, Object> response = new HashMap<>();
        User user = userServiceBL.getById(SecurityContextHolder.getContext().getAuthentication().getName());
        response.put("status","success");
        response.put("code", 200);
        response.put("data",user);
        return ResponseEntity.ok(response);
    }

    @PutMapping()
    public ResponseEntity<?> update(@Valid UserUpdate userUpdate, BindingResult bindingResult) throws IOException {
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            response.put("errors", bindingResult.getAllErrors());
            response.put("status", "error");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
        if(userUpdate.getBirthday() != null){
            if(!userUpdate.getBirthday().plusYears(18).isAfter(LocalDate.now())){
                response.put("errors", "You must be over 18 years old");
                response.put("status","error");
                response.put("code",400);
                return ResponseEntity.ok(response);
            }
        }
        if(userUpdate.getCvFile() != null){
            try{
                MultipartFileValidator.validateCvFile(userUpdate.getCvFile());
            }catch (Exception e){
                response.put("error s", e.getMessage());
                response.put("status","error");
                response.put("code",400);
                return ResponseEntity.ok(response);
            }
        }
        if(userUpdate.getImageAvt() != null){
            try{
                MultipartFileValidator.validateImageAvatar(userUpdate.getImageAvt());
            }catch (Exception e){
                response.put("errors", e.getMessage());
                response.put("status","error");
                response.put("code",400);
                return ResponseEntity.ok(response);
            }
        }
        userServiceBL.update(userUpdate);
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
                .email(studentRegister.getEmail())
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
            result.put("message", bindingResult.getAllErrors());
            result.put("status","error");
            result.put("code",400);
            return ResponseEntity.ok(result);
        }
        byte[] encryptedBytes = Base64.getDecoder().decode(login.getPassword());
        byte[] decryptedBytes = cipherDecrypt.doFinal(encryptedBytes);
        String password = new String(decryptedBytes, StandardCharsets.UTF_8);
        System.out.println(password);
        String jwt = accountServiceBL.login(login.getEmail(), Arrays.toString(decryptedBytes));
        if(jwt == null){
            result.put("message", "Login fail");
            result.put("status","error");
            result.put("code",400);
            return ResponseEntity.ok(result);
        }
        Account account = accountServiceBL.getAccount(login.getEmail());
        User user = userServiceBL.getById(account.getId());
        String refreshToken = jwtTokenUtil.generateRefreshToken(jwtEncoder, user);

        String cookie = "access_token=" + jwt +
                "; Path=/;" +
                " HttpOnly;" +
                " Max-Age=" + (15 * 60) + ";" +
                    " SameSite=Lax;";
        response.setHeader("Set-Cookie", cookie);

        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(refreshTokenCookie);

        // Trả về phản hồi JSON
        result.put("message", "Login successful!");
        result.put("status", "success");
        result.put("code", 200);
        return ResponseEntity.ok(result);

    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken,  HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        if (refreshToken == null) {
            result.put("message", "Refresh token is missing");
            result.put("status", "error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
        System.out.println("refreshToken: "+refreshToken);

        try {
            Jwt jwt = jwtDecoder.decode(refreshToken);
            String id = jwtTokenUtil.getUsernameFromToken(jwt);

            System.out.println("id "+id);
            User user = userServiceBL.getById(id);

            if (!jwtTokenUtil.isTokenValid(jwt, user)) {
                System.out.println("token is invalid");
                result.put("message", "Invalid refresh token");
                result.put("status", "error");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
            }

            // Tạo Access Token mới
            String newAccessToken = jwtTokenUtil.generateToken(jwtEncoder, user);
            Cookie jwtCookie = new Cookie("access_token", newAccessToken);

            jwtCookie.setHttpOnly(true);   // Bảo mật chống XSS
            jwtCookie.setPath("/");        // Cookie áp dụng cho toàn bộ domain
            jwtCookie.setMaxAge(15*60); //
            response.addCookie(jwtCookie);

            result.put("message", "Token refreshed successfully!");
            result.put("access_token",newAccessToken);
            result.put("status", "success");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            result.put("message", "Invalid refresh token");
            result.put("status", "error");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }
    }

}
