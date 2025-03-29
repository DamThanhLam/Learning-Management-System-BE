package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.services.BL.CognitoServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminAddUserToGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;

import java.util.ArrayList;
import java.util.Collection;

@Service
public class CognitoServiceImpl implements CognitoServiceBL {
    @Value("${aws.cognito.poolid}")
    private String poolId;
    @Autowired
    private CognitoIdentityProviderClient cognitoIdentityProviderClient;

    @Override
    public String createTeacher(User teacher) {
        Collection<AttributeType> attributeTypes = new ArrayList<>();
        attributeTypes.add(AttributeType.builder().name("name").value(teacher.getUserName()).build());
        attributeTypes.add(AttributeType.builder().name("email").value(teacher.getEmail()).build());
        attributeTypes.add(AttributeType.builder().name("birthdate").value(teacher.getBirthday().toString()).build());
        attributeTypes.add(AttributeType.builder().name("gender").value(teacher.getGender().toString()).build());

        AdminCreateUserResponse adminCreateUserResponse = cognitoIdentityProviderClient.adminCreateUser(builder ->
                builder.userAttributes(attributeTypes)
                        .temporaryPassword("Lms@1111")
                        .username(teacher.getEmail())
                        .userPoolId(poolId)
                );
        AdminAddUserToGroupRequest adminAddUserToGroupRequest = AdminAddUserToGroupRequest
                .builder()
                .groupName("TEACHER")
                .userPoolId(poolId)
                .username(adminCreateUserResponse.user().username())
                .build();
        cognitoIdentityProviderClient.adminAddUserToGroup(adminAddUserToGroupRequest);
        return adminCreateUserResponse.user().username();
    }
}
