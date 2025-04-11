package fit.iuh.edu.com.services.impl;

import fit.iuh.edu.com.dtos.UserOwnResponse;
import fit.iuh.edu.com.dtos.UserResponseNoAuth;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.repositories.UserRepository;
import fit.iuh.edu.com.services.bl.UserServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserServiceBL {
    @Autowired
    private UserRepository userRepository;



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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            User userDetail = userRepository.find(user.getId());
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
        System.out.println("authentication null");

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
}
