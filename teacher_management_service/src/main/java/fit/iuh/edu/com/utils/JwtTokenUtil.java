package fit.iuh.edu.com.utils;

import fit.iuh.edu.com.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    @SuppressWarnings("ReassignedVariable")
    public String generateToken(JwtEncoder jwtEncoder, User user) {
        String token = "";
        List<SimpleGrantedAuthority> authorities = user.getGroups().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
        try {
            Instant now = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("iuh.fit.se")
                    .issuedAt(now)
                    .expiresAt(generateExpirationDate())
                    .subject(user.getId())
                    .claim("scope", authorities
                            .stream().map(r -> r.getAuthority()).collect(Collectors.toList()))
                    .build();

            token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return token;
    }
    public String generateRefreshToken(JwtEncoder jwtEncoder, User user) {
        String refreshToken = "";
        try {
            Instant now = Instant.now();
            JwtClaimsSet claims = JwtClaimsSet.builder()
                    .issuer("iuh.fit.se")
                    .issuedAt(now)
                    .expiresAt(now.plus(7, ChronoUnit.DAYS)) // Refresh token có thời gian sống lâu hơn
                    .subject(user.getId())
                    .build();

            refreshToken = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        } catch (Exception e) {
            logger.error("Error generating refresh token: " + e.getMessage());
        }
        return refreshToken;
    }
    public String getUsernameFromToken(Jwt jwtToken) {
        return jwtToken.getSubject();
    }
    private boolean isTokenExpired(Jwt jwtToken) {
        return Objects.requireNonNull(jwtToken.getExpiresAt()).isBefore(Instant.now());
    }
    public boolean isTokenValid(Jwt jwtToken, User user) {

        return !isTokenExpired(jwtToken) &&
                user != null &&
                user.getId().equals(getUsernameFromToken(jwtToken));
    }
    public Instant generateExpirationDate() {
        return Instant.now().plus(100, ChronoUnit.MINUTES);
    }
}
