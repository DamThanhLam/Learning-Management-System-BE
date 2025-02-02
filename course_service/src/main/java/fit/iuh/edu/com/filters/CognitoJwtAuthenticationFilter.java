package fit.iuh.edu.com.filters;

//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
//
//public class CognitoJwtAuthenticationFilter extends OncePerRequestFilter {
//    @Value("${cognito.publicKey}")
//    private String cognitoPublicKey;
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String authorizationHeader = request.getHeader("Authorization");
//        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
//            String token = authorizationHeader.substring("Bearer ".length());
//            Claims claims = Jwts.parserBuilder().setSigningKey(cognitoPublicKey).build().parseClaimsJws(token).getBody();
//        }
//    }
//}
