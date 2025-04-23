package fit.iuh.edu.com.services.impl;

import fit.iuh.edu.com.dtos.UserOwnResponse;
import fit.iuh.edu.com.dtos.UserResponseNoAuth;
import fit.iuh.edu.com.dtos.UserUpdate;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.repositories.UserRepository;
import fit.iuh.edu.com.services.bl.UserServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class UserServiceImpl implements UserServiceBL {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BucketServiceImpl bucketService;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    @Override
    public boolean studentRegister(User user) {
        return false;
    }

    @Override
    public User getUserDetail() {
        return null;
    }

    @Override
    public UserOwnResponse getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String sub = jwt.getSubject();  // claim "sub"
            User userDetail = userRepository.find(sub);
            return UserOwnResponse.builder()
                    .id(userDetail.getId())
                    .email(userDetail.getEmail())
                    .birthday(userDetail.getBirthday())
                    .contacts(userDetail.getContacts())
                    .cvFile(userDetail.getCvFile())
                    .phoneNumber(userDetail.getPhoneNumber())
                    .gender(userDetail.getGender())
                    .urlImage(userDetail.getUrlImage())
                    .description(userDetail.getDescription())
                    .userName(userDetail.getUserName())
                    .build();
        }

        return null;

    }

    @Override
    public User addUser(User user) {
        return userRepository.create(user);
    }

    @Override
    public UserResponseNoAuth getUserById(String id) {
        User user = userRepository.find(id);
        return UserResponseNoAuth.builder()
                .userName(user.getUserName())
                .contacts(user.getContacts())
                .description(user.getDescription())
                .id(user.getId())
                .build();
    }

    @Override
    public User getById(String id) {
        return userRepository.find(id);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getUserByRole(String role) {
        return userRepository.findByRole(role);
    }

    @Override
    public void update(UserUpdate userUpdate) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getName());
        System.out.println("auth.getName()");
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String sub = jwt.getSubject();  // claim "sub"

            User user = userRepository.find(sub);
            System.out.println(user.getId());

            if (user == null) {
                throw new RuntimeException("User not found");
            }

            if (userUpdate.getUserName() != null && !userUpdate.getUserName().isBlank()) {
                user.setUserName(userUpdate.getUserName());
            }

            if (userUpdate.getEmail() != null && !userUpdate.getEmail().isBlank()) {
                user.setEmail(userUpdate.getEmail());
            }

            if (userUpdate.getPhoneNumber() != null && !userUpdate.getPhoneNumber().isBlank()) {
                user.setPhoneNumber(userUpdate.getPhoneNumber());
            }

            if (userUpdate.getBirthday() != null) {
                user.setBirthday(userUpdate.getBirthday());
            }

            if (userUpdate.getGender() != null) {
                user.setGender(userUpdate.getGender());
            }

            if (userUpdate.getDescription() != null && !userUpdate.getDescription().isBlank()) {
                user.setDescription(userUpdate.getDescription());
            }

            if (userUpdate.getContacts() != null && !userUpdate.getContacts().isEmpty()) {
                user.setContacts(userUpdate.getContacts());
            }

            if (userUpdate.getImageAvt() != null && !userUpdate.getImageAvt().isEmpty()) {
                // Giả sử bạn có một phương thức để xử lý file ảnh và lưu đường dẫn hoặc nội dung
                String imagePath = bucketService.putObjectToBucket(bucketName,userUpdate.getImageAvt(),"files");
                user.setUrlImage(imagePath);

            }

            if (userUpdate.getCvFile() != null && !userUpdate.getCvFile().isEmpty()) {
                String cvPath = bucketService.putObjectToBucket(bucketName,userUpdate.getImageAvt(),"files");
                user.setCvFile(cvPath);
            }

            userRepository.update(user);
        }
    }

}
