package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.repositories.UserRepository;
import fit.iuh.edu.com.services.BL.UserServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserServiceBL {

    @Autowired
    private UserRepository userRepository;


    @Override
    public User getUser(String userId) {
        return userRepository.getUserById(userId);
    }



    @Override
    public boolean beforeAddTeacher(String email, String phoneNumber) {
        User user = userRepository.findUserByEmailOrPhoneNumber(email, phoneNumber);
        return user == null;
    }

    @Override
    public User create(User teacherTemp) {
        return userRepository.create(teacherTemp);
    }

    @Override
    public User getTeacherByCognitoId(String cognitoId) {
        return null;
    }

    @Override
    public void lockAndSendEmailTeacherAccount(User teacher) {

    }

    @Override
    public User findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public void delete(User teacherTemp) {
        userRepository.delete(teacherTemp);
    }

    @Override
    public void update(User teacher) {
        userRepository.update(teacher);
    }

    @Override
    public Page<User> getAllAccountByStatus(String status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return userRepository.findUsersByAccountStatus(status, pageable);
    }

}
