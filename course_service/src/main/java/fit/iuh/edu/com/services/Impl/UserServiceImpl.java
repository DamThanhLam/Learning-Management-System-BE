package fit.iuh.edu.com.services.Impl;

import fit.iuh.edu.com.models.User;
import fit.iuh.edu.com.repositories.UserRepository;
import fit.iuh.edu.com.services.BL.UserServiceBL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserServiceImpl implements UserServiceBL {
    @Autowired
    private UserRepository userRepository;

    @PostAuthorize("returnObject.id == authentication.principal.claims['sub']")
    @Override
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt){
            Map<String, Object> claims = jwt.getClaims();
            return userRepository.getUserById(claims.get("sub").toString());
        }
        return null;
    }
}
