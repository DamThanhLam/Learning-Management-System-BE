package fit.iuh.edu.com.utils;

import fit.iuh.edu.com.models.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SecurityUtils {
    public static void setUserAuthentication(User user) {
        if (user != null) {
            // Chuyển đổi danh sách nhóm thành danh sách quyền (GrantedAuthority)
            List<SimpleGrantedAuthority> authorities = user.getGroups().stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            // Tạo đối tượng authentication
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);

            // Đặt authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
