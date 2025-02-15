package fit.iuh.edu.com.controller;

import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.services.CognitoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @Autowired
    private CognitoService cognitoService;
    @GetMapping
    public ResponseEntity<?> findById(@RequestParam String id) {
        User user = cognitoService.getUserById(id);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);

        return ResponseEntity.ok(response);
    }
}
