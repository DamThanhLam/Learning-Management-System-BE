package fit.iuh.edu.com.services;

import fit.iuh.edu.com.enums.Gender;
import fit.iuh.edu.com.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CognitoService {
    @Value("${aws.cognito.poolid}")
    private String poolId;
    @Autowired
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;
    public User getUserById(String id) {
        User user = new User();


        AdminListGroupsForUserRequest groupsRequest = AdminListGroupsForUserRequest.builder()
                .userPoolId(poolId)
                .username(id)
                .build();
        AdminListGroupsForUserResponse groupsResponse = cognitoIdentityProviderClient.adminListGroupsForUser(groupsRequest);
        List<String> groupNames = groupsResponse.groups().stream()
                .map(GroupType::groupName)
                .collect(Collectors.toList());
        user.setGroups(groupNames);

        AdminGetUserRequest adminGetUserRequest = AdminGetUserRequest.builder()
                .userPoolId(poolId)
                .username(id)
                .build();
        AdminGetUserResponse adminGetUserResponse = cognitoIdentityProviderClient.adminGetUser(adminGetUserRequest);
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
                user.setGender(Gender.valueOf(attribute.value()));
            }
        }
        user.setId(adminGetUserResponse.username());
        return user;
    }
}
