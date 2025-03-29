package fit.iuh.edu.com.services.impl;

import fit.iuh.edu.com.dtos.UserOwnResponse;
import fit.iuh.edu.com.dtos.UserResponseNoAuth;
import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.repositories.UserRepository;
import fit.iuh.edu.com.services.bl.UserServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.find(id);
        return UserOwnResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .birthday(user.getBirthday())
                .contacts(user.getContacts())
                .cvFile(user.getCvFile())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .urlImage(user.getUrlImage())
                .description(user.getDescription())
                .userName(user.getUserName())
                .build();
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
}
