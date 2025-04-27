package lms.payment_service_lms.service.impl;

import lms.payment_service_lms.entity.User;
import lms.payment_service_lms.repositories.UserRepository;
import lms.payment_service_lms.service.UserServices;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServices {
    private final UserRepository userRepository;

    @Override
    public Optional<User> findUserById(String userId) {
        return Optional.ofNullable(userRepository.find(userId));
    }
}
