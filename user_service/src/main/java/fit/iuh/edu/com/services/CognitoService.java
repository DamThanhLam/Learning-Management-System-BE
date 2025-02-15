package fit.iuh.edu.com.services;

import fit.iuh.edu.com.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminGetUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class CognitoService {
    @Value("${aws.cognito.poolid}")
    private String poolId;
    @Autowired
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;
    public User getUserById(String id) {
        AdminGetUserRequest adminGetUserRequest = AdminGetUserRequest.builder()
                .userPoolId(poolId)
                .username(id)
                .build();
        AdminGetUserResponse adminGetUserResponse = cognitoIdentityProviderClient.adminGetUser(adminGetUserRequest);
        User user = new User();
        // Lấy danh sách thuộc tính
        List<AttributeType> attributes = adminGetUserResponse.userAttributes();

        for (AttributeType attribute : attributes) {
            if ("email".equals(attribute.name())) {
                user.setEmail(attribute.value());
            }
            if ("name".equals(attribute.name())) {
                user.setUserName(attribute.value());
            }
            if ("birthdate".equals(attribute.name())) {
                user.setBirthday( LocalDate.parse(attribute.value(), DateTimeFormatter.ofPattern("yyyy-dd-MM")));
            }
            if ("gender".equals(attribute.name())) {
                user.setGender(attribute.value());
            }
        }
        user.setId(adminGetUserResponse.username());
        return user;
    }
}
