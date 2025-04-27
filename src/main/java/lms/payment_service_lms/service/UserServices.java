package lms.payment_service_lms.service;

import lms.payment_service_lms.entity.User;

import java.util.Optional;

public interface UserServices {
    Optional<User> findUserById(String userId);
}
